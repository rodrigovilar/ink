package org.ink.core.vm.test.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.ink.core.vm.constraints.ValidationContext;
import org.ink.core.vm.exceptions.WeaveException;
import org.ink.core.vm.factory.Context;
import org.ink.core.vm.factory.DslFactoryEventDispatcher;
import org.ink.core.vm.factory.DslFactoryState;
import org.ink.core.vm.factory.InkVM;
import org.ink.core.vm.factory.VMMain;
import org.ink.core.vm.factory.internal.CoreNotations;
import org.ink.core.vm.lang.InkClass;
import org.ink.core.vm.lang.InkClassState;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.JavaMapping;
import org.ink.core.vm.lang.Property;
import org.ink.core.vm.lang.PropertyState;
import org.ink.core.vm.lang.internal.ClassMirrorAPI;
import org.ink.core.vm.lang.internal.MirrorAPI;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.mirror.editor.ClassEditor;
import org.ink.core.vm.mirror.editor.ObjectEditor;
import org.ink.core.vm.modelinfo.ModelInfoFactory;
import org.ink.core.vm.modelinfo.ModelInfoRepository;
import org.ink.core.vm.modelinfo.relations.ExtendsRelation;
import org.ink.core.vm.modelinfo.relations.IsInstanceOfRelation;
import org.ink.core.vm.traits.Trait;
import org.ink.core.vm.traits.TraitClass;
import org.ink.core.vm.traits.TraitClassState;
import org.ink.core.vm.traits.TraitState;
import org.ink.core.vm.types.PrimitiveType;
import org.ink.core.vm.types.PrimitiveTypeMarker;
import org.ink.core.vm.utils.property.LongAttributeState;
import org.ink.core.vm.utils.property.Reference;
import org.ink.core.vm.utils.property.ReferenceState;
import org.ink.core.vm.utils.property.StringAttributeState;


/**
 * @author Lior Schachter
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class CoreBasicTests extends TestCase{

	private final Context context = InkVM.instance().getContext();

	public void testObjectRetrival(){
		InkClass o = context.getObject(CoreNotations.Ids.INK_OBJECT);
		assertNotNull(o);
		o = context.getObject(CoreNotations.Ids.INK_CLASS);
		assertNotNull(o);
		ClassMirror mirror = o.reflect();
		assertNotNull(mirror);
		assertNotNull(mirror.getClassPropertyMirror(InkClassState.p_java_path));
		PrimitiveType pt = context.getObject(CoreNotations.Ids.LONG);
		assertNotNull(pt);
		assertTrue(pt.isNumeric());
		assertTrue(pt.getPrimitiveMarker()==PrimitiveTypeMarker.Long);
	}

	public void testObjectCreation(){
		InkClass referenceAttClass = context.getObject(CoreNotations.Ids.REFERENCE);
		InkClassState referenceType = context.getState(CoreNotations.Ids.PROPERTY);
		assertNotNull(referenceAttClass);
		ReferenceState referenceAttribute = (ReferenceState) referenceAttClass.newInstance();
		referenceAttribute.setName("myObject");
		referenceAttribute.setType(referenceType);
		assertNotNull(referenceAttribute);
		Reference refObject = (Reference) referenceAttribute.getBehavior();
		assertNotNull(refObject);
		Mirror mirror = refObject.reflect();
		assertNotNull(mirror);
		assertNotNull(referenceAttribute.getType());
		assertNotNull(referenceAttribute.getName());
		assertEquals(referenceType.getId(), referenceAttribute.getType().reflect().getId());
		InkClass containedRef = referenceAttribute.getType();
		Mirror containedRefMirror = containedRef.reflect();
		assertTrue(mirror==containedRefMirror.getOwner());
		assertTrue(containedRefMirror.getDefiningPropertyIndex()==ReferenceState.p_type);
	}

	public void testTraitCreation(){
		DslFactoryEventDispatcher listener = context.getTargetBehavior().asTrait(DslFactoryState.t_event_dispatcher);
		assertNotNull(listener);
	}

	@SuppressWarnings("unchecked")
	public void testOwner(){
		InkClass stringAttClass = context.getObject(CoreNotations.Ids.STRING_ATTRIBUTE);
		ClassMirror mirror = stringAttClass.reflect();
		assertTrue(mirror==((InkObject)mirror.getPropertyValue(InkClassState.p_personality)).reflect().getOwner());
		assertTrue(InkClassState.p_personality==((InkObject)mirror.getPropertyValue(InkClassState.p_personality)).reflect().getDefiningPropertyIndex());
		Map<String, Property> properties = (Map<String, Property>)mirror.getPropertyValue(InkClassState.p_properties);
		assertNotNull(properties);
		assertFalse(properties.isEmpty());
		for(Property prop : properties.values()){
			assertTrue(mirror==prop.reflect().getOwner());
			assertTrue(InkClassState.p_properties==prop.reflect().getDefiningPropertyIndex());
		}
		//test MirrorProxy
		Property prop = properties.values().iterator().next();
		InkObject type = prop.getType();
		assertTrue(type.reflect().getOwner().getOwner()==mirror);
		assertTrue(type.reflect().getDefiningPropertyIndex()==PropertyState.p_type);
	}

	public void testDefaultValue(){
		InkClass inkClass = context.getObject(CoreNotations.Ids.INK_CLASS);
		assertNotNull(inkClass);
		InkClassState o = (InkClassState) inkClass.newInstance();
		assertNotNull(o.getComponentType());
		assertTrue(o.getJavaMapping().hasBehavior());
	}

	public void testClone(){
		InkClass tClass = context.getObject(CoreNotations.Ids.INK_CLASS);
		assertNotNull(tClass.cloneState());
	}

	public void testFinalValue(){
		InkClass stringAttClass = context.getObject(CoreNotations.Ids.STRING_ATTRIBUTE);
		PrimitiveType stringType = context.getObject(CoreNotations.Ids.STRING);
		assertNotNull(stringAttClass);
		StringAttributeState stringAttribute = (StringAttributeState) stringAttClass.newInstance();
		assertNotNull(stringAttribute.getType());
		assertEquals(stringAttribute.getType().reflect().getId(), stringType.reflect().getId());
	}

	public void testPrintElements() throws IOException{
		String filePath = System.getProperty("java.io.tmpdir") + File.separator + "ink_core_elements.txt";
		File output = new File(filePath);
		if(output.exists()){
			output.delete();
		}
		context.getFactory().printElements(filePath);
		assertTrue(output.exists());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testStructuralTraitWeave() throws WeaveException{
		InkClass tClass = context.getObject(CoreNotations.Ids.MIRROR);
		TraitClassState newTrait = tClass.cloneState();
		ClassMirror cMirror = newTrait.reflect();
		InkClass targetClass = context.getObject(CoreNotations.Ids.STRING_ATTRIBUTE);
		StringAttributeState stringState = targetClass.newInstance();
		stringState.setName("kuku");
		stringState.setDefaultValue("kuku");
		ClassEditor cEditor = cMirror.edit();
		cEditor.setPropertyValue(TraitClassState.p_java_mapping, JavaMapping.No_Java);
		List injecteddProps = new ArrayList();
		injecteddProps.add(stringState);
		cEditor.setPropertyValue(TraitClassState.p_injected_properties, injecteddProps);
		assertTrue(((MirrorAPI)injecteddProps.get(0)).getCachedTrait((byte)0)==null);
		cMirror.edit().save();
		ClassMirror targetMirror = targetClass.reflect();
		ClassEditor targetEditor = targetMirror.edit();
		targetEditor.weaveStructuralTrait("stam_role", (TraitClass)newTrait.getBehavior());
		PropertyMirror[] mirrors = targetMirror.getClassPropertiesMirrors();
		assertTrue(mirrors[mirrors.length-1].getName().equals("stam_role.kuku"));
		InkObjectState newInstance = targetClass.newInstance();
		assertNotNull(newInstance.reflect().getPropertyValue("stam_role.kuku"));
		//test weave on a sub-class
		//create the super-class
		InkClassState superClass = targetClass.cloneState();
		((ClassMirrorAPI)superClass).setJavaMapping(JavaMapping.No_Java);
		((ClassMirrorAPI)superClass).setSuper(context.getState(CoreNotations.Ids.STRING_ATTRIBUTE));
		((ClassMirrorAPI)superClass).afterPropertiesSet();
		targetMirror = superClass.reflect();
		targetEditor = targetMirror.edit();
		targetEditor.weaveStructuralTrait("stam_role", (TraitClass)newTrait.getBehavior());
		//create the sub class
		InkClassState subClass = (InkClassState) superClass.reflect().edit().createDescendent(null).getEditedState();
		((ClassMirrorAPI)subClass).setJavaMapping(JavaMapping.No_Java);

		((ClassMirrorAPI)subClass).afterPropertiesSet();
		//assemble properties
		StringAttributeState stringState2 = stringState.cloneState();
		Map props = new HashMap();
		Map<String, ? extends Property> existingProperties =  subClass.getProperties();
		for(Property prop : existingProperties.values()){
			props.put(prop.getName(), prop.cloneState());
		}
		props.put(stringState2.getName(), stringState2);
		subClass.setProperties(props);
		((ClassMirrorAPI)subClass).afterPropertiesSet();
		//weave
		targetMirror = subClass.reflect();
		targetEditor = targetMirror.edit();
		targetEditor.weaveStructuralTrait("stam_role", (TraitClass)newTrait.getBehavior());
		newInstance = ((InkClass)subClass.getBehavior()).newInstance();
		assertNotNull(newInstance.reflect().getPropertyValue("stam_role.kuku"));
		assertNotNull(newInstance.reflect().getPropertyValue("kuku"));
		newInstance.reflect().edit().setPropertyValue("kuku", "kuku");
		assertTrue(newInstance.reflect().getPropertyValue("kuku").equals("kuku"));
		newInstance.reflect().edit().setPropertyValue("stam_role.kuku", "stam_role.kuku");
		assertTrue(newInstance.reflect().getPropertyValue("stam_role.kuku").equals("stam_role.kuku"));
		byte ind = newInstance.reflect().getPropertyMirror("kuku").getIndex();
		byte ind2 = newInstance.reflect().getPropertyMirror("stam_role.kuku").getIndex();
		assertTrue(ind==ind2+1);
		assertTrue(ind2==((ClassMirror)superClass.reflect()).getClassPropertyMirror("stam_role.kuku").getIndex());
	}




	public void testDetachableTraitWeave() throws WeaveException{
		InkClass tClass = context.getObject(CoreNotations.Ids.PRIMITIVE_ATTRIBUTE_MIRROR);
		TraitClassState newTrait = tClass.cloneState();
		newTrait.setRole("stam_role");
		ClassMirror cMirror = newTrait.reflect();
		ClassMirrorAPI targetClassState = context.getState(CoreNotations.Ids.LONG_ATTRIBUTE);
		InkClass targetClass = targetClassState.getBehavior();
		LongAttributeState longState = targetClass.newInstance();
		longState.setName("kuku");
		longState.setDefaultValue(4l);
		ClassEditor cEditor = cMirror.edit();
		cEditor.setPropertyValue(TraitClassState.p_java_mapping, JavaMapping.No_Java);
		List injecteddProps = new ArrayList();
		injecteddProps.add(longState);
		cEditor.setPropertyValue(TraitClassState.p_injected_properties, injecteddProps);
		assertTrue(((MirrorAPI)injecteddProps.get(0)).getCachedTrait((byte)0)==null);
		cEditor.save();
		TraitClass newTraitBehavior = newTrait.getBehavior();
		TraitState traitState = newTraitBehavior.newInstance();
		ClassMirror targetMirror = targetClass.reflect();
		ClassEditor targetEditor = targetMirror.edit();
		Trait t = traitState.getBehavior();
		targetEditor.weaveDetachableTrait(t);
		PropertyMirror[] mirrors = targetMirror.getClassPropertiesMirrors();
		assertTrue(mirrors[mirrors.length-1].getName().equals("stam_role.kuku"));
		InkObjectState newInstance = targetClass.newInstance();
		assertNotNull(newInstance.reflect().getPropertyValue("stam_role.kuku"));
		assertNotNull(newInstance.asTrait("stam_role"));
		assertNotNull(newInstance.asTrait(newTraitBehavior));
		boolean exception = false;
		try{
			targetEditor.weaveDetachableTrait((Trait)traitState.getBehavior());
		}catch(WeaveException e){
			exception = true;
		}
		assertTrue(exception);
		exception = false;
		try{
			newTrait.setRole("reflection");
			targetEditor.weaveDetachableTrait((Trait)traitState.getBehavior());
		}catch(WeaveException e){
			exception = true;
			newTrait.setRole("stam_role.kuku");
		}
		assertTrue(exception);
		exception = false;
		try{
			newTrait.setRole("stam_role2.kuku2");
			targetEditor.weaveDetachableTrait((Trait)traitState.getBehavior());
		}catch(WeaveException e){
			//currently not allowing 2 roles of the same class
			exception = true;
			newTrait.setRole("stam_role.kuku");
		}
		assertTrue(exception);
		exception = false;
		try{
			targetClassState.addRole(targetClassState.getNamespace(),"stam_role.kuku", (Trait)traitState.getBehavior());
		}catch(WeaveException e){
			exception = true;
		}
		assertTrue(exception);
		exception = false;
		try{
			targetClassState.addRole(targetClassState.getNamespace(),"reflection", (Trait)traitState.getBehavior());
		}catch(WeaveException e){
			exception = true;
		}
		assertTrue(exception);
	}

	public void testValidateCoreElements(){
		ValidationContext vc = ((InkClass)context.getObject(CoreNotations.Ids.VALIDATION_CONTEXT)).newInstance().getBehavior();
		context.getFactory().validateAllElements(vc);
		assertFalse(vc.containsError());
	}

	public void testValidationFramework(){
		InkClass stringAttClass = context.getObject(CoreNotations.Ids.STRING_ATTRIBUTE);
		StringAttributeState stringAttribute = (StringAttributeState) stringAttClass.newInstance();
		ValidationContext vc = ((InkClass)context.getObject(CoreNotations.Ids.VALIDATION_CONTEXT)).newInstance().getBehavior();
		stringAttribute.reflect().edit().setPropertyValue(StringAttributeState.p_max_length, "6");
		stringAttribute.getBehavior().validate(vc);
		assertTrue(vc.containsError());
		assertTrue(vc.getMessages().size()==2);
		assertTrue(vc.getMessages().get(0).getFormattedMessage().equals("The field 'name' should not be empty."));
		assertTrue(vc.getMessages().get(1).getFormattedMessage().equals("The field 'max_length' is of wrong type; expected 'java.lang.Integer', actual 'java.lang.String'."));
	}

	public void testStringAttributeValidation(){
		InkClass loaderClass = context.getObject(CoreNotations.Ids.DSL_LOADER);
		InkClassState newLoaderClass = loaderClass.cloneState();
		ClassMirror cMirror = newLoaderClass.reflect();
		ClassEditor cEditor = cMirror.edit();
		cEditor.setSuper(context.getState(CoreNotations.Ids.DSL_LOADER));
		InkClass stringAttClass = context.getObject(CoreNotations.Ids.STRING_ATTRIBUTE);
		StringAttributeState stringState = stringAttClass.newInstance();
		stringState.setName("kuku");
		stringState.setDefaultValue("kuku");
		stringState.setMinLength(5);
		stringState.setMaxLength(10);
		stringState.setRegExp(".*");
		Map props = new HashMap();
		props.put(stringState.getName(), stringState);
		newLoaderClass.setJavaMapping(JavaMapping.No_Java);
		newLoaderClass.setProperties(props);
		cEditor.save();
		InkObjectState state = ((InkClass)newLoaderClass.getBehavior()).newInstance();
		ValidationContext vc = ((InkClass)context.getObject(CoreNotations.Ids.VALIDATION_CONTEXT)).newInstance().getBehavior();
		state.getBehavior().validate(vc);
		assertTrue(vc.containsError());
		assertTrue(vc.getMessages().size()==1);
		assertTrue(vc.getMessages().get(0).getFormattedMessage().equals("The value length of field 'kuku' should be at least 5 characters."));
		ObjectEditor stateEditor = state.reflect().edit();
		stateEditor.setPropertyValue("kuku", "kukukukukukukukukukuku");
		vc.reset();
		state.getBehavior().validate(vc);
		assertTrue(vc.containsError());
		assertTrue(vc.getMessages().size()==1);
		assertTrue(vc.getMessages().get(0).getFormattedMessage().equals("The value length of field 'kuku' should be at most 10 characters."));
		stateEditor.setPropertyValue("kuku", "kukuku");
		vc.reset();
		state.getBehavior().validate(vc);
		assertFalse(vc.containsError());
	}

	public void testModelInfoRepository(){
		ModelInfoRepository repo = ModelInfoFactory.getInstance();

		InkObject mirror = context.getObject(CoreNotations.Ids.MIRROR);
		InkObject trait = context.getObject(CoreNotations.Ids.TRAIT);
		InkObject traitClass = context.getObject(CoreNotations.Ids.TRAIT_CLASS);
		Collection<Mirror> referrers = repo.findReferrers(trait.reflect(), ExtendsRelation.getInstance(), false);
		assertTrue(referrers != null);
		assertTrue(referrers.contains(mirror.reflect()));
		referrers = repo.findReferrers(traitClass.reflect(), IsInstanceOfRelation.getInstance(), false);
		assertTrue(referrers != null);
		assertTrue(referrers.contains(mirror.reflect()));
		InkObject inkClass = context.getObject(CoreNotations.Ids.INK_CLASS);
		InkObject dslFactory = context.getObject(CoreNotations.Ids.DSL_FACTORY);
		referrers = repo.findReferrers(inkClass.reflect(), ExtendsRelation.getInstance(), false);
		assertTrue(referrers != null);
		assertTrue(referrers.contains(dslFactory.reflect()));
		assertTrue(referrers.contains(traitClass.reflect()));

		// Test recursive
		InkObject classMirror = context.getObject(CoreNotations.Ids.CLASS_MIRROR);
		referrers = repo.findReferrers(inkClass.reflect(), IsInstanceOfRelation.getInstance(), false);
		assertTrue(referrers != null);
		assertTrue(!referrers.contains(classMirror.reflect()));
		referrers = repo.findReferrers(inkClass.reflect(), IsInstanceOfRelation.getInstance(), true);
		assertTrue(referrers != null);
		assertTrue(referrers.contains(classMirror.reflect()));

		// Test inner vs. root objects
		InkObject canCacheBehaviorInstanceProperty = ((Map<String, InkObject>) dslFactory.reflect().getPropertyValue(InkClassState.p_properties)).get("can_cache_behavior_instance");
		referrers = repo.findReferrers(context.getObject(CoreNotations.Ids.BOOLEAN_ATTRIBUTE).reflect(), IsInstanceOfRelation.getInstance(), false);
		assertTrue(referrers != null);
		assertTrue(referrers.contains(canCacheBehaviorInstanceProperty.reflect()));
		assertTrue(!referrers.contains(dslFactory.reflect()));
	}

	public void testVMRestart(){
		VMMain.restart();
		InkObject mirror = InkVM.instance().getContext().getObject(CoreNotations.Ids.MIRROR);
		assertNotNull(mirror);
	}

	public void testInkConvention(){

		ink(org.ink.core.vm.lang.InkObject.class);

		// core.vm.lang
		ink(org.ink.core.vm.lang.InkType.class);
		ink(org.ink.core.vm.lang.ObjectFactory.class);
		//ink(org.ink.core.vm.lang.InkClass.class);
		ink(org.ink.core.vm.lang.Property.class);

		// core.vm.factory
		ink(org.ink.core.vm.factory.Context.class);
		ink(org.ink.core.vm.factory.DslFactory.class);
		//ink(org.ink.core.vm.factory.DslFactoryTraits.class);
		ink(org.ink.core.vm.factory.DslLoader.class);
		ink(org.ink.core.vm.factory.DslRepository.class);

		// core.vm.factory.internal
		//ink(org.ink.core.vm.factory.internal.CoreClassDescriptor.class);
		//ink(org.ink.core.vm.factory.internal.CoreLoader.class);
		//ink(org.ink.core.vm.factory.internal.CoreObjectDescriptor.class);
		//ink(org.ink.core.vm.factory.internal.ObjectDescriptor.class);

		// core.vm.lang.mirror
		ink(org.ink.core.vm.mirror.ClassMirror.class);
		ink(org.ink.core.vm.mirror.Mirror.class);
		ink(org.ink.core.vm.mirror.StructClassMirror.class);
		ink(org.ink.core.vm.mirror.TraitMirror.class);

		ink(org.ink.core.vm.mirror.editor.ObjectEditor.class);
		//ink(org.ink.core.vm.lang.mirror.editor.TransactionalObjectEditor.class);
		ink(org.ink.core.vm.lang.operation.Operation.class);



	}

	private void ink(Class<?> base){
		final String impl = "Impl";
		final String stat = "State";
		final String data = "Data";
		final String sep = ".";
		final String baseName = base.getCanonicalName();
		final String baseImplName = baseName + impl;
		final String baseStatName = baseName + stat;

		//base is an interface?
		assertTrue(base.isInterface());

		Class<?> baseImpl = null;
		try {
			baseImpl = Class.forName(baseImplName);
		} catch (ClassNotFoundException e) {
			System.out.println("Missing " + baseImplName);
			return;
		}
		assertNotNull(baseImpl);
		Class<?> baseState = null;
		try {
			baseState = Class.forName(baseStatName);
		} catch (ClassNotFoundException e) {
			System.out.println("Missing " + baseStatName);
		}
		assertNotNull(baseState);
		assertTrue(baseState.getDeclaredClasses().length==1);
		Class<?> baseData = baseState.getDeclaredClasses()[0];
		assertNull(base.getSuperclass()); // no superclass
		//Lior - sometimes an interface can extend Comaprable, Serializable etc...
		assertTrue(base.getInterfaces().length>=1);
		Class<?> zuper = base.getInterfaces()[0];
		Class<?> zuperImpl = baseImpl.getSuperclass();
		final String zuperName = zuper.getCanonicalName();
		final String zuperImplName = zuperImpl.getCanonicalName();
		final String zuperImplNameExp = zuperName + impl;

		//baseImpl extends correctly?
		if (!zuperImplName.equals(zuperImplNameExp)) {
			System.out.println(baseImplName
					+ ": Expected to extend " + zuperImplNameExp
					+ " but extending instead " + zuperImplName);
		}
		final boolean isInkObject = base==InkObject.class;
		assertTrue(isInkObject || zuperImplName.equals(zuperImplNameExp));

		//baseImpl implements correctly?
		//assertTrue(baseImpl.getInterfaces().length==1);
		Class<?> zuperInterface = baseImpl.getInterfaces()[0];
		final String zuperInterfaceName = zuperInterface.getCanonicalName();
		assertTrue(zuperInterfaceName.equals(baseName));

		//baseState is an interface?
		assertTrue(baseState.isInterface());

		// baseState and baseState.Data extends correctly?
		Class<?> zuperData = baseData.getSuperclass();
		assertNotNull(zuperData);
		assertNull(baseState.getSuperclass()); // no superclass
		Class<?> zuperState = zuperData.getEnclosingClass();
		if (!isInkObject){
			assertNotNull(zuperState);
			final String zuperStateName = zuperState.getCanonicalName();
			final String zuperStateNameExp = zuperName + stat;
			final String zuperDataName = zuperData.getCanonicalName();
			assertTrue(zuperDataName.equals(zuperStateName + sep + data));
			if (!zuperStateName.equals(zuperStateNameExp)) {
				System.out.println("Expected " + zuperStateNameExp
						+ " but found " + zuperState);
			}
			assertTrue(zuperStateName.equals(zuperStateNameExp));
		}
		//		if (false) {
		//			System.out.println(base);
		//			System.out.println(baseImpl);
		//			System.out.println(baseState);
		//			System.out.println(baseData);
		//			System.out.println(zuper);
		//			System.out.println(zuperImpl);
		//			System.out.println(zuperState);
		//			System.out.println(zuperData);
		//		}
	}

}



