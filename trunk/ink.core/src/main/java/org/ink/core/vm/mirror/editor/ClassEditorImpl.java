package org.ink.core.vm.mirror.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ink.core.vm.exceptions.CompilationException;
import org.ink.core.vm.exceptions.WeaveException;
import org.ink.core.vm.factory.internal.CoreNotations;
import org.ink.core.vm.lang.ObjectFactoryState;
import org.ink.core.vm.lang.Property;
import org.ink.core.vm.lang.PropertyState;
import org.ink.core.vm.lang.internal.ClassMirrorAPI;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.traits.Trait;
import org.ink.core.vm.traits.TraitClass;


/**
 * @author Lior Schachter
 */
public class ClassEditorImpl<S extends ClassEditorState> extends ObjectEditorImpl<S> implements ClassEditor{

	private static final String PROPERTY_NAME_DELIMITER = ".";


	@Override
	public void setFactory(ObjectFactoryState factoryState) {
		((ClassMirrorAPI)workOnObject).setFactory(factoryState);
	}

	@Override
	public void weaveDetachableTrait(Trait trait) throws WeaveException{
		TraitClass traitClass = trait.getMeta();
		String role = traitClass.getRole();
		injectProperties(role, traitClass, false);
		((ClassMirrorAPI)workOnObject).addRole(trait.reflect().getNamespace(), role, trait);
	}

	private void injectProperties(String role, TraitClass traitClass, boolean isStructural) throws WeaveException{
		ClassMirrorAPI classObject = (ClassMirrorAPI)workOnObject;
		String classNS = classObject.getNamespace();
		String traitNS = traitClass.reflect().getNamespace();
		if(!isStructural && classObject.hasRole(traitNS, role)){
			throw new WeaveException("The class '" + workOnObject.getId() +"', already contains the role '" + role +"'.");
		}
		boolean addNS = !isStructural & !traitNS.equals(classNS);
		List<PropertyState> propertiesToInject = collectProperties(role, traitClass, addNS, traitNS);
		Map<String, PropertyState> injectedPropertiesMap = new HashMap<String, PropertyState>(propertiesToInject.size());
		for(PropertyState propState : propertiesToInject){
			injectedPropertiesMap.put(propState.getName(), propState);
		}
		List<? extends PropertyMirror> superPropMirrors = ((ClassMirror)traitClass.reflect().getSuper()).getAllProperties();
		List<Property> newProperties = new ArrayList<Property>(superPropMirrors.size());
		PropertyMirror[] existingProperties =  classObject.getClassPropertiesMirrors();
		ClassMirrorAPI superClass = classObject.getSuper();
		Property newProperty;
		Map<String, PropertyMirror> propertiesMap = new HashMap<String, PropertyMirror>();
		if(superClass!=null){
			PropertyMirror[] superProperties = superClass.getClassPropertiesMirrors();
			for(PropertyMirror propMirror : existingProperties){
				propertiesMap.put(propMirror.getName(), propMirror);
			}
			String name;
			//to keep montonicity - first go through the super properties
			for(PropertyMirror propMirror : superProperties){
				name = propMirror.getName();
				newProperty = getExistingProperty(name, traitNS, propertiesMap);
				if(newProperty==null){
					//check in the injected properties
					newProperty = getTraitProperty(name, traitNS, injectedPropertiesMap);
					if(newProperty==null){
						//we arrive here if there are injected properties in the super
						//that were still not resolved in the sub-class (but should be soon...)
						newProperty = propMirror.getTargetBehavior().cloneState().getBehavior();
					}
				}
				newProperties.add(newProperty);
			}
		}
		//add original properties + trait properties
		for(PropertyMirror propMirror : existingProperties){
			if(propertiesMap.containsKey(propMirror.getName())){
				newProperty = propMirror.getTargetBehavior();
				newProperties.add(newProperty);
			}
		}
		for(PropertyState propState : propertiesToInject){
			if(injectedPropertiesMap.containsKey(propState.getName())){
				newProperties.add((Property) propState.getBehavior());
			}
		}
		classObject.applyProperties(newProperties);
	}

	@Override
	public void weaveStructuralTrait(String role, TraitClass traitClass) throws WeaveException{
		injectProperties(role, traitClass, true);
	}

	private Property getExistingProperty(String name, String traitNS, Map<String, PropertyMirror> existingProperties){
		PropertyMirror result = existingProperties.remove(name);
		if(result==null){
			result = existingProperties.remove(traitNS + PROPERTY_NAME_DELIMITER + name);
		}
		return result==null?null:(Property)result.getTargetBehavior();
	}

	private Property getTraitProperty(String name, String traitNS, Map<String, PropertyState> traitProperties){
		PropertyState result =traitProperties.remove(name);
		if(result==null){
			result = traitProperties.remove(traitNS + PROPERTY_NAME_DELIMITER + name);
		}
		return result==null?null:(Property)result.getBehavior();
	}

	@Override
	public void compile()  throws CompilationException{
		String superState = workOnObject.getSuperId();
		if(superState==null){
			workOnObject.setSuperId(CoreNotations.Ids.INK_OBJECT);
		}
		super.compile();
	}

	private List<PropertyState> collectProperties(String role, TraitClass traitClass, boolean addNS, String traitNS){
		List<? extends Property> traitProperties = traitClass.getInjectedTargetProperties();
		List<PropertyState> injectedProperties = new ArrayList<PropertyState>(traitProperties.size());
		PropertyState state;
		String propertyName;
		for(Property traitProperty : traitProperties){
			state = traitProperty.cloneState();
			propertyName = role+PROPERTY_NAME_DELIMITER+state.getName();
			if(addNS){
				propertyName = traitNS+PROPERTY_NAME_DELIMITER + propertyName;
			}
			state.setName(propertyName);
			injectedProperties.add(state);
		}
		return injectedProperties;
	}

}
