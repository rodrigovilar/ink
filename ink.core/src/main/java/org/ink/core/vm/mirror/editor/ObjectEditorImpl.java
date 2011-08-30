package org.ink.core.vm.mirror.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.ink.core.vm.exceptions.CompilationException;
import org.ink.core.vm.lang.DataTypeMarker;
import org.ink.core.vm.lang.InkClassState;
import org.ink.core.vm.lang.InkObjectImpl;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.Property;
import org.ink.core.vm.lang.Scope;
import org.ink.core.vm.lang.internal.MirrorAPI;
import org.ink.core.vm.lang.property.mirror.CollectionPropertyMirror;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.proxy.Proxiability;
import org.ink.core.vm.proxy.Proxiable;
import org.ink.core.vm.utils.CoreUtils;
import org.ink.core.vm.utils.property.mirror.ListPropertyMirror;
import org.ink.core.vm.utils.property.mirror.MapPropertyMirror;

/**
 * @author Lior Schachter
 */
public class ObjectEditorImpl<S extends ObjectEditorState> extends
		InkObjectImpl<S> implements ObjectEditor{

	public MirrorAPI editedObject;
	public MirrorAPI workOnObject;

	protected MirrorAPI getWorkOnObject() {
		return this.workOnObject;
	}

	@Override
	public ObjectEditor startEdit(InkObjectState object) {
		return startEdit(object, false);
	}

	@Override
	public ObjectEditor startEdit(InkObjectState object, boolean transactional) {
		if (transactional) {
			this.editedObject = (MirrorAPI) object;
			this.workOnObject = (MirrorAPI) object.cloneState();
		} else {
			this.editedObject = this.workOnObject = (MirrorAPI) object;
		}
		return this;
	}

	@Override
	public void init(InkClassState cls) {
		workOnObject.init(cls);
	}

	@Override
	public void setAbstract(boolean isAbstract) {
		workOnObject.setAbstract(isAbstract);
	}

	@Override
	public void setId(String id) {
		workOnObject.setId(id);
	}

	@Override
	public void setOwner(InkObjectState owner) {
		workOnObject.setOwner(owner);
	}

	@Override
	public void setSuper(InkObjectState theSuperObject) {
		workOnObject.setSuper(theSuperObject);
	}

	@Override
	public void setSuperId(String id) {
		workOnObject.setSuperId(id);
	}

	@Override
	public void setPropertyValue(String propertyName, Object value) {
		setPropertyValue(propertyName, value, true);
	}

	@Override
	public void setPropertyValue(byte index, Object value) {
		setPropertyValue(index, value, true);
	}

	@Override
	public void setPropertyValue(String propertyName, Object value,
			boolean override) {
		setPropertyValue(workOnObject.getPropertyIndex(propertyName), value, override);
	}

	@Override
	public void setPropertyValue(byte index, Object value, boolean override) {
		if (override) {
			workOnObject.setRawValue(index, value);
		} else {
			// implement
		}
	}

	@Override
	public void setScope(Scope scope) {
		workOnObject.setScope(scope);
	}

	@Override
	public void setHoldingProperty(PropertyMirror propMirror, byte Index) {
		workOnObject.setDefiningProperty(propMirror, Index);
	}

	@Override
	public void setRoot(boolean isRoot) {
		workOnObject.setRoot(isRoot);
	}

	@Override
	public void setLoadOnStartup(boolean loadOnStartup) {
		workOnObject.setLoadOnStartUp(loadOnStartup);
	}

	@Override
	public void save() {
		workOnObject.afterPropertiesSet();
		//TODO - here we should validate the workOnObject and then redfine the editedObject with the workOnObject
	}

	@Override
	public final void compile() throws CompilationException{
		Mirror superObject = null;
		prepareForCompilation();
		MirrorAPI superState = (MirrorAPI) workOnObject.getSuper();
		if(superState!=null){
			superObject = superState.reflect();
		}
		innerCompile(workOnObject, superObject);
		workOnObject.afterPropertiesSet();
		setFields(workOnObject);
	}


	protected void prepareForCompilation() throws CompilationException{
		MirrorAPI superState = (MirrorAPI) workOnObject.getSuper();
		if (superState == null) {
			String superId = workOnObject.getSuperId();
			if (superId != null) {
				superState = workOnObject.getContext().getState(superId);
				workOnObject.setSuper(superState);
			}
		}
	}

	private void setFields(MirrorAPI object) {
		PropertyMirror[] pMirrors = object.getPropertiesMirrors();
		Object innerO;
		for (PropertyMirror pm : pMirrors) {
			if (pm.isValueContainsInkObject() && pm.isMutable()) {
				innerO = object.getRawValue(pm.getIndex());
				if (innerO != null) {
					switch (pm.getTypeMarker()) {
					case Class:
						if(((Proxiable)innerO).isProxied()){
							innerO = ((Proxiability)innerO).getVanillaState();
						}
						if (!((MirrorAPI) innerO).isRoot()) {
							setFields((MirrorAPI) innerO);
						}
						object.setPropertyValue(pm.getIndex(), innerO);
						break;
					case Collection:
						switch (((CollectionPropertyMirror) pm).getCollectionTypeMarker()) {
						case List:
							handleCollection(((ListPropertyMirror) pm).getItemMirror(),(Collection<?>) innerO);
							object.setPropertyValue(pm.getIndex(), innerO);
							break;
						case Map:
							PropertyMirror keyMirror = ((MapPropertyMirror)pm).getKeyMirror();
							PropertyMirror valueMirror = ((MapPropertyMirror)pm).getValueMirror();
							boolean keyContainsInkObject = keyMirror.isValueContainsInkObject();
							boolean valueContainsInkObject = valueMirror.isValueContainsInkObject();
							if (keyContainsInkObject) {
								handleCollection(keyMirror, ((Map<?, ?>) innerO).keySet());
							}
							if (valueContainsInkObject) {
								handleCollection(valueMirror, ((Map<?, ?>) innerO).values());
								object.setPropertyValue(pm.getIndex(), innerO);
							}
							break;
						}
						break;
					}
				}
			}
		}
		object.afterPropertiesSet();
	}

	private void handleCollection(PropertyMirror itemMirror, Collection<?> col) {
		switch (itemMirror.getTypeMarker()) {
		case Class:
			for (Object item : col) {
				if(((Proxiable)item).isProxied()){
					item = ((Proxiability)item).getVanillaState();
				}
				if (!((Proxiable) item).reflect().isRoot()) {
					setFields((MirrorAPI) item);
				}
			}
			break;
		case Collection:
			switch (((CollectionPropertyMirror) itemMirror)
					.getCollectionTypeMarker()) {
			case List:
				PropertyMirror listItemMirror = ((ListPropertyMirror)itemMirror).getItemMirror();
				for (Object list : col) {
					handleCollection(listItemMirror, (Collection<?>) list);
				}
				break;
			case Map:
				PropertyMirror keyMirror = ((MapPropertyMirror) itemMirror).getKeyMirror();
				PropertyMirror valueMirror = ((MapPropertyMirror) itemMirror).getValueMirror();
				boolean keyContainsInkObject = keyMirror.isValueContainsInkObject();
				boolean valueContainsInkObject = valueMirror.isValueContainsInkObject();
				for (Object map : col) {
					if (keyContainsInkObject) {
						handleCollection(keyMirror, ((Map<?, ?>) map).keySet());
					}
					if (valueContainsInkObject) {
						handleCollection(valueMirror, ((Map<?, ?>) map).values());
					}
				}
				break;
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void innerCompile(MirrorAPI object, Mirror superObject) {
		// TODO - add a compiler object
		PropertyMirror[] pMirrors = object.getPropertiesMirrors();
		Object o;
		Object valuesToSet;
		for (PropertyMirror pm : pMirrors) {
			if (pm.isMutable()) {
				o = object.getRawValue(pm.getIndex());
				if (o == null) {
					valuesToSet = null;
					if(superObject!=null && superObject.getPropertiesCount() > pm.getIndex()){
						valuesToSet = superObject.getPropertyValue(pm.getIndex());
					}
					if (valuesToSet != null) {
						valuesToSet = CoreUtils.cloneOneValue(pm, valuesToSet, false);
					} else {
						valuesToSet = ((Property) pm.getTargetBehavior()).getDefaultValue();
					}
					object.setRawValue(pm.getIndex(), valuesToSet);
				} else{
					if(pm.getTypeMarker()==DataTypeMarker.Class){
						if(((Proxiable)o).isProxied()){
							o = ((Proxiability)o).getVanillaState();
						}
						if(!((MirrorAPI)o).reflect().isRoot()){
							Mirror innerSuper = null;
							if(superObject!=null && superObject.getPropertiesCount()> pm.getIndex()){
								Proxiable innerSuperO = (Proxiable)superObject.getPropertyValue(pm.getIndex());
								if(innerSuperO!=null){
									innerSuper = innerSuperO.reflect();
								}
							}
							innerCompile((MirrorAPI)o, innerSuper);
						}
					}else if (pm.getTypeMarker()==DataTypeMarker.Collection){
						switch(((CollectionPropertyMirror)pm).getCollectionTypeMarker()){
						case List:
							if(((ListPropertyMirror)pm).getItemMirror().getTypeMarker()==DataTypeMarker.Class){
								Collection<MirrorAPI> col = (Collection<MirrorAPI>)o;
								for(Proxiable item : col){
									if((item).isProxied()){
										item = ((Proxiability)item).getVanillaState();
									}
									if(!item.reflect().isRoot()){
										innerCompile((MirrorAPI)item, null);
									}
								}
							}
							if (superObject!=null && superObject.getPropertiesCount()> pm.getIndex()){
								List<?> superValue = (List<?>) superObject.getPropertyValue(pm.getIndex());
								if (superValue != null) {
									PropertyMirror itemPM = ((ListPropertyMirror) pm).getItemMirror();
									Object itemToAdd;
									List mergedList = new ArrayList();
									for (Object i : superValue) {
										itemToAdd = CoreUtils.cloneOneValue(itemPM, i, false);
										mergedList.add(itemToAdd);
									}
									((List)o).addAll(0, mergedList);
								}
							}
							break;
						case Map:
							Map map = (Map)o;
							PropertyMirror keyMirror = ((MapPropertyMirror)pm).getKeyMirror();
							PropertyMirror valueMirror = ((MapPropertyMirror)pm).getValueMirror();
							if(keyMirror.getTypeMarker()==DataTypeMarker.Class){
								Collection<MirrorAPI> col = map.keySet();
								for(Proxiable item : col){
									if((item).isProxied()){
										item = ((Proxiability)item).getVanillaState();
									}
									if(!item.reflect().isRoot()){
										innerCompile((MirrorAPI)item, null);
									}
								}
							}
							if(valueMirror.getTypeMarker()==DataTypeMarker.Class){
								Collection<MirrorAPI> col = map.values();
								for(Proxiable item : col){
									if((item).isProxied()){
										item = ((Proxiability)item).getVanillaState();
									}
									if(!item.reflect().isRoot()){
										innerCompile((MirrorAPI)item, null);
									}
								}
							}
							if(superObject!=null){
								Map<?,?> superValue = (Map<?,?>) superObject.getPropertyValue(pm.getIndex());
								if(superValue!=null && !superValue.isEmpty()){
									Map<?,?> tempMap = ((MapPropertyMirror)pm).getNewInstance();
									tempMap.putAll(map);
									map.clear();
									for(Map.Entry<?, ?> superEn : ((Map<?,?>)superValue).entrySet()){
										if(!tempMap.containsKey(superEn.getKey())){
											Object keyToadd  = CoreUtils.cloneOneValue(keyMirror, superEn.getKey(), false);
											Object valueToadd  = CoreUtils.cloneOneValue(valueMirror, superEn.getValue(), false);
											map.put(keyToadd, valueToadd);
										}
									}
									map.putAll(tempMap);
								}
							}
						}

					}

				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends InkObjectState> T getEditedState() {
		return (T)workOnObject;
	}

	@Override
	public ObjectEditor createDescendent(String	descendentId){
		InkObjectState descendentState = workOnObject.cloneState();
		ObjectEditor descendentEditor =	descendentState.reflect().edit();
		descendentEditor.setSuper(workOnObject);
		if(descendentId!=null){
			descendentEditor.setId(descendentId);
		}
		return descendentEditor;
	}

}
