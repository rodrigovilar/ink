package org.ink.core.vm.factory.internal;

import static org.ink.core.vm.factory.internal.CoreNotations.Ids.BOOLEAN;
import static org.ink.core.vm.factory.internal.CoreNotations.Ids.BOOLEAN_ATTRIBUTE;
import static org.ink.core.vm.factory.internal.CoreNotations.Ids.BYTE;
import static org.ink.core.vm.factory.internal.CoreNotations.Ids.BYTE_ATTRIBUTE;
import static org.ink.core.vm.factory.internal.CoreNotations.Ids.DATE;
import static org.ink.core.vm.factory.internal.CoreNotations.Ids.DATE_ATTRIBUTE;
import static org.ink.core.vm.factory.internal.CoreNotations.Ids.DEFAULT_CONTEXT;
import static org.ink.core.vm.factory.internal.CoreNotations.Ids.DOUBLE;
import static org.ink.core.vm.factory.internal.CoreNotations.Ids.DOUBLE_ATTRIBUTE;
import static org.ink.core.vm.factory.internal.CoreNotations.Ids.FLOAT;
import static org.ink.core.vm.factory.internal.CoreNotations.Ids.FLOAT_ATTRIBUTE;
import static org.ink.core.vm.factory.internal.CoreNotations.Ids.INTEGER;
import static org.ink.core.vm.factory.internal.CoreNotations.Ids.INTEGER_ATTRIBUTE;
import static org.ink.core.vm.factory.internal.CoreNotations.Ids.LIST;
import static org.ink.core.vm.factory.internal.CoreNotations.Ids.LONG;
import static org.ink.core.vm.factory.internal.CoreNotations.Ids.LONG_ATTRIBUTE;
import static org.ink.core.vm.factory.internal.CoreNotations.Ids.MAP;
import static org.ink.core.vm.factory.internal.CoreNotations.Ids.SHORT;
import static org.ink.core.vm.factory.internal.CoreNotations.Ids.SHORT_ATTRIBUTE;
import static org.ink.core.vm.factory.internal.CoreNotations.Ids.STRING;
import static org.ink.core.vm.factory.internal.CoreNotations.Ids.STRING_ATTRIBUTE;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ink.core.vm.constraints.ActivationMode;
import org.ink.core.vm.constraints.ClassConstraintsState;
import org.ink.core.vm.constraints.ConstraintsState;
import org.ink.core.vm.constraints.InstanceValidatorState;
import org.ink.core.vm.constraints.PropertyConstraintsState;
import org.ink.core.vm.constraints.PropertyValueValidatorState;
import org.ink.core.vm.constraints.Severity;
import org.ink.core.vm.constraints.SystemState;
import org.ink.core.vm.constraints.ValidationContextClassState;
import org.ink.core.vm.constraints.ValidationContextState;
import org.ink.core.vm.constraints.ValidationMessageClassState;
import org.ink.core.vm.constraints.ValidationMessageState;
import org.ink.core.vm.constraints.ValidatorClassState;
import org.ink.core.vm.constraints.ValidatorState;
import org.ink.core.vm.exceptions.CoreException;
import org.ink.core.vm.factory.Context;
import org.ink.core.vm.factory.ContextImpl;
import org.ink.core.vm.factory.ContextState;
import org.ink.core.vm.factory.DslFactory;
import org.ink.core.vm.factory.DslFactoryEvent;
import org.ink.core.vm.factory.DslFactoryEventDispatcherImpl;
import org.ink.core.vm.factory.DslFactoryEventDispatcherState;
import org.ink.core.vm.factory.DslFactoryEventKind;
import org.ink.core.vm.factory.DslFactoryEventListenerTraitState;
import org.ink.core.vm.factory.DslFactoryImpl;
import org.ink.core.vm.factory.DslFactoryPersonalityState;
import org.ink.core.vm.factory.DslFactoryState;
import org.ink.core.vm.factory.DslLoaderImpl;
import org.ink.core.vm.factory.DslLoaderState;
import org.ink.core.vm.factory.DslRepositoryImpl;
import org.ink.core.vm.factory.DslRepositoryState;
import org.ink.core.vm.factory.EmptyDslLoaderState;
import org.ink.core.vm.lang.ComponentType;
import org.ink.core.vm.lang.DataTypeMarker;
import org.ink.core.vm.lang.InheritanceConstraints;
import org.ink.core.vm.lang.InkClassImpl;
import org.ink.core.vm.lang.InkClassState;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.InkObjectImpl;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.InkTypeState;
import org.ink.core.vm.lang.JavaMapping;
import org.ink.core.vm.lang.ObjectFactoryState;
import org.ink.core.vm.lang.Property;
import org.ink.core.vm.lang.PropertyState;
import org.ink.core.vm.lang.Scope;
import org.ink.core.vm.lang.Struct;
import org.ink.core.vm.lang.StructClassState;
import org.ink.core.vm.lang.TypedObjectState;
import org.ink.core.vm.lang.constraints.GenericInstanceValidatorState;
import org.ink.core.vm.lang.constraints.GenericPropertyValueValidatorState;
import org.ink.core.vm.lang.internal.ClassMirrorAPI;
import org.ink.core.vm.lang.internal.MirrorAPI;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;
import org.ink.core.vm.lang.internal.annotations.CoreEnumField;
import org.ink.core.vm.lang.internal.annotations.CoreField;
import org.ink.core.vm.lang.internal.annotations.CoreInstanceSpec;
import org.ink.core.vm.lang.internal.annotations.CoreInstanceValues;
import org.ink.core.vm.lang.internal.annotations.CoreInstanceValuesLocation;
import org.ink.core.vm.lang.internal.annotations.CoreListField;
import org.ink.core.vm.lang.internal.annotations.CoreMapField;
import org.ink.core.vm.lang.internal.annotations.CorePropertySpec;
import org.ink.core.vm.lang.internal.annotations.ValidatorMessages;
import org.ink.core.vm.lang.operation.OperationState;
import org.ink.core.vm.lang.operation.interceptors.OperationInterceptorState;
import org.ink.core.vm.lang.operation.interceptors.ValidationInterceptorState;
import org.ink.core.vm.lang.property.AttributeState;
import org.ink.core.vm.lang.property.CollectionPropertyState;
import org.ink.core.vm.lang.property.PropertyValueCalculatorState;
import org.ink.core.vm.lang.property.ValuePropertyState;
import org.ink.core.vm.lang.property.mirror.CollectionPropertyMirror;
import org.ink.core.vm.lang.property.mirror.CollectionPropertyMirrorImpl;
import org.ink.core.vm.lang.property.mirror.CollectionPropertyMirrorState;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.lang.property.mirror.PropertyMirrorImpl;
import org.ink.core.vm.lang.property.mirror.PropertyMirrorState;
import org.ink.core.vm.lang.property.mirror.ValuePropertyMirrorState;
import org.ink.core.vm.messages.MessageClassState;
import org.ink.core.vm.messages.MessageState;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.mirror.ClassMirrorState;
import org.ink.core.vm.mirror.EnumTypeMirrorState;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.mirror.MirrorImpl;
import org.ink.core.vm.mirror.MirrorState;
import org.ink.core.vm.mirror.StructClassMirrorState;
import org.ink.core.vm.mirror.TraitMirrorImpl;
import org.ink.core.vm.mirror.TraitMirrorState;
import org.ink.core.vm.mirror.editor.ClassEditorState;
import org.ink.core.vm.mirror.editor.ObjectEditorClassState;
import org.ink.core.vm.mirror.editor.ObjectEditorState;
import org.ink.core.vm.proxy.Proxiability;
import org.ink.core.vm.proxy.Proxiable;
import org.ink.core.vm.serialization.InkReaderState;
import org.ink.core.vm.traits.ClassHierarchyLocatorState;
import org.ink.core.vm.traits.PersonalityState;
import org.ink.core.vm.traits.TargetLocatorState;
import org.ink.core.vm.traits.TraitClassState;
import org.ink.core.vm.traits.TraitKind;
import org.ink.core.vm.traits.TraitState;
import org.ink.core.vm.types.CollectionTypeMarker;
import org.ink.core.vm.types.CollectionTypeState;
import org.ink.core.vm.types.EnumTypeClassState;
import org.ink.core.vm.types.EnumTypeState;
import org.ink.core.vm.types.NumericTypeState;
import org.ink.core.vm.types.PrimitiveTypeMarker;
import org.ink.core.vm.types.PrimitiveTypeState;
import org.ink.core.vm.utils.CoreUtils;
import org.ink.core.vm.utils.InkNotations;
import org.ink.core.vm.utils.property.AcquiredPropertyValueState;
import org.ink.core.vm.utils.property.BooleanAttributeState;
import org.ink.core.vm.utils.property.ByteAttributeState;
import org.ink.core.vm.utils.property.DateAttributeState;
import org.ink.core.vm.utils.property.DictionaryState;
import org.ink.core.vm.utils.property.DoubleAttributeState;
import org.ink.core.vm.utils.property.ElementsDictionaryState;
import org.ink.core.vm.utils.property.EnumAttributeState;
import org.ink.core.vm.utils.property.FloatAttributeState;
import org.ink.core.vm.utils.property.IntegerAttributeState;
import org.ink.core.vm.utils.property.KeyValueDictionaryState;
import org.ink.core.vm.utils.property.ListPropertyState;
import org.ink.core.vm.utils.property.LongAttributeState;
import org.ink.core.vm.utils.property.MapPropertyState;
import org.ink.core.vm.utils.property.NumericAttributeState;
import org.ink.core.vm.utils.property.PrimitiveAttributeState;
import org.ink.core.vm.utils.property.ReferenceKind;
import org.ink.core.vm.utils.property.ReferenceState;
import org.ink.core.vm.utils.property.ShortAttributeState;
import org.ink.core.vm.utils.property.StringAttributeState;
import org.ink.core.vm.utils.property.constraints.EnumAttributeValidatorState;
import org.ink.core.vm.utils.property.constraints.EnumAttributeValueValidatorState;
import org.ink.core.vm.utils.property.constraints.NumericAttributeValidatorState;
import org.ink.core.vm.utils.property.constraints.NumericAttributeValueValidatorState;
import org.ink.core.vm.utils.property.constraints.StringAttributeValidatorState;
import org.ink.core.vm.utils.property.constraints.StringAttributeValueValidatorState;
import org.ink.core.vm.utils.property.mirror.ListPropertyMirror;
import org.ink.core.vm.utils.property.mirror.ListPropertyMirrorImpl;
import org.ink.core.vm.utils.property.mirror.ListPropertyMirrorState;
import org.ink.core.vm.utils.property.mirror.MapPropertyMirror;
import org.ink.core.vm.utils.property.mirror.MapPropertyMirrorImpl;
import org.ink.core.vm.utils.property.mirror.MapPropertyMirrorState;
import org.ink.core.vm.utils.property.mirror.PrimitiveAttributeMirrorImpl;
import org.ink.core.vm.utils.property.mirror.PrimitiveAttributeMirrorState;
import org.ink.core.vm.utils.property.mirror.ReferenceMirrorState;

/**
 * @author Lior Schachter
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public final class CoreLoaderImpl<S extends CoreLoaderState> extends DslLoaderImpl<S, Class> {

	Class<?>[] enums = new Class<?>[] { PrimitiveTypeMarker.class, CollectionTypeMarker.class, TraitKind.class, ReferenceKind.class, ComponentType.class, InheritanceConstraints.class, JavaMapping.class, Severity.class, SystemState.class, ActivationMode.class, DslFactoryEventKind.class };
	private static final String P_FINAL_VALUE = "final_value";
	private final String P_DEFAULT_VALUE_NAME = "p_default_value";
	private final String PROPERTY_PREFIX = "p_";
	private final String TRAIT_PREFIX = "t_";
	public Map<String, CoreObjectDescriptor> elements = new LinkedHashMap();
	public Map<Class<?>, CoreClassDescriptor> classElements = new HashMap<Class<?>, CoreClassDescriptor>();
	public Map<Class<?>, String> simpleTypeMapping = new HashMap<Class<?>, String>();
	public Map<String, String> simpleTypeToAttributeMapping = new HashMap<String, String>();
	public DslFactory factory;
	private Context context = null;

	public Collection<CoreObjectDescriptor> start(DslFactory factory) {
		this.factory = factory;
		simpleTypeMapping.put(String.class, STRING);
		simpleTypeMapping.put(Boolean.class, BOOLEAN);
		simpleTypeMapping.put(Integer.class, INTEGER);
		simpleTypeMapping.put(Date.class, DATE);
		simpleTypeMapping.put(Long.class, LONG);
		simpleTypeMapping.put(Double.class, DOUBLE);
		simpleTypeMapping.put(Float.class, FLOAT);
		simpleTypeMapping.put(Byte.class, BYTE);
		simpleTypeMapping.put(Short.class, SHORT);
		simpleTypeToAttributeMapping.put(STRING, STRING_ATTRIBUTE);
		simpleTypeToAttributeMapping.put(BOOLEAN, BOOLEAN_ATTRIBUTE);
		simpleTypeToAttributeMapping.put(INTEGER, INTEGER_ATTRIBUTE);
		simpleTypeToAttributeMapping.put(DATE, DATE_ATTRIBUTE);
		simpleTypeToAttributeMapping.put(LONG, LONG_ATTRIBUTE);
		simpleTypeToAttributeMapping.put(DOUBLE, DOUBLE_ATTRIBUTE);
		simpleTypeToAttributeMapping.put(FLOAT, FLOAT_ATTRIBUTE);
		simpleTypeToAttributeMapping.put(BYTE, BYTE_ATTRIBUTE);
		simpleTypeToAttributeMapping.put(SHORT, SHORT_ATTRIBUTE);
		loadCoreElements();
		return elements.values();
	}

	private void loadCoreElements() {
		try {
			instantiateCoreClassElements();
			instantiatePrimitiveTypes();
			instantiateCollectionTypes();
			instantiateEnumerators();
			buildProperties();
			createObjects();
			activateClasses();
			fillObjects();
			activateObjects();
			cleanUp();
		} catch (Exception e) {
			e.printStackTrace();
			throw new CoreException("Fatal Error. Could not boot.", e);
		}
	}

	private void createObjects() throws Exception {
		Class<?> stateClass;
		CoreInstanceSpec instanceSpec;
		for (CoreClassDescriptor desc : classElements.values()) {
			stateClass = desc.getStateClass();
			instanceSpec = stateClass.getAnnotation(CoreInstanceSpec.class);
			if (instanceSpec != null) {
				createObject(desc, instanceSpec);
			}
		}

	}

	private void createObject(CoreClassDescriptor classDesc, CoreInstanceSpec instanceSpec) throws Exception {
		String[] ids = instanceSpec.ids();
		CoreInstanceValuesLocation[] locationsArr = instanceSpec.locations();
		CoreInstanceValues[] valuesArr = instanceSpec.values();
		List<PropertyState> properties = classDesc.getPropertiesList();
		CoreObjectDescriptor desc;
		MirrorAPI prop;
		String id;
		Class<?> propertyType;
		byte[] indexes;
		String[] values;
		MirrorAPI o;
		for (int i = 0; i < ids.length; i++) {
			id = ids[i];
			o = newInstance(id, classDesc.getStateClass(), classDesc.getNumberOfFields(), classDesc.getNumberOfTraits(), true);
			indexes = locationsArr[i].indexes();
			values = valuesArr[i].values();
			for (int j = 0; j < indexes.length; j++) {
				prop = (MirrorAPI) properties.get(indexes[j]);
				propertyType = classDesc.getPropertyClass((String) prop.getRawValue(PropertyState.p_name));
				o.setRawValue(indexes[j], convertStringValue(values[j], propertyType));
			}
			desc = new CoreObjectDescriptorImpl(id, classDesc.getId(), classDesc.getStateClass(), o);
			elements.put(id, desc);
		}
	}

	private void cleanUp() throws Exception {
		ClassMirrorAPI cls;
		MirrorAPI o;
		Set<MirrorAPI> allObjects = new HashSet<MirrorAPI>();
		for (CoreClassDescriptor desc : classElements.values()) {
			cls = desc.getObject();
			o = (MirrorAPI) (desc.getObject()).getFactoryState();
			cleanUpObject(o, allObjects);
			insertFinalProperties(cls, desc);
		}

		CoreClassDescriptor traitMirrorDesc = classElements.get(TraitMirrorState.class);
		// resolve ink.core:TraitMirror self-dependency
		MirrorAPI tm = traitMirrorDesc.getObject();
		MirrorAPI traits = (MirrorAPI) tm.getRawValue(InkClassState.p_personality);
		MirrorAPI traitMirrorState = (MirrorAPI) traits.getRawValue(PersonalityState.p_reflection);
		TraitMirrorImpl<?> mirror = new TraitMirrorImpl();
		mirror.setTargetState(traitMirrorState);
		traitMirrorState.boot((InkClassState) elements.get(traitMirrorDesc.getId()).getObject(), factory, mirror, context, null);
		traitMirrorState.cacheTrait(TraitMirrorState.t_reflection, mirror);
		elements.get(LIST).getObject().afterPropertiesSet();
		elements.get(MAP).getObject().afterPropertiesSet();
		MirrorAPI enumO;
		for (Class<?> c : enums) {
			enumO = elements.get(createEnumId(c)).getObject();
			enumO.afterPropertiesSet();
		}
		cleanUpObject(tm, allObjects);
		allObjects.add(tm);
		for (CoreObjectDescriptor desc : elements.values()) {
			o = desc.getObject();
			cleanUpObject(o, allObjects);
		}
		for (MirrorAPI state : allObjects) {
			state.afterPropertiesSet();
			if (!((ClassMirror) state.getMeta().reflect()).canCacheBehaviorInstance()) {
				state.cacheBeahvior(null);
			}
		}
	}

	private void insertFinalProperties(MirrorAPI o, CoreClassDescriptor desc) throws Exception {
		CoreClassSpec metadata = desc.getMetadata();
		if (metadata != null) {
			byte[] locs = metadata.finalValuesLocation();
			String[] values = metadata.finalValues();
			if (locs.length > 0) {
				List props = desc.getPropertiesList();
				MirrorAPI prop = (MirrorAPI) props.get(locs[0]);
				Object finalValue = null;
				if (prop instanceof EnumAttributeState) {
					finalValue = values[0];
					// just checking that the final enum value is valid
					convertStringValue(values[0], desc.getPropertyClass((String) prop.getRawValue(PropertyState.p_name)));
				} else {
					finalValue = convertStringValue(values[0], desc.getPropertyClass((String) prop.getRawValue(PropertyState.p_name)));
				}
				byte finalPropertyLocation = prop.getPropertyIndex(P_FINAL_VALUE);
				prop.setRawValue(finalPropertyLocation, finalValue);
			}
		}
	}

	private void assembleFactory(DslFactoryState targetFactory) throws Exception {
		CoreClassDescriptor classDesc = classElements.get(DslLoaderState.class);
		MirrorAPI o = newInstance(null, classDesc.getStateClass(), classDesc.getNumberOfFields(), classDesc.getNumberOfTraits(), false);
		o.boot((InkClassState) elements.get(classDesc.getClassId()).getObject(), factory, this, context, null);
		targetFactory.setLoader((DslLoaderState) o);
		classDesc = classElements.get(DslRepositoryState.class);
		o = newInstance(null, classDesc.getStateClass(), classDesc.getNumberOfFields(), classDesc.getNumberOfTraits(), false);
		o.boot((InkClassState) elements.get(classDesc.getClassId()).getObject(), factory, new DslRepositoryImpl(), context, null);
		targetFactory.setRepository((DslRepositoryState) o);
		targetFactory.setJavaPackage(CoreNotations.CORE_PACKAGE);
		targetFactory.setNamespace(CoreNotations.CORE_NAMESPACE);
		targetFactory.setDslPackage(CoreNotations.CORE_SOURCE_PATH);
		((MirrorAPI) targetFactory).cacheTrait(DslFactoryState.t_app_context, context);
	}

	private void cleanUpObject(MirrorAPI o, Set<MirrorAPI> allObjects) throws Exception {
		if (allObjects.contains(o)) {
			return;
		}
		if (o.getSuper() != null && !allObjects.contains(o.getSuper())) {
			cleanUpObject((MirrorAPI) o.getSuper(), allObjects);
		}
		if (o.isClass()) {
			o.afterPropertiesSet();
		}
		PropertyMirror[] mirrors = o.getPropertiesMirrors();
		allObjects.add(o);
		DataTypeMarker marker;
		for (PropertyMirror m : mirrors) {
			Object value = null;
			Object propertyValue = ((Property) m.getTargetBehavior()).getFinalValue();
			if (propertyValue == null) {
				value = o.getRawValue(m.getIndex());
				if (value == null) {
					((Property) m.getTargetBehavior()).afterStateSet();
					propertyValue = ((Property) m.getTargetBehavior()).getDefaultValue();
				}
			}
			marker = m.getTypeMarker();
			switch (marker) {
			case Class:
				MirrorAPI innerO = (MirrorAPI) value;
				if (propertyValue != null) {
					if (((Proxiable) propertyValue).isProxied()) {
						propertyValue = elements.get(((Proxiability) propertyValue).getVanillaState().getId()).getObject();
					} else {
						propertyValue = ((InkObject) propertyValue).cloneState();
					}
					o.insertValue(m.getIndex(), propertyValue);
				} else {
					o.insertValue(m.getIndex(), innerO);
				}
				if (innerO != null && !innerO.isRoot()) {
					cleanUpObject(innerO, allObjects);
				}
				break;
			case Collection:
				CollectionTypeMarker collectionMarker = ((CollectionPropertyMirror) m).getCollectionTypeMarker();
				switch (collectionMarker) {
				case List:
					if (((ListPropertyMirror) m).getItemMirror().getTypeMarker() == DataTypeMarker.Class) {
						List<MirrorAPI> l = (List<MirrorAPI>) value;
						if (l != null) {
							MirrorAPI item;
							for (byte i = 0; i < l.size(); i++) {
								item = l.get(i);
								if (m.getIndex() == InkClassState.p_properties && o.isClass()) {
									((PropertyMirrorImpl<?>) item.reflect()).bind((ClassMirror) o.reflect(), i);
								}
								cleanUpObject(item, allObjects);
							}
							o.insertValue(m.getIndex(), l);
						}
					}
					break;
				case Map:
					Map<?, ?> mapValue = (Map<?, ?>) value;
					if (mapValue != null) {
						PropertyMirror keyMirror = ((MapPropertyMirror) m).getKeyMirror();
						PropertyMirror valueMirror = ((MapPropertyMirror) m).getValueMirror();
						if (keyMirror.getTypeMarker() == DataTypeMarker.Class) {
							for (Object item : mapValue.keySet()) {
								cleanUpObject((MirrorAPI) item, allObjects);
							}
						}
						if (valueMirror.getTypeMarker() == DataTypeMarker.Class) {
							for (Object item : mapValue.values()) {
								cleanUpObject((MirrorAPI) item, allObjects);
							}
						}
						o.insertValue(m.getIndex(), mapValue);
					}
				}
				break;
			default:
				if (propertyValue != null) {
					o.insertValue(m.getIndex(), propertyValue);
				}
				break;
			}
			m.afterTargetSet();
		}
		o.afterPropertiesSet();
	}

	private void createAndBindContext(MirrorAPI targetFactory) throws Exception {
		CoreClassDescriptor classDesc = classElements.get(ContextState.class);
		MirrorAPI o = newInstance(DEFAULT_CONTEXT, classDesc.getStateClass(), classDesc.getNumberOfFields(), classDesc.getNumberOfTraits(), true);
		context = new ContextImpl();
		o.boot((InkClassState) elements.get(classDesc.getClassId()).getObject(), factory, context, context, null);
		elements.put(DEFAULT_CONTEXT, new CoreObjectDescriptorImpl("ink.core:DefaultContext", classDesc.getId(), classDesc.getStateClass(), o));
	}

	private void assembleFactoryClass() throws Exception {
		CoreClassDescriptor classDesc = classElements.get(DslFactoryState.class);
		MirrorAPI o = classDesc.getObject();
		createAndBindContext(o);
		MirrorAPI traits = (MirrorAPI) (o).getRawValue(DslFactoryState.p_personality);
		traits.setRawValue(DslFactoryPersonalityState.p_app_context, elements.get(DEFAULT_CONTEXT).getObject());
		classDesc = classElements.get(DslFactoryEventDispatcherState.class);
		o = newInstance(null, classDesc.getStateClass(), classDesc.getNumberOfFields(), classDesc.getNumberOfTraits(), true);
		InkObjectImpl<?> listener = new DslFactoryEventDispatcherImpl();
		o.setRoot(false);
		o.boot((InkClassState) elements.get(classDesc.getClassId()).getObject(), factory, listener, context, null);
		traits.setRawValue(DslFactoryPersonalityState.p_event_dispatcher, o);
	}

	private void activateObjects() throws Exception {
		assembleFactoryClass();
		CoreClassDescriptor classDesc = classElements.get(ObjectFactoryState.class);
		MirrorAPI o = classDesc.getObject();
		((ContextImpl<?>) context).setTargetState(o);
		o.boot((InkClassState) elements.get(classDesc.getClassId()).getObject(), factory, (DslFactoryImpl<?>) factory, context, null);
		assembleFactory((DslFactoryState) o);
		classDesc = classElements.get(InkClassState.class);
		o = classDesc.getObject();
		// TODO - This should be removed once we have InkMetaClass
		o.setRawValue(InkClassState.p_component_type, ComponentType.Root);
		o.boot((InkClassState) o, factory, new InkClassImpl(), context, createMirror(o, classDesc, null, (byte) -1));
		for (CoreObjectDescriptor desc : elements.values()) {
			o = desc.getObject();
			classDesc = (CoreClassDescriptor) elements.get(desc.getClassId());
			activateObject(o, classDesc, null, (byte) -1);
			if (desc.isClass()) {
				activateObject((MirrorAPI) ((ClassMirrorAPI) o).getFactoryState(), (CoreClassDescriptor) elements.get(CoreNotations.Ids.OBJECT_FACTORY), null, (byte) -1);
			}
		}
	}

	private Mirror createMirror(MirrorAPI target, CoreClassDescriptor classDesc, PropertyMirror definingProperty, byte loc) throws Exception {
		MirrorImpl<?> result = target.getCachedTrait(InkObjectState.t_reflection);
		Class<? extends MirrorState> mirrorStateClass = null;
		if (classDesc.getMetadata() != null) {
			mirrorStateClass = classDesc.getMetadata().mirrorClass();
		} else {
			mirrorStateClass = MirrorState.class;
		}
		CoreClassDescriptor classMirrorDesc = classElements.get(mirrorStateClass);
		if (result == null) {
			if (loc > -1) {
				result = (PropertyMirrorImpl<?>) definingProperty;
			} else {
				Class<?> mirrorClass = getBehaviorClass(classMirrorDesc);
				result = (MirrorImpl<?>) mirrorClass.newInstance();
			}
		}
		MirrorAPI traits = (MirrorAPI) classDesc.getObject().getRawValue(InkClassState.p_personality);
		MirrorAPI mirrorState = (MirrorAPI) traits.getRawValue(PersonalityState.p_reflection);
		result.setTargetState(target);
		target.cacheTrait(InkObjectState.t_reflection, result);
		mirrorState.boot(classMirrorDesc.getObject(), factory, result, context, null);
		return result;
	}

	private Class<?> getStateInterface(InkObjectState o) {
		return o.getClass().getInterfaces()[0];
	}

	public Class<?> getBehaviorClass(CoreClassDescriptor cls) {
		Class<?> stateClass = cls.getStateClass();
		Class<?> result = null;
		String className;
		while (result == null) {
			className = stateClass.getName().substring(0, stateClass.getName().indexOf("State")) + "Impl";
			try {
				result = loadClass(className);
			} catch (ClassNotFoundException e) {
				stateClass = stateClass.getInterfaces()[0];
			}
		}
		return result;
	}

	private void activateObject(MirrorAPI o, CoreClassDescriptor classDesc, PropertyMirror definingProperty, byte index) throws Exception {
		InkObjectImpl<?> behaviorInstance = (InkObjectImpl<?>) o.getCachedBehavior();
		if (behaviorInstance == null && classDesc.getObject().getCanCacheBehaviorInstance()) {
			try {
				behaviorInstance = (InkObjectImpl<?>) classDesc.getBehaviorClass().newInstance();
			} catch (InstantiationException e) {
				throw new RuntimeException("Could not create " + classDesc.getBehaviorClass().getName() + ".", e);
			}
		}
		o.boot(classDesc.getObject(), factory, behaviorInstance, context, createMirror(o, classDesc, definingProperty, index));
		PropertyMirror[] mirrors = o.getPropertiesMirrors();
		DataTypeMarker marker;
		CoreClassDescriptor innerCoerDesc;
		for (PropertyMirror m : mirrors) {
			marker = m.getTypeMarker();
			switch (marker) {
			case Class:
				MirrorAPI innerO = (MirrorAPI) o.getRawValue(m.getIndex());
				if (innerO != null && !innerO.isRoot()) {
					innerCoerDesc = classElements.get(getStateInterface(innerO));
					activateObject(innerO, innerCoerDesc, null, (byte) -1);
				}
				break;
			case Collection:
				CollectionTypeMarker collectionMarker = ((CollectionPropertyMirror) m).getCollectionTypeMarker();
				switch (collectionMarker) {
				case List:
					if (((ListPropertyMirror) m).getItemMirror().getTypeMarker() == DataTypeMarker.Class) {
						List<MirrorAPI> l = (List<MirrorAPI>) o.getRawValue(m.getIndex());
						if (l != null) {
							MirrorAPI item;
							for (int i = 0; i < l.size(); i++) {
								item = l.get(i);
								if (!item.isRoot()) {
									innerCoerDesc = classElements.get(getStateInterface(item));
									if (item instanceof PropertyState) {
										PropertyMirror mirror = ((ClassMirrorAPI) o).getClassPropertiesMirrors()[i];
										activateObject(item, innerCoerDesc, mirror, (byte) i);
									} else {
										activateObject(item, innerCoerDesc, null, (byte) -1);
									}
								}
							}
						}
					}
					break;
				case Map:
					Map<?, ?> mapValue = (Map<?, ?>) o.getRawValue(m.getIndex());
					if (mapValue != null) {
						PropertyMirror keyMirror = ((MapPropertyMirror) m).getKeyMirror();
						PropertyMirror valueMirror = ((MapPropertyMirror) m).getValueMirror();
						if (keyMirror.getTypeMarker() == DataTypeMarker.Class) {
							for (Object item : mapValue.keySet()) {
								innerCoerDesc = classElements.get(getStateInterface((InkObjectState) item));
								activateObject((MirrorAPI) item, innerCoerDesc, null, (byte) -1);
							}
						}
						if (valueMirror.getTypeMarker() == DataTypeMarker.Class) {
							for (Object item : mapValue.values()) {
								innerCoerDesc = classElements.get(getStateInterface((InkObjectState) item));
								activateObject((MirrorAPI) item, innerCoerDesc, null, (byte) -1);
							}
						}
					}
					break;
				}
				break;
			}
		}
	}

	private void activateClasses() throws Exception {
		for (CoreClassDescriptor desc : classElements.values()) {
			activateClass(desc);
		}
	}

	private void activateClass(CoreClassDescriptor desc) throws Exception {
		ClassMirrorAPI theClass = desc.getObject();
		InkClassState cls = (InkClassState) elements.get(desc.getClassId()).getObject();
		CoreClassDescriptor superDesc = (CoreClassDescriptor) elements.get(desc.getClassId());
		Class<?>[] allInterfaces = null;
		if (!(theClass instanceof StructClassState)) {
			Class<?> behaviorClass = getBehaviorClass(desc);
			if (Modifier.isAbstract(behaviorClass.getModifiers())) {
				theClass.setAbstract(true);
			}
			desc.setBehaviorClass(behaviorClass);
			allInterfaces = CoreUtils.getBehaviorProxyInterfaces(behaviorClass);
		}
		theClass.bootClass(cls, desc.getPropertyMirrors(), superDesc.getPropertyMirrors(), desc.getPropertiesIndexes(), superDesc.getPropertiesIndexes(), allInterfaces, factory, (Class<InkObjectState>) getDataClass(desc.getStateClass()));
	}

	private void buildProperties() throws Exception {
		for (CoreObjectDescriptor desc : elements.values()) {
			if (desc.isClass() && ((CoreClassDescriptor) desc).getPropertiesIndexes() == null) {
				createProperties(desc);
			}
		}
	}

	private void createProperties(CoreObjectDescriptor desc) throws Exception {
		Map<String, ? extends PropertyState> props;
		PropertyMirror[] mirrors;
		Map<String, Byte> indexes;
		Field[] fields = ((CoreClassDescriptor) desc).getFields();
		props = createProperties(fields, (CoreClassDescriptor) desc);
		mirrors = createPropertiesMirrors(props.values(), (CoreClassDescriptor) desc);
		indexes = createPropertiesIndexes(props.values());
		((CoreClassDescriptor) desc).setPropertyMirrors(mirrors);
		((CoreClassDescriptor) desc).setProperties(props);
		((CoreClassDescriptor) desc).setPropertiesIndexes(indexes);
	}

	private Map<String, Byte> createPropertiesIndexes(Collection<? extends PropertyState> props) {
		Map<String, Byte> result = new HashMap<String, Byte>();
		byte i = 0;
		for (PropertyState ps : props) {
			String name = (String) ((MirrorAPI) ps).getRawValue(PropertyState.p_name);
			result.put(name, i);
			i++;
		}
		return result;
	}

	private PropertyMirror[] createPropertiesMirrors(Collection<? extends PropertyState> props, CoreClassDescriptor desc) throws Exception {
		List<PropertyMirror> mirrors = new ArrayList<PropertyMirror>();
		byte i = 0;
		for (PropertyState ps : props) {
			mirrors.add(createPropertyMirror(ps, desc, i));
			i++;
		}
		return mirrors.toArray(new PropertyMirror[] {});
	}

	private PropertyMirror createPropertyMirror(PropertyState prop, CoreClassDescriptor desc, byte index) throws Exception {
		Class<?> propertyClass = getStateInterface(prop);
		CoreClassSpec metadata = findAnnotation(propertyClass, CoreClassSpec.class);
		Class<? extends MirrorState> mirrorState = PropertyMirrorState.class;
		if (metadata != null) {
			mirrorState = metadata.mirrorClass();
		}
		String mirrorStateClassName = mirrorState.getName();
		String mirrorImplClassName = mirrorStateClassName.substring(0, mirrorStateClassName.indexOf("State")) + "Impl";
		Class<?> mirrorImplClass = loadClass(mirrorImplClassName);
		PropertyMirrorImpl<?> result = (PropertyMirrorImpl<?>) mirrorImplClass.newInstance();
		result.setTargetState(prop);
		((MirrorAPI) prop).cacheTrait(InkObjectState.t_reflection, result);
		String name = (String) ((MirrorAPI) prop).getRawValue(PropertyState.p_name);
		;
		CoreField fieldAnnot = desc.getFieldAnntation(name);
		boolean isComputed = false;
		boolean hasStaticValue = true;
		if (fieldAnnot != null) {
			isComputed = fieldAnnot.computed();
			hasStaticValue = fieldAnnot.hasStaticValue();
		}
		if (result instanceof CollectionPropertyMirrorImpl<?>) {
			instantiateCollectionMirror(desc, prop, index, (CollectionPropertyMirrorImpl<?>) result, isComputed, hasStaticValue);
		} else if (result instanceof PrimitiveAttributeMirrorImpl<?>) {
			instantiatePrimitiveMirror(prop, index, (PrimitiveAttributeMirrorImpl<?>) result, isComputed, hasStaticValue);
		} else if (result instanceof PropertyMirrorImpl<?>) {
			if (prop.getClass() == ReferenceState.Data.class) {
				instantiateReferenceMirror(prop, index, result, isComputed, hasStaticValue);
			} else if (prop.getClass() == EnumAttributeState.Data.class) {
				instantiateEnumMirror(prop, index, result, isComputed, hasStaticValue);
			} else {
				throw new CoreException("Invalid property class " + prop.getClass().getName());
			}
		} else {
			throw new CoreException("Invalid property mirror class " + mirrorImplClassName);
		}
		return result;
	}

	private void instantiateCollectionMirror(CoreClassDescriptor desc, PropertyState prop, byte index, CollectionPropertyMirrorImpl<?> mirror, boolean isComputed, boolean hasStaticValue) throws Exception {
		DataTypeMarker marker = DataTypeMarker.Collection;
		CollectionTypeMarker collectionMarker = null;
		Class<?> typeClass = null;
		if (prop.getClass() == MapPropertyState.Data.class) {
			collectionMarker = CollectionTypeMarker.Map;
			typeClass = factory.resolveCollectionClass(collectionMarker);
			DictionaryState ds = (DictionaryState) ((MirrorAPI) prop).getRawValue(MapPropertyState.p_specifications);
			if (ds.getClass() == KeyValueDictionaryState.Data.class) {
				PropertyMirror valueMirror = createPropertyMirror((PropertyState) ((MirrorAPI) ds).getRawValue(KeyValueDictionaryState.p_value), desc, Property.UNBOUNDED_PROPERTY_INDEX);
				PropertyMirror keyMirror = createPropertyMirror((PropertyState) ((MirrorAPI) ds).getRawValue(KeyValueDictionaryState.p_key), desc, Property.UNBOUNDED_PROPERTY_INDEX);
				((MapPropertyMirrorImpl<?>) mirror).boot(index, (String) ((MirrorAPI) prop).getRawValue(MapPropertyState.p_name), typeClass, marker, hasStaticValue, isComputed, collectionMarker, valueMirror, keyMirror);
			} else {
				// elements
				PropertyState itemPropertyDesc = (PropertyState) ((MirrorAPI) ds).getRawValue(ElementsDictionaryState.p_item);
				InkTypeState theType = (InkTypeState) ((MirrorAPI) itemPropertyDesc).getRawValue(PropertyState.p_type);
				CoreClassDescriptor cd = (CoreClassDescriptor) elements.get(theType.getId());
				if (cd.getPropertiesIndexes() == null) {
					createProperties(cd);
				}
				String keyProperty = (String) ((MirrorAPI) ds).getRawValue(ElementsDictionaryState.p_key_property);
				PropertyMirror valueMirror = createPropertyMirror(itemPropertyDesc, desc, Property.UNBOUNDED_PROPERTY_INDEX);
				PropertyMirror keyMirror = cd.getPropertyMirrors()[cd.getPropertiesIndexes().get(keyProperty)];
				((MapPropertyMirrorImpl<?>) mirror).boot(index, (String) ((MirrorAPI) prop).getRawValue(MapPropertyState.p_name), typeClass, marker, hasStaticValue, isComputed, collectionMarker, valueMirror, keyMirror);
			}

		} else if (prop.getClass() == ListPropertyState.Data.class) {
			collectionMarker = CollectionTypeMarker.List;
			PropertyMirror itemMirror = createPropertyMirror((PropertyState) ((MirrorAPI) prop).getRawValue(ListPropertyState.p_list_item), desc, Property.UNBOUNDED_PROPERTY_INDEX);
			String name = (String) ((MirrorAPI) prop).getRawValue(PropertyState.p_name);
			((ListPropertyMirrorImpl<?>) mirror).boot(index, name, typeClass, marker, hasStaticValue, isComputed, collectionMarker, itemMirror);
		} else {
			throw new CoreException("Could not resolve collection type marker for collection property " + prop.getClass().getName());
		}
	}

	private void instantiateReferenceMirror(PropertyState prop, byte index, PropertyMirrorImpl<?> mirror, boolean isComputed, boolean hasStaticValue) {
		DataTypeMarker marker = DataTypeMarker.Class;
		CoreClassDescriptor typeDescriptor = (CoreClassDescriptor) elements.get(((InkObjectState) ((MirrorAPI) prop).getRawValue(PropertyState.p_type)).getId());
		Class<?> typeClass = typeDescriptor.getStateClass();
		String name = (String) ((MirrorAPI) prop).getRawValue(PropertyState.p_name);
		mirror.boot(index, name, typeClass, marker, hasStaticValue, isComputed);
	}

	private void instantiateEnumMirror(PropertyState prop, byte index, PropertyMirrorImpl<?> mirror, boolean isComputed, boolean hasStaticValue) throws Exception {
		DataTypeMarker marker = DataTypeMarker.Enum;
		EnumTypeState enumState = (EnumTypeState) ((MirrorAPI) prop).getRawValue(PropertyState.p_type);
		Class<?> typeClass = loadClass(getEnumClassName(enumState));
		String name = (String) ((MirrorAPI) prop).getRawValue(PropertyState.p_name);
		mirror.boot(index, name, typeClass, marker, hasStaticValue, isComputed);
	}

	private void instantiatePrimitiveMirror(PropertyState prop, byte index, PrimitiveAttributeMirrorImpl<?> mirror, boolean isComputed, boolean hasStaticValue) {
		DataTypeMarker marker = DataTypeMarker.Primitive;
		PrimitiveTypeMarker primitiveMarker = null;
		if (prop.getClass() == StringAttributeState.Data.class) {
			primitiveMarker = PrimitiveTypeMarker.String;
		} else if (prop.getClass() == LongAttributeState.Data.class) {
			primitiveMarker = PrimitiveTypeMarker.Long;
		} else if (prop.getClass() == DoubleAttributeState.Data.class) {
			primitiveMarker = PrimitiveTypeMarker.Double;
		} else if (prop.getClass() == ShortAttributeState.Data.class) {
			primitiveMarker = PrimitiveTypeMarker.Short;
		} else if (prop.getClass() == FloatAttributeState.Data.class) {
			primitiveMarker = PrimitiveTypeMarker.Float;
		} else if (prop.getClass() == ByteAttributeState.Data.class) {
			primitiveMarker = PrimitiveTypeMarker.Byte;
		} else if (prop.getClass() == BooleanAttributeState.Data.class) {
			primitiveMarker = PrimitiveTypeMarker.Boolean;
		} else if (prop.getClass() == DateAttributeState.Data.class) {
			primitiveMarker = PrimitiveTypeMarker.Date;
		} else if (prop.getClass() == IntegerAttributeState.Data.class) {
			primitiveMarker = PrimitiveTypeMarker.Integer;
		} else {
			throw new CoreException("Could not resolve collection type marker for collection property " + prop.getClass().getName());
		}
		Class<?> typeClass = factory.resolvePrimitiveClass(primitiveMarker);
		String name = (String) ((MirrorAPI) prop).getRawValue(PropertyState.p_name);
		mirror.boot(index, name, typeClass, marker, hasStaticValue, isComputed, primitiveMarker);
	}

	private Map<String, ? extends PropertyState> createProperties(Field[] fields, CoreClassDescriptor desc) throws Exception {
		Map result = new LinkedHashMap(fields.length);
		PropertyState prop;
		for (Field f : fields) {
			prop = createProperty(f, desc);
			if (prop != null) {
				String name = (String) ((MirrorAPI) prop).getRawValue(PropertyState.p_name);
				result.put(name, prop);
			}
		}
		return result;
	}

	private PropertyState createProperty(Field f, CoreClassDescriptor desc) throws Exception {
		PropertyState result = null;
		Class<?> stateClass = desc.getStateClass();
		String propertyName = f.getName().substring(2);
		Method getter = extractGetter(f, stateClass);
		if (getter == null) {
			if (desc.getMetadata().isAbstract()) {
				return null;
			}
			throw new CoreException("Could not extract setter for field : " + f.getName() + ", of class " + classElements.get(stateClass).getId());
		}

		Class<?> paramClass = getter.getReturnType();
		if (InkObject.class.isAssignableFrom(paramClass)) {
			paramClass = loadClass(paramClass.getName() + "State");
		}
		desc.addPropertyClass(propertyName, paramClass);
		result = createProperty(f, getter, propertyName, paramClass, stateClass);
		return result;
	}

	private PropertyState createProperty(Field f, Method m, String propertyName, Class<?> typeClass, Class<?> containerClass) throws Exception {
		boolean mandatory = false;
		InheritanceConstraints st = InheritanceConstraints.Instance_Can_Refine_Inherited_Value;
		CoreField fieldAnnot = f.getAnnotation(CoreField.class);
		if (fieldAnnot != null) {
			mandatory = fieldAnnot.mandatory();
			st = fieldAnnot.valuePropagationStrategy();
		}
		PropertyState result = null;
		if (typeClass.isEnum()) {
			result = createEnumProperty(f, propertyName, typeClass, mandatory);
		} else if (typeClass.isInterface()) {
			if (typeClass == List.class) {
				result = createListProperty(f, m, propertyName, typeClass, containerClass, mandatory);
			} else if (typeClass == Map.class) {
				result = createMapProperty(f, m, propertyName, typeClass, containerClass, mandatory);
			} else {
				result = createReference(f, m, propertyName, typeClass, containerClass, mandatory);
			}
		} else {
			result = createSimpleAttribute(f, m, propertyName, typeClass, containerClass, mandatory);
		}
		((MirrorAPI) result).setRawValue(PropertyState.p_inheritance_constraints, st);
		return result;
	}

	private PropertyState createSimpleAttribute(Field f, Method m, String propertyName, Class<?> typeClass, Class<?> containerClass, boolean mandatory) throws Exception {
		CoreObjectDescriptor typeDesc = elements.get(simpleTypeMapping.get(typeClass));
		CoreClassDescriptor attDesc = (CoreClassDescriptor) elements.get(simpleTypeToAttributeMapping.get(typeDesc.getId()));
		PrimitiveAttributeState result = (PrimitiveAttributeState) getDataClass(attDesc.getStateClass()).newInstance();
		((MirrorAPI) result).setCoreObject(attDesc.getNumberOfFields(), attDesc.getNumberOfTraits());
		((MirrorAPI) result).setRawValue(PropertyState.p_mandatory, mandatory);
		((MirrorAPI) result).setRawValue(PropertyState.p_name, propertyName);
		((MirrorAPI) result).setRawValue(PropertyState.p_type, typeDesc.getObject());
		CoreField fieldAnnot = f.getAnnotation(CoreField.class);
		if (fieldAnnot != null && !fieldAnnot.defaultValue().equals("")) {
			try {
				Object defaultValue = convertStringValue(fieldAnnot.defaultValue(), typeClass);
				byte loc = (Byte) attDesc.getStateClass().getField(P_DEFAULT_VALUE_NAME).get(null);
				((MirrorAPI) result).setRawValue(loc, defaultValue);
			} catch (Exception e) {
				throw new CoreException("Could not instantiate default value for property " + propertyName + ", of type " + containerClass.getName(), e);
			}
		}
		return result;
	}

	private Object convertStringValue(String value, Class<?> typeClass) throws Exception {
		Object result = value;
		if (typeClass.isInterface()) {
			result = elements.get(value).getObject();
		} else if (typeClass != String.class) {
			Method valueMethod = typeClass.getMethod(InkNotations.Reflection.VALUE_OF_METHOD_NAME, new Class[] { String.class });
			result = valueMethod.invoke(null, new Object[] { value });
		}
		return result;
	}

	private PropertyState createReference(Field f, Method m, String propertyName, Class<?> typeClass, Class<?> containerClass, boolean mandatory) throws Exception {
		ReferenceState result = new ReferenceState.Data();
		CoreClassDescriptor desc = classElements.get(ReferenceState.class);
		((MirrorAPI) result).setCoreObject(desc.getNumberOfFields(), desc.getNumberOfTraits());
		((MirrorAPI) result).setRawValue(PropertyState.p_mandatory, mandatory);
		((MirrorAPI) result).setRawValue(ReferenceState.p_name, propertyName);
		ReferenceKind refKind = (ReferenceKind) getEnumerationDefault(ReferenceKind.class);
		if (refKind == null) {
			refKind = ReferenceKind.Association_or_Composition;
		}
		// to solve a problem in getFinalValue/getDefaultValue in ReferenceState
		typeClass = typeClass == Proxiable.class ? InkObjectState.class : typeClass;
		((MirrorAPI) result).setRawValue(ReferenceState.p_kind, refKind);
		CoreClassDescriptor typeDesc = classElements.get(typeClass);
		if (typeDesc == null) {
			throw new CoreException("Could not resolve reference type " + typeClass.getName() + ", of class " + containerClass.getName());
		}
		CoreField fieldAnnot = f.getAnnotation(CoreField.class);
		if (fieldAnnot != null && !fieldAnnot.defaultValue().equals("")) {
			try {
				Object defaultValue = convertStringValue(fieldAnnot.defaultValue(), typeClass);
				CoreClassDescriptor attDesc = classElements.get(ReferenceState.class);
				byte loc = (Byte) attDesc.getStateClass().getField(P_DEFAULT_VALUE_NAME).get(null);
				((MirrorAPI) result).setRawValue(loc, defaultValue);
			} catch (Exception e) {
				throw new CoreException("Could not instantiate default value for property " + propertyName + ", of type " + containerClass.getName(), e);
			}
		}
		InkTypeState type = typeDesc.getObject();
		((MirrorAPI) result).setRawValue(ReferenceState.p_type, type);
		return result;
	}

	private PropertyState createMapProperty(Field f, Method m, String propertyName, Class<?> typeClass, Class<?> containerClass, boolean mandatory) throws Exception {
		Class<?> mapClass = MapPropertyState.class;
		CoreClassDescriptor mapDesc = classElements.get(mapClass);
		MapPropertyState result = (MapPropertyState) newInstance(null, mapClass, mapDesc.getNumberOfFields(), mapDesc.getNumberOfTraits(), false);
		((MirrorAPI) result).setRawValue(PropertyState.p_mandatory, mandatory);
		((MirrorAPI) result).setRawValue(ReferenceState.p_name, propertyName);
		InkTypeState type = (InkTypeState) elements.get(MAP).getObject();
		((MirrorAPI) result).setRawValue(MapPropertyState.p_type, type);
		try {
			ParameterizedType genericT = (ParameterizedType) m.getGenericReturnType();
			Type keyGenericT = genericT.getActualTypeArguments()[0];
			Type valueGenericT = genericT.getActualTypeArguments()[1];
			Class<?> keyClass = (Class<?>) keyGenericT;
			if (InkObject.class.isAssignableFrom(keyClass)) {
				keyClass = loadClass(keyClass.getName() + "State");
			}
			Class<?> valueClass;
			if (valueGenericT instanceof WildcardType) {
				valueClass = (Class<?>) ((WildcardType) valueGenericT).getUpperBounds()[0];
			} else {
				valueClass = (Class<?>) valueGenericT;
			}
			if (InkObject.class.isAssignableFrom(valueClass)) {
				valueClass = loadClass(valueClass.getName() + "State");
			}
			CoreMapField mapAnnot = f.getAnnotation(CoreMapField.class);
			if (mapAnnot == null) {
				throw new CoreException("The field " + propertyName + ", of class " + classElements.get(containerClass).getId() + ", must have Map annotation.");
			}
			DictionaryState dictionary = null;
			PropertyState keyProperty;
			PropertyState valueProperty;
			Class<?> dictionaryClass;
			CoreClassDescriptor dictionaryDesc;
			switch (mapAnnot.kind()) {
			case key_value:
				keyProperty = createProperty(f, m, mapAnnot.keyName(), keyClass, containerClass);
				valueProperty = createProperty(f, m, mapAnnot.valueName(), valueClass, containerClass);
				dictionaryClass = KeyValueDictionaryState.class;
				dictionaryDesc = classElements.get(dictionaryClass);
				dictionary = (DictionaryState) newInstance(null, dictionaryClass, dictionaryDesc.getNumberOfFields(), dictionaryDesc.getNumberOfTraits(), false);
				((MirrorAPI) dictionary).setRawValue(KeyValueDictionaryState.p_key, keyProperty);
				((MirrorAPI) dictionary).setRawValue(KeyValueDictionaryState.p_value, valueProperty);
				((MirrorAPI) dictionary).setRawValue(KeyValueDictionaryState.p_ordered, mapAnnot.ordered());
				((MirrorAPI) result).setRawValue(MapPropertyState.p_specifications, dictionary);
				break;
			case elements:
				valueProperty = createProperty(f, m, mapAnnot.valueName(), valueClass, containerClass);
				dictionaryClass = ElementsDictionaryState.class;
				dictionaryDesc = classElements.get(dictionaryClass);
				dictionary = (DictionaryState) newInstance(null, dictionaryClass, dictionaryDesc.getNumberOfFields(), dictionaryDesc.getNumberOfTraits(), false);
				((MirrorAPI) dictionary).setRawValue(ElementsDictionaryState.p_key_property, mapAnnot.keyName());
				((MirrorAPI) dictionary).setRawValue(ElementsDictionaryState.p_item, valueProperty);
				((MirrorAPI) dictionary).setRawValue(KeyValueDictionaryState.p_ordered, mapAnnot.ordered());
				((MirrorAPI) result).setRawValue(MapPropertyState.p_specifications, dictionary);
				break;
			default:
				throw new CoreException("Unknown map kind " + mapAnnot.kind());
			}
			if (mapAnnot.kind() == org.ink.core.vm.lang.internal.annotations.CoreMapField.Kind.key_value) {

			} else if (mapAnnot.kind() == org.ink.core.vm.lang.internal.annotations.CoreMapField.Kind.key_value) {

			}
		} catch (Exception e) {
			throw new CoreException("Could not resolve Map Property : " + propertyName + ", found in class " + classElements.get(containerClass).getId(), e);
		}
		return result;
	}

	private Class<?> extractItemClass(Type genericT) {
		Class<?> result = null;
		Type itemType = ((ParameterizedType) genericT).getActualTypeArguments()[0];
		if (itemType instanceof WildcardType) {
			result = (Class<?>) ((WildcardType) itemType).getUpperBounds()[0];
		} else {
			result = (Class<?>) itemType;
		}
		return result;
	}

	private PropertyState createListProperty(Field f, Method m, String propertyName, Class<?> paramClass, Class<?> containerClass, boolean mandatory) {
		ListPropertyState result = new ListPropertyState.Data();
		CoreClassDescriptor desc = classElements.get(ListPropertyState.class);
		((MirrorAPI) result).setCoreObject(desc.getNumberOfFields(), desc.getNumberOfTraits());
		((MirrorAPI) result).setRawValue(ListPropertyState.p_name, propertyName);
		InkTypeState type = (InkTypeState) elements.get(LIST).getObject();
		((MirrorAPI) result).setRawValue(ListPropertyState.p_type, type);
		try {
			Type genericT = m.getGenericReturnType();
			Class<?> itemClass = extractItemClass(genericT);
			if (InkObject.class.isAssignableFrom(itemClass)) {
				itemClass = loadClass(itemClass.getName() + "State");
			}
			CoreListField listAnnot = f.getAnnotation(CoreListField.class);
			if (listAnnot == null) {
				throw new CoreException("The field " + propertyName + ", of class " + classElements.get(containerClass).getId() + ", must have List annotation.");
			}
			PropertyState itemProperty = createProperty(f, m, listAnnot.itemName(), itemClass, containerClass);
			((MirrorAPI) result).setRawValue(ListPropertyState.p_list_item, itemProperty);
			((MirrorAPI) result).setRawValue(ListPropertyState.p_mandatory, listAnnot.mandatory());
		} catch (Exception e) {
			throw new CoreException("Could not resolve List Property : " + propertyName + ", found in class " + classElements.get(containerClass).getId(), e);
		}
		return result;
	}

	private PropertyState createEnumProperty(Field f, String propertyName, Class<?> enumClass, boolean mandatory) throws Exception {
		EnumAttributeState result = new EnumAttributeState.Data();
		CoreClassDescriptor desc = classElements.get(EnumAttributeState.class);
		((MirrorAPI) result).setCoreObject(desc.getNumberOfFields(), desc.getNumberOfTraits());
		((MirrorAPI) result).setRawValue(PropertyState.p_mandatory, mandatory);
		((MirrorAPI) result).setRawValue(PropertyState.p_name, propertyName);
		Object defaultValue = null;
		CoreField fieldAnnot = f.getAnnotation(CoreField.class);
		if (fieldAnnot != null && !fieldAnnot.defaultValue().equals("")) {
			try {
				defaultValue = fieldAnnot.defaultValue();
				// just checking that the enum string is valid
				convertStringValue(fieldAnnot.defaultValue(), enumClass);
				byte loc = EnumAttributeState.p_default_value;
				((MirrorAPI) result).setRawValue(loc, defaultValue);
			} catch (Exception e) {
				throw new CoreException("Could not instantiate default value for property " + propertyName + ", of type " + enumClass.getName(), e);
			}
		}
		if (defaultValue == null) {
			defaultValue = getEnumerationDefault(enumClass);
		}
		if (defaultValue != null) {
			defaultValue = defaultValue.toString();
		}
		((MirrorAPI) result).setRawValue(EnumAttributeState.p_default_value, defaultValue);
		InkTypeState type = (InkTypeState) elements.get(createEnumId(enumClass)).getObject();
		((MirrorAPI) result).setRawValue(EnumAttributeState.p_type, type);
		return result;
	}

	private Object getEnumerationDefault(Class<?> enumClass) throws Exception {
		String defaultEnumName = null;
		Field[] fields = enumClass.getFields();
		for (Field f : fields) {
			if (f.getAnnotation(CoreEnumField.class) != null) {
				if (f.getAnnotation(CoreEnumField.class).isDefault()) {
					if (defaultEnumName != null) {
						throw new CoreException("More than one default value was found for enumeration class " + enumClass.getName());
					}
					defaultEnumName = f.getName();
				}
			}
		}

		Object defaultValue = null;
		if (defaultEnumName != null) {
			defaultValue = enumClass.getMethod(InkNotations.Reflection.VALUE_OF_METHOD_NAME, new Class[] { String.class }).invoke(null, new Object[] { defaultEnumName });
		}
		return defaultValue;
	}

	@SuppressWarnings("unused")
	private Method extractSetter(Field f, Class<?> stateClass) {
		String setterName = f.getName();
		StringBuilder buf = new StringBuilder(setterName.length());
		char[] chars = setterName.toCharArray();
		buf.append(Character.toUpperCase(chars[2]));
		for (int i = 3; i < chars.length; i++) {
			if (chars[i] == '_') {
				i++;
				buf.append(Character.toUpperCase(chars[i]));
			} else {
				buf.append(chars[i]);
			}
		}
		setterName = "set" + buf.toString();
		Method[] methods = stateClass.getMethods();
		Method result = null;
		for (Method m : methods) {
			if (m.getName().equals(setterName)) {
				result = m;
				break;
			}
		}
		return result;
	}

	private Method extractGetter(Field f, Class<?> stateClass) {
		String getterName = f.getName();
		StringBuilder buf = new StringBuilder(getterName.length());
		char[] chars = getterName.toCharArray();
		buf.append(Character.toUpperCase(chars[2]));
		for (int i = 3; i < chars.length; i++) {
			if (chars[i] == '_') {
				i++;
				buf.append(Character.toUpperCase(chars[i]));
			} else {
				buf.append(chars[i]);
			}
		}
		getterName = "get" + buf.toString();
		Method[] methods = stateClass.getMethods();
		Method result = null;
		for (Method m : methods) {
			if (m.getName().equals(getterName)) {
				result = m;
				break;
			}
		}
		return result;
	}

	private void instantiateEnumerators() throws Exception {
		CoreClassDescriptorImpl desc = (CoreClassDescriptorImpl) elements.get(createId(EnumTypeState.class));
		for (Class<?> e : enums) {
			newEnumerator(e, desc);
		}
	}

	private String getRelativeJavaPackage(Class<?> c) {
		String result = c.getPackage().getName();
		result = result.substring(CoreNotations.CORE_PACKAGE.length() + 1, result.length());
		return result;
	}

	private void newEnumerator(Class<?> enumClass, CoreClassDescriptor classDesc) throws Exception {
		String id = createEnumId(enumClass);
		Object[] enumValues = (Object[]) enumClass.getMethod("values", (Class<?>[]) null).invoke(null, (Object[]) null);
		MirrorAPI object = newInstance(id, classDesc.getStateClass(), classDesc.getNumberOfFields(), classDesc.getNumberOfTraits(), true);
		List<String> values = new ArrayList<String>();
		for (Object o : enumValues) {
			values.add(o.toString());
		}
		(object).setRawValue(EnumTypeState.p_values, values);
		(object).setRawValue(EnumTypeState.p_java_path, getRelativeJavaPackage(enumClass));
		CoreObjectDescriptor desc = new CoreObjectDescriptorImpl(id, classDesc.getId(), classDesc.getStateClass(), object);
		elements.put(id, desc);

	}

	private String createEnumId(Class<?> enumClass) {
		String id = enumClass.getSimpleName();
		return createId(id);
	}

	private void fillObjects() throws Exception {
		PersonalityState personality;
		for (CoreObjectDescriptor desc : elements.values()) {
			if (desc.isClass()) {
				desc.getObject().setPropertyValue(InkClassState.p_properties, ((CoreClassDescriptor) desc).getProperties());
				addMetadata(desc);
				addFactory((CoreClassDescriptor) desc);
				personality = addTraits((CoreClassDescriptor) desc);
				addConstraints((CoreClassDescriptor) desc, personality);
			}
		}
	}

	private void addConstraints(CoreClassDescriptor ce, PersonalityState personality) throws Exception {
		CoreClassSpec metadata = ce.getMetadata();
		Class<?> constraintsClass = ConstraintsState.class;
		Class<? extends GenericInstanceValidatorState> genericValidatorClass = GenericInstanceValidatorState.class;
		if (metadata != null) {
			constraintsClass = metadata.constraintsClass();
			genericValidatorClass = metadata.genericValidatorClass();
		}
		CoreClassDescriptor constraintsDesc = classElements.get(constraintsClass);
		ConstraintsState constraints = (ConstraintsState) newInstance(null, constraintsClass, constraintsDesc.getNumberOfFields(), constraintsDesc.getNumberOfTraits(), false);
		((MirrorAPI) personality).setRawValue(PersonalityState.p_constraints, constraints);
		CoreClassDescriptor validatorDesc = classElements.get(genericValidatorClass);
		InstanceValidatorState validator = (InstanceValidatorState) newInstance(null, genericValidatorClass, validatorDesc.getNumberOfFields(), validatorDesc.getNumberOfTraits(), false);
		((MirrorAPI) constraints).setRawValue(ConstraintsState.p_generic_constraints, validator);
		CoreClassDescriptor tempClassDesc;
		if (constraintsClass == PropertyConstraintsState.class) {
			Class<? extends GenericPropertyValueValidatorState> genericPropertyValidatorClass = GenericPropertyValueValidatorState.class;
			CorePropertySpec propSpec = ce.getStateClass().getAnnotation(CorePropertySpec.class);
			tempClassDesc = classElements.get(genericValidatorClass);
			PropertyValueValidatorState propValidator = (PropertyValueValidatorState) newInstance(null, genericPropertyValidatorClass, tempClassDesc.getNumberOfFields(), tempClassDesc.getNumberOfTraits(), false);
			((MirrorAPI) constraints).setRawValue(PropertyConstraintsState.p_generic_property_value_constraints, propValidator);
			if (propSpec != null && propSpec.validatorsClasses() != null) {
				Map validators = new HashMap();
				Class[] vcs = propSpec.validatorsClasses();
				String[] keys = propSpec.keys();
				Class vc;
				for (int i = 0; i < vcs.length; i++) {
					vc = vcs[i];
					tempClassDesc = classElements.get(vc);
					propValidator = (PropertyValueValidatorState) newInstance(null, vc, tempClassDesc.getNumberOfFields(), tempClassDesc.getNumberOfTraits(), false);
					validators.put(keys[i], propValidator);
				}
				((MirrorAPI) constraints).setRawValue(PropertyConstraintsState.p_property_value_validators, validators);
			}
		}
		if (ValidatorState.class.isAssignableFrom(ce.getStateClass()) && ce.getStateClass() != ValidatorState.class && (metadata == null || metadata.javaMapping() != JavaMapping.State_Interface)) {
			ValidatorMessages vm = ce.getStateClass().getAnnotation(ValidatorMessages.class);
			if (vm == null) {
				throw new CoreException("Validator must have messages defined. Please fix " + ce.getId());
			}
			Map<String, MessageState> messages = new HashMap<String, MessageState>();
			String[] codes = vm.codes();
			String[] messagesIds = vm.messages();
			for (int i = 0; i < codes.length; i++) {
				messages.put(codes[i], (MessageState) elements.get(messagesIds[i]).getObject());
			}
			ce.getObject().setRawValue(ValidatorClassState.p_specific_messages, messages);
		}
		if (ce.getMetadata() != null && ce.getMetadata().validatorsClasses().length > 0) {
			Map validators = new HashMap();
			Class[] vcs = ce.getMetadata().validatorsClasses();
			String[] keys = ce.getMetadata().validatorsKeys();
			InstanceValidatorState iv;
			Class vc;
			for (int i = 0; i < vcs.length; i++) {
				vc = vcs[i];
				tempClassDesc = classElements.get(vc);
				iv = (InstanceValidatorState) newInstance(null, vc, tempClassDesc.getNumberOfFields(), tempClassDesc.getNumberOfTraits(), false);
				validators.put(keys[i], iv);
			}
			((MirrorAPI) constraints).setRawValue(ConstraintsState.p_validators, validators);
		}
	}

	private PersonalityState addTraits(CoreClassDescriptor ce) throws Exception {
		CoreClassSpec metadata = ce.getMetadata();
		Class<?> traitsClass = PersonalityState.class;
		Class<?> mirrorStateClass = MirrorState.class;
		if (metadata != null) {
			mirrorStateClass = metadata.mirrorClass();
			traitsClass = metadata.traitsClass();
		}
		CoreClassDescriptor desc = classElements.get(traitsClass);
		PersonalityState traits = (PersonalityState) newInstance(null, traitsClass, desc.getNumberOfFields(), desc.getNumberOfTraits(), false);
		CoreClassDescriptor classDesc = classElements.get(mirrorStateClass);
		MirrorAPI mirrorState = newInstance(null, mirrorStateClass, classDesc.getNumberOfFields(), classDesc.getNumberOfTraits(), false);
		Class<? extends ObjectEditorState> editorClassState = null;
		if (ClassMirrorState.class.isAssignableFrom(mirrorStateClass)) {
			editorClassState = ClassEditorState.class;
		} else {
			editorClassState = ObjectEditorState.class;
		}
		CoreClassDescriptor editorDesc = classElements.get(editorClassState);
		MirrorAPI editorObject = newInstance(null, editorClassState, editorDesc.getNumberOfFields(), editorDesc.getNumberOfTraits(), false);
		// InkObjectImpl<?> editorBehaviorInstance = (InkObjectImpl<?>) editorDesc.getBehaviorClass().newInstance();
		editorObject.boot(editorDesc.getObject(), factory, null, context, null);
		mirrorState.setRawValue(MirrorState.p_editor, editorObject);
		((MirrorAPI) traits).setRawValue(PersonalityState.p_reflection, mirrorState);
		ce.getObject().setPropertyValue(InkClassState.p_personality, traits);
		return traits;
	}

	private void addFactory(CoreClassDescriptor ce) {
		ObjectFactoryState factory = new ObjectFactoryState.Data();
		CoreClassDescriptor desc = classElements.get(ObjectFactoryState.class);
		((MirrorAPI) factory).setCoreObject(desc.getNumberOfFields(), desc.getNumberOfTraits());

		(ce.getObject()).setFactory(factory);
	}

	private void addMetadata(CoreObjectDescriptor ce) {
		if (!ce.getStateClass().equals(InkObjectState.class)) {
			Class<?> superClass = ce.getStateClass().getInterfaces()[0];
			String superId = createId(superClass);
			ce.getObject().setSuper(elements.get(superId).getObject());
		}
		if (ce.isClass()) {
			JavaMapping javaRepresentation = JavaMapping.State_Behavior_Interface;
			CoreClassSpec metadata = ((CoreClassDescriptor) ce).getMetadata();
			if (metadata != null) {
				javaRepresentation = metadata.javaMapping();
			}
			ce.getObject().setRawValue(InkClassState.p_java_mapping, javaRepresentation);
		}
	}

	private void instantiateCollectionTypes() throws Exception {
		newCollectionType(MAP, CollectionTypeState.class, CollectionTypeMarker.Map);
		newCollectionType(LIST, CollectionTypeState.class, CollectionTypeMarker.List);
	}

	private void newCollectionType(String id, Class<?> stateInteface, CollectionTypeMarker marker) throws Exception {
		String classId = createId(stateInteface);
		CoreClassDescriptor classDesc = (CoreClassDescriptor) elements.get(classId);
		MirrorAPI object = newInstance(id, stateInteface, classDesc.getNumberOfFields(), classDesc.getNumberOfTraits(), true);
		CoreObjectDescriptor desc = new CoreObjectDescriptorImpl(id, classId, stateInteface, object);
		(object).setRawValue(CollectionTypeState.p_type_marker, marker);
		elements.put(id, desc);
	}

	private void instantiatePrimitiveTypes() {
		CoreClassDescriptorImpl desc = (CoreClassDescriptorImpl) elements.get(createId(PrimitiveTypeState.class));
		String[] ids = new String[] { BOOLEAN, SHORT, BYTE, DATE, INTEGER, DOUBLE, FLOAT, STRING, LONG };
		String type;
		PrimitiveTypeMarker marker;
		for (String id : ids) {
			type = id.substring(id.indexOf(InkNotations.Path_Syntax.NAMESPACE_DELIMITER_C) + 1, id.length());
			marker = PrimitiveTypeMarker.valueOf(type);
			try {
				newPrimitive(desc, id, marker);
			} catch (Exception e) {
				throw new CoreException("Could not load " + id + ".", e);
			}
		}
	}

	private void instantiateCoreClassElements() throws Exception {
		newInkObject(InkObjectState.class);
		newInkObject(InkTypeState.class);
		newInkObject(InkClassState.class);
		newInkObject(StructClassState.class);
		newInkObject(Struct.class);
		newInkObject(TypedObjectState.class);
		newInkObject(DslFactoryState.class);
		newInkObject(ObjectFactoryState.class);
		newInkObject(PropertyState.class);
		newInkObject(ValuePropertyState.class);
		newInkObject(AttributeState.class);
		newInkObject(CollectionPropertyState.class);
		newInkObject(PropertyValueCalculatorState.class);
		newInkObject(TraitClassState.class);
		newInkObject(TargetLocatorState.class);
		newInkObject(TraitState.class);
		newInkObject(DslFactoryEventDispatcherState.class);
		newInkObject(DslFactoryEventListenerTraitState.class);
		newInkObject(DslFactoryEvent.class);
		newInkObject(DslFactoryPersonalityState.class);
		newInkObject(PersonalityState.class);
		newInkObject(ConstraintsState.class);
		newInkObject(ClassConstraintsState.class);
		newInkObject(PropertyConstraintsState.class);
		newInkObject(MessageClassState.class);
		newInkObject(MessageState.class);
		newInkObject(ValidatorState.class);
		newInkObject(ValidatorClassState.class);
		newInkObject(InstanceValidatorState.class);
		newInkObject(PropertyValueValidatorState.class);
		newInkObject(GenericInstanceValidatorState.class);
		newInkObject(GenericPropertyValueValidatorState.class);
		newInkObject(MirrorState.class);
		newInkObject(ClassMirrorState.class);
		newInkObject(TraitMirrorState.class);
		newInkObject(StructClassMirrorState.class);
		newInkObject(ObjectEditorClassState.class);
		newInkObject(ObjectEditorState.class);
		newInkObject(ClassEditorState.class);
		newInkObject(OperationState.class);
		newInkObject(OperationInterceptorState.class);
		newInkObject(PropertyMirrorState.class);
		newInkObject(ValuePropertyMirrorState.class);
		newInkObject(CollectionPropertyMirrorState.class);
		newInkObject(PrimitiveTypeState.class);
		newInkObject(CollectionTypeState.class);
		newInkObject(EnumTypeMirrorState.class);
		newInkObject(EnumTypeClassState.class);
		newInkObject(EnumTypeState.class);
		newInkObject(NumericTypeState.class);
		newInkObject(AcquiredPropertyValueState.class);
		newInkObject(PrimitiveAttributeState.class);
		newInkObject(NumericAttributeState.class);
		newInkObject(BooleanAttributeState.class);
		newInkObject(ByteAttributeState.class);
		newInkObject(DateAttributeState.class);
		newInkObject(DoubleAttributeState.class);
		newInkObject(EnumAttributeState.class);
		newInkObject(FloatAttributeState.class);
		newInkObject(IntegerAttributeState.class);
		newInkObject(ShortAttributeState.class);
		newInkObject(ListPropertyState.class);
		newInkObject(LongAttributeState.class);
		newInkObject(MapPropertyState.class);
		newInkObject(ReferenceState.class);
		newInkObject(StringAttributeState.class);
		newInkObject(ListPropertyMirrorState.class);
		newInkObject(MapPropertyMirrorState.class);
		newInkObject(PrimitiveAttributeMirrorState.class);
		newInkObject(ReferenceMirrorState.class);
		newInkObject(DictionaryState.class);
		newInkObject(KeyValueDictionaryState.class);
		newInkObject(ElementsDictionaryState.class);
		newInkObject(ContextState.class);
		newInkObject(DslLoaderState.class);
		newInkObject(DslRepositoryState.class);
		newInkObject(CoreLoaderState.class);
		newInkObject(ValidationContextClassState.class);
		newInkObject(ValidationContextState.class);
		newInkObject(ValidationMessageClassState.class);
		newInkObject(ValidationMessageState.class);
		newInkObject(StringAttributeValueValidatorState.class);
		newInkObject(StringAttributeValidatorState.class);
		newInkObject(NumericAttributeValueValidatorState.class);
		newInkObject(NumericAttributeValidatorState.class);
		newInkObject(EnumAttributeValueValidatorState.class);
		newInkObject(EnumAttributeValidatorState.class);
		newInkObject(InkReaderState.class);
		newInkObject(EmptyDslLoaderState.class);
		newInkObject(ClassHierarchyLocatorState.class);
		newInkObject(ValidationInterceptorState.class);
	}

	private <A extends Annotation> A findAnnotation(Class stateClass, Class<A> annotationClass) {
		A result = (A) stateClass.getAnnotation(annotationClass);
		if (result == null && !stateClass.equals(InkObjectState.class)) {
			Class<?> superClass = stateClass.getInterfaces()[0];
			result = findAnnotation(superClass, annotationClass);
		}
		return result;
	}

	private void newInkObject(Class<?> stateInterface) throws Exception {
		try {
			String id = createId(stateInterface);
			Class<?> metaClass = InkClassState.class;
			JavaMapping mapping = JavaMapping.State_Behavior_Interface;
			boolean isAbstract = false;
			Scope scope = Scope.all;
			CoreClassSpec annot = findAnnotation(stateInterface, CoreClassSpec.class);
			if (annot != null) {
				metaClass = annot.metaclass();
				mapping = annot.javaMapping();
				isAbstract = annot.isAbstract();
				scope = annot.scope();
			}
			String metaClassId = createId(metaClass);
			Field[] metaFields = getFields(metaClass);
			byte numberOfTraits = getTraitsCount(metaClass);
			InkClassState.Data object = (InkClassState.Data) newInstance(id, metaClass, (byte) metaFields.length, numberOfTraits, true);
			object.setId(id);
			Field[] fields = getFields(stateInterface);
			object.setRawValue(InkClassState.p_java_path, getRelativeJavaPackage(stateInterface));
			object.setRawValue(InkClassState.p_can_cache_behavior_instance, true);
			object.setRawValue(InkClassState.p_properties, new ArrayList<PropertyState>());
			object.setRawValue(InkClassState.p_java_mapping, mapping);
			object.setAbstract(isAbstract);
			object.setScope(scope);
			CoreClassDescriptorImpl desc = new CoreClassDescriptorImpl(id, metaClassId, stateInterface, object, fields, numberOfTraits);
			desc.setMetadata(annot);
			elements.put(id, desc);
			classElements.put(stateInterface, desc);
		} catch (Exception e) {
			throw new CoreException("Could not load " + stateInterface.getName() + ".", e);
		}
	}

	private MirrorAPI newInstance(String id, Class<?> stateInterface, byte numberOfFields, byte numberOfTraits, boolean isRoot) throws Exception {
		MirrorAPI result = (MirrorAPI) getDataClass(stateInterface).newInstance();
		result.setId(id);
		result.setRoot(isRoot);
		result.setCoreObject(numberOfFields, numberOfTraits);
		return result;
	}

	private Class<?> getDataClass(Class<?> stateInterface) throws ClassNotFoundException {
		String name = stateInterface.getName() + "$Data";
		return loadClass(name);
	}

	private Class<?> loadClass(String name) throws ClassNotFoundException {
		try {
			return Thread.currentThread().getContextClassLoader().loadClass(name);
		} catch (ClassNotFoundException e) {
			throw e;
		}
	}

	private String createId(Class<?> stateInterface) {
		String id;
		id = stateInterface.getSimpleName();
		if (id.indexOf("State") > 0) {
			id = id.substring(0, id.indexOf("State"));
		}
		id = createId(id);
		return id;
	}

	public String createId(String shortId) {
		return CoreNotations.CORE_NAMESPACE + InkNotations.Path_Syntax.NAMESPACE_DELIMITER_C + shortId;
	}

	private void newPrimitive(CoreClassDescriptor classDesc, String id, PrimitiveTypeMarker marker) throws Exception {
		MirrorAPI typeInstance = newInstance(id, PrimitiveTypeState.class, classDesc.getNumberOfFields(), classDesc.getNumberOfTraits(), true);
		(typeInstance).setRawValue(PrimitiveTypeState.p_type_marker, marker);
		CoreObjectDescriptor desc = new CoreObjectDescriptorImpl(id, classDesc.getId(), null, typeInstance);
		elements.put(id, desc);
	}

	private void arrangeFields(Field[] fields, int currentLoc, int listLoc, List<Field> resultList, Class<?> currentClass) {
		Field f;
		boolean toContinue = true;
		for (int i = currentLoc; i < fields.length && toContinue; i++) {
			f = fields[i];
			if (f.getDeclaringClass() == currentClass) {
				if (resultList.size() <= listLoc) {
					resultList.add(null);
					resultList.set(listLoc, f);
				} else {
					resultList.add(listLoc, f);
				}
			} else {
				toContinue = false;
				arrangeFields(fields, i, 0, resultList, f.getDeclaringClass());
			}
			listLoc++;
		}
	}

	private Field[] getFields(Class<?> interfaceClass) {
		List<Field> result = new ArrayList<Field>();
		List<Field> temp = new ArrayList<Field>();
		Field[] fields = interfaceClass.getFields();
		arrangeFields(fields, 0, 0, temp, interfaceClass);
		for (Field f : temp) {
			if (f.getName().startsWith(PROPERTY_PREFIX)) {
				result.add(f);
			}
		}
		return result.toArray(new Field[] {});
	}

	private byte getTraitsCount(Class<?> interfaceClass) {
		byte count = 0;
		Field[] fields = interfaceClass.getFields();
		for (Field f : fields) {
			if (f.getName().startsWith(TRAIT_PREFIX)) {
				count++;
			}
		}
		return count;
	}

	public String getEnumClassName(EnumTypeState enumState) {
		return CoreNotations.CORE_PACKAGE + "." + ((MirrorAPI) enumState).getRawValue(EnumTypeState.p_java_path) + "." + CoreUtils.getShortId(enumState.getId());
	}

	@Override
	public List<File> getInkFiles() {
		return new ArrayList<File>();
	}

}