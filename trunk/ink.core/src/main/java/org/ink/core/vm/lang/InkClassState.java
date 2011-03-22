package org.ink.core.vm.lang;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ink.core.vm.exceptions.CoreException;
import org.ink.core.vm.exceptions.WeaveException;
import org.ink.core.vm.factory.DslFactory;
import org.ink.core.vm.lang.internal.ClassMirrorAPI;
import org.ink.core.vm.lang.internal.MirrorAPI;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;
import org.ink.core.vm.lang.internal.annotations.CoreField;
import org.ink.core.vm.lang.internal.annotations.CoreListField;
import org.ink.core.vm.lang.operation.Operation;
import org.ink.core.vm.lang.operation.OperationState;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.mirror.ClassMirrorState;
import org.ink.core.vm.traits.Personality;
import org.ink.core.vm.traits.PersonalityState;
import org.ink.core.vm.traits.Trait;
import org.ink.core.vm.traits.TraitClass;
import org.ink.core.vm.types.ObjectTypeMarker;
import org.ink.core.vm.utils.CoreUtils;

/**
 * @author Lior Schachter
 */
@CoreClassSpec(mirrorClass=ClassMirrorState.class, javaMapping=JavaMapping.State_Behavior_Interface
		/*,finalValues={"Root"}, finalValuesLocation={InkClassState.p_component_type}*/)
public interface InkClassState extends InkTypeState{

	@CoreField(valuePropagationStrategy=InheritanceConstraints.Instance_Must_Override_Inherited_Value)
	public static final byte p_java_path = 0;

	//TODO - should be mandatory true
	@CoreField(mandatory=false, valuePropagationStrategy=InheritanceConstraints.Instance_Must_Override_Inherited_Value)
	public static final byte p_description = 1;
	@CoreField(mandatory=true, defaultValue="State_Behavior_Interface", valuePropagationStrategy=InheritanceConstraints.Instance_Must_Override_Inherited_Value)
	public static final byte p_java_mapping = 2;
	@CoreField(defaultValue="true")
	public static final byte p_can_cache_behavior_instance = 3;
	@CoreField(defaultValue="Root_or_Pure_Component")
	public static final byte p_component_type = 4;
	@CoreListField(itemName="property")
	public static final byte p_properties = 5;
	@CoreListField(itemName="operation")
	public static final byte p_operations = 6;
	public static final byte p_personality = 7;

	public String getDescription();
	public void setDescription(String value);

	public JavaMapping getJavaMapping();
	public void setJavaMapping(JavaMapping value);

	public List<? extends Property> getProperties();
	public void setProperties(List<? extends PropertyState> value);

	public List<? extends Operation> getOperations();
	public void setOperations(List<? extends OperationState> value);

	public Personality getPersonality();
	public void setPersonality(PersonalityState value);

	public String getJavaPath();
	public void setJavaPath(String value);

	public Boolean getCanCacheBehaviorInstance();
	public void setCanCacheBehaviorInstance(Boolean value);

	public ComponentType getComponentType();
	public void setComponentType(ComponentType value);

	public class Data extends InkTypeState.Data implements ClassMirrorAPI{

		private ObjectFactoryState factory = null;
		private Map<String, Byte> classPropertiesIndexes;
		private PropertyMirror[] classPropsMirrors;
		private Class<InkObjectState> dataClass;
		private Class<?> stateInterface;
		private Class<? extends InkObject> behaviorClass;
		private Class<? extends InkObject> interfaceClass;
		private Class<?>[] behaviorProxyInterfaces;
		private boolean isMetaClass = false;
		private byte[] classRealPropertiesIndex;
		private byte offset;
		private Trait[] detachableRoles = null;
		private Map<String, Byte> detachableTraitsRolesToIndexes = null;
		private Map<String, Byte> detachableTraitsIdsToIndexes = null;
		private boolean created = false;

		@Override
		public void bootClass(InkClassState c, PropertyMirror[] propsMirrors, PropertyMirror[] intancePropsMirrors, Map<String, Byte> propertiesIndexes, Map<String, Byte> instancePropertiesIndexes, Class<?>[] behaviorProxyInterfaces, DslFactory context, Class<InkObjectState> dataClass){
			if(!isCoreObject()){
				throw new CoreException("This method should be used for core objects only !!");
			}
			this.classPropsMirrors = propsMirrors;
			this.propsMirrors = intancePropsMirrors;
			this.classPropertiesIndexes = propertiesIndexes;
			this.propertiesIndexes = instancePropertiesIndexes;
			this.myClass = (ClassMirrorAPI)c;
			this.myFactory = context;
			this.dataClass = dataClass;
			this.behaviorProxyInterfaces = behaviorProxyInterfaces;
			//TODO this should go away once we have metaclas class.
			if(InkClassState.Data.class.isAssignableFrom(dataClass)){
				isMetaClass = true;
			}
			classRealPropertiesIndex = new byte[classPropsMirrors.length];
			for(byte i=0;i<classRealPropertiesIndex.length;i++){
				classRealPropertiesIndex[i]=i;
			}
		}

		protected Class<InkObjectState> resolveDataClass(){
			if(!this.getJavaMapping().hasState()){
				return ((ClassMirrorAPI)getSuper()).getDataClass();
			}
			return myFactory.resolveDataClass(this);
		}

		@Override
		public Map<String, Byte> getClassPropertiesIndexes() {
			return classPropertiesIndexes;
		}

		@Override
		public <T extends InkObjectState> T cloneState(boolean identicalTwin) {
			T result = super.cloneState(identicalTwin);
			((ClassMirrorAPI)result).setFactory((ObjectFactoryState)this.factory.cloneState());
			return result;
		}

		@Override
		public ObjectFactory getFactory() {
			return (ObjectFactory) this.factory.getBehavior();
		}

		@Override
		public ObjectFactoryState getFactoryState() {
			return this.factory;
		}

		@SuppressWarnings("unchecked")
		@Override
		public List<? extends Operation> getOperations() {
			return (List<? extends Operation>) getValue(p_operations);
		}

		@SuppressWarnings("unchecked")
		@Override
		public List<? extends Property> getProperties() {
			return (List<? extends Property>) getValue(p_properties);
		}

		@Override
		public void setFactory(ObjectFactoryState value) {
			this.factory = value;
		}

		@Override
		public void setOperations(List<? extends OperationState> value) {
			setValue(p_operations, value);
		}

		@Override
		public void setProperties(List<? extends PropertyState> value) {
			setValue(p_properties, value);
		}

		@Override
		public Personality getPersonality() {
			return (Personality) getValue(p_personality);
		}

		@Override
		public void setPersonality(PersonalityState value) {
			setValue(p_personality, value);
		}

		@Override
		public String getJavaPath() {
			return (String)getValue(p_java_path);
		}

		@Override
		public void setJavaPath(String value) {
			setValue(p_java_path, value);
		}

		@Override
		public Boolean getCanCacheBehaviorInstance() {
			return (Boolean)getValue(p_can_cache_behavior_instance);
		}

		@Override
		public void setCanCacheBehaviorInstance(Boolean value) {
			setValue(p_can_cache_behavior_instance, value);
		}

		@Override
		public PropertyMirror[] getClassPropertiesMirrors() {
			return classPropsMirrors;
		}

		@Override
		public void applyProperties(List<Property> properties){
			//TODO - add basic validation (properties contains getProperties)
			if(properties!=null){
				List<? extends Property> originalProperties = getProperties();
				classRealPropertiesIndex = new byte[originalProperties.size()];
				classPropertiesIndexes = new HashMap<String, Byte>(properties.size());
				classPropsMirrors = new PropertyMirror[properties.size()];
				Property prop;
				byte counter=0;
				for(byte i=0;i<properties.size();i++){
					prop = properties.get(i);
					for(byte k=counter;k<originalProperties.size();k++){
						if(originalProperties.get(k).getName().equals(prop.getName())){
							classRealPropertiesIndex[counter]=i;
							counter++;
							break;
						}
					}
					classPropsMirrors[i] = prop.reflect();
					//currently can't add detachable traits to core elements need to find a way to allow this
					if(created && (!isCoreObject())){
						//if the class was created then instances of it hold a pointer to the property mirror
						//need to create a new object
						classPropsMirrors[i] = classPropsMirrors[i].asTrait(t_reflection, true);
					}
					classPropsMirrors[i].bind((ClassMirror) reflect(), i);
					classPropertiesIndexes.put(prop.getName(), i);
				}
			}else{
				classPropertiesIndexes = new HashMap<String, Byte>();
				classPropsMirrors = new PropertyMirror[0];
				classRealPropertiesIndex = new byte[0];
			}
			getFactory().bind((ClassMirror)reflect());
		}

		@SuppressWarnings("unchecked")
		@Override
		public void afterPropertiesSet(){
			super.afterPropertiesSet();
			applyProperties((List<Property>) getProperties());
			behaviorClass = resolveBehaviorClass();
			interfaceClass = resolveInterfaceClass();
			behaviorProxyInterfaces = CoreUtils.getBehaviorProxyInterfaces(behaviorClass);
			dataClass = resolveDataClass();
			stateInterface = dataClass.getInterfaces()[0];
			if(stateInterface.equals(MirrorAPI.class)){
				stateInterface = InkObjectState.class;
			}else if(stateInterface.equals(ClassMirrorAPI.class)){
				stateInterface = InkClassState.class;
			}
			Personality personality = getPersonality();
			if(personality!=null){
				offset = personality.getTraitsCount();
				try {
					personality.bind((ClassMirror)this.reflect());
				} catch (WeaveException e) {
					throw new CoreException(e);
				}
			}
			created = true;
		}

		protected Class<? extends InkObject> resolveBehaviorClass() {
			if(this.isAbstract()){
				return InkObjectImpl.class;
			}
			if(!this.getJavaMapping().hasBeahvior()){
				return ((ClassMirrorAPI)getSuper()).getBehaviorClass();
			}else{
				return myFactory.resolveBehaviorClass(this);
			}
		}

		private Class<? extends InkObject> resolveInterfaceClass() {
			if(!this.getJavaMapping().hasInterface()){
				return ((ClassMirrorAPI)getSuper()).getInterfaceClass();
			}else{
				return myFactory.resolveInterfaceClass(this);
			}
		}

		@Override
		public boolean isClass(){
			return true;
		}

		@Override
		public boolean isMetaClass(){
			return isMetaClass;
		}

		@Override
		public Class<InkObjectState> getDataClass(){
			return dataClass;
		}

		@Override
		public Class<? extends InkObject> getBehaviorClass(){
			return behaviorClass;
		}

		@Override
		public Class<? extends InkObject> getInterfaceClass(){
			return interfaceClass;
		}

		@Override
		public Class<?>[] getBehaviorProxyInterfaces(){
			return behaviorProxyInterfaces;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Class<InkObjectState> getStateInterface(){
			return (Class<InkObjectState>) stateInterface;
		}

		@Override
		public ComponentType getComponentType() {
			return (ComponentType)getValue(p_component_type);
		}

		@Override
		public void setComponentType(ComponentType value) {
			setValue(p_component_type, value);
		}

		@Override
		public void init(InkClassState c, DslFactory factory) {
			super.init(c, factory);
			this.factory = factory.newInstance(factory.getAppContext(), false, true);
		}

		@Override
		public ObjectTypeMarker getObjectTypeMarker() {
			if(isMetaClass()){
				return ObjectTypeMarker.Metaclass;
			}
			return ObjectTypeMarker.Class;
		}

		@Override
		public String getDescription() {
			return (String)getValue(p_description);
		}

		@Override
		public JavaMapping getJavaMapping() {
			return (JavaMapping)getValue(p_java_mapping);
		}

		@Override
		public void setDescription(String value) {
			setValue(p_description, value);
		}

		@Override
		public void setJavaMapping(JavaMapping value) {
			setValue(p_java_mapping, value);
		}

		@Override
		public byte[] getRealPropertiesIndex(){
			return classRealPropertiesIndex;
		}

		@Override
		public synchronized void addRole(String namespace, String role, Trait t) throws WeaveException{
			String qualifiedRole = role;
			if(!namespace.equals(getNamespace())){
				qualifiedRole = namespace +"." + role;
			}
			if(getPersonality().hasRole(qualifiedRole)){
				throw new WeaveException("The class '" + getId() +"', already contains the role '" + qualifiedRole +"'.");
			}
			Map<String, Byte> temp1 = new HashMap<String, Byte>();
			String traitClassId = t.getMeta().reflect().getId();
			if(detachableTraitsRolesToIndexes!=null){
				if(detachableTraitsRolesToIndexes.containsKey(qualifiedRole)){
					throw new WeaveException("The class '" + getId() +"', already contains the role '" + qualifiedRole +"'.");
				}else if(detachableTraitsIdsToIndexes.containsKey(traitClassId)){
					throw new WeaveException("The class '" + getId() +"', already contains the a role with the same behavior class '" + traitClassId +"'.");
				}
				temp1.putAll(detachableTraitsRolesToIndexes);
			}
			Map<String, Byte> temp2 = new HashMap<String, Byte>();
			Trait[] temp3 = null;
			Byte index = null;
			if(detachableRoles==null){
				index = 0;
				temp3 = new Trait[1];
			}else{
				temp3 = new Trait[detachableRoles.length + 1];
				index = (byte) detachableRoles.length;
				System.arraycopy(detachableRoles, 0, temp3, 0, detachableRoles.length);
			}
			temp1.put(qualifiedRole, index);
			temp2.put(traitClassId, index);
			temp3[index] = t.cloneState().getBehavior();
			detachableRoles = temp3;
			detachableTraitsRolesToIndexes = temp1;
			detachableTraitsIdsToIndexes = temp2;
		}

		@Override
		public boolean hasRole(String namespace, String role){
			if(detachableTraitsRolesToIndexes==null){
				return getPersonality().hasRole(role);
			}
			String qualifiedRole = role;
			if(!namespace.equals(getNamespace())){
				qualifiedRole = namespace +"." + role;
				return detachableTraitsRolesToIndexes.containsKey(qualifiedRole);
			}else{
				return detachableTraitsRolesToIndexes.containsKey(qualifiedRole) | getPersonality().hasRole(role);
			}

		}

		@Override
		public Byte getTraitIndex(String role) {
			Byte result = detachableTraitsRolesToIndexes.get(role);
			if(result!=null){
				result = (byte) (result+offset);
			}
			return  result;
		}

		@Override
		public Byte getTraitIndex(TraitClass traitClass) {
			Byte result = detachableTraitsIdsToIndexes.get(traitClass.reflect().getId());
			if(result!=null){
				result = (byte) (result+offset);
			}
			return  result;
		}

		@Override
		public Trait getDetachableRole(byte index) {
			byte loc = (byte) (index - offset);
			if(loc < detachableRoles.length){
				return detachableRoles[loc];
			}
			return null;
		}

		@Override
		public int getTraitsCount() {
			return getPersonality().getTraitsCount() + (detachableRoles==null?0:detachableRoles.length);
		}

	}

}
