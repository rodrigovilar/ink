package org.ink.core.vm.test.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.ink.core.vm.constraints.ValidationContext;
import org.ink.core.vm.exceptions.WeaveException;
import org.ink.core.vm.factory.Context;
import org.ink.core.vm.factory.DslFactoryEventDispatcher;
import org.ink.core.vm.factory.DslFactoryState;
import org.ink.core.vm.factory.InkVM;
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
 * 
 * <p><!--$Id: CoreBasicTests.java,v 1.10 2010/08/03 12:54:00 liors Exp $-->
 * <ul>
 * <li>$Log: CoreBasicTests.java,v $
 * <li>Revision 1.10  2010/08/03 12:54:00  liors
 * <li>customer dsl
 * <li>
 * <li>Revision 1.9  2010/07/27 16:43:49  liors
 * <li>*** empty log message ***
 * <li>
 * <li>Revision 1.8  2010/07/27 16:40:27  liors
 * <li>customer dsl
 * <li>
 * <li>Revision 1.7  2010/07/24 14:42:27  liors
 * <li>cutomer dsl
 * <li>
 * <li>Revision 1.6  2010/07/20 14:52:09  liors
 * <li>eclipse 3.6 fixes
 * <li>
 * <li>Revision 1.5  2010/07/17 16:46:43  liors
 * <li>lots of fixes
 * <li>
 * <li>Revision 1.4  2010/07/13 15:50:57  liors
 * <li>first DSL
 * <li>
 * <li>Revision 1.3  2010/07/10 17:52:22  liors
 * <li>dsl + vm stuf
 * <li>
 * <li>Revision 1.2  2010/06/29 17:17:16  liors
 * <li>customer dsl
 * <li>
 * <li>Revision 1.1  2010/06/15 17:02:43  liors
 * <li>ink parsing
 * <li>
 * <li>Revision 1.40  2010/06/01 13:46:53  liors
 * <li>string validations + bug fixes
 * <li>
 * <li>Revision 1.39  2010/05/11 14:00:51  liors
 * <li>core validations
 * <li>
 * <li>Revision 1.38  2010/05/11 09:19:13  liors
 * <li>validation framework finaly works
 * <li>
 * <li>Revision 1.37  2010/05/04 14:49:57  liors
 * <li>validation framework
 * <li>
 * <li>Revision 1.36  2010/04/27 17:01:48  liors
 * <li>ability to define instances
 * <li>
 * <li>Revision 1.35  2010/03/16 19:44:41  liors
 * <li>move method
 * <li>
 * <li>Revision 1.34  2010/03/16 19:29:32  liors
 * <li>asdf
 * <li>
 * <li>Revision 1.33  2010/03/16 13:40:50  liors
 * <li>refactor - rename
 * <li>
 * <li>Revision 1.32  2010/03/16 10:14:26  liors
 * <li>java mapping features
 * <li>
 * <li>Revision 1.31  2010/03/13 19:06:35  liors
 * <li>detachable trait testings
 * <li>
 * <li>Revision 1.30  2010/03/13 12:07:31  liors
 * <li>detachable trait testings
 * <li>
 * <li>Revision 1.29  2010/03/13 09:57:47  liors
 * <li>java mapping feature
 * <li>
 * <li>Revision 1.28  2010/03/09 17:09:27  liors
 * <li>detachable trait
 * <li>
 * <li>Revision 1.27  2010/03/09 15:20:04  liors
 * <li>detachable trait
 * <li>
 * <li>Revision 1.26  2010/02/23 18:50:15  liors
 * <li>dispatcher
 * <li>
 * <li>Revision 1.25  2010/02/23 17:53:18  liors
 * <li>fix personality bug
 * <li>
 * <li>Revision 1.24  2010/02/23 14:34:57  liors
 * <li>small fixes
 * <li>
 * <li>Revision 1.23  2010/02/23 13:02:57  liors
 * <li>weaving tests + bug fixes
 * <li>
 * <li>Revision 1.22  2010/02/23 12:52:56  liors
 * <li>weaving tests + bug fixes
 * <li>
 * <li>Revision 1.21  2010/02/20 14:16:11  liors
 * <li>fix the test
 * <li>
 * <li>Revision 1.20  2010/02/20 14:04:10  liors
 * <li>fix the test
 * <li>
 * <li>Revision 1.19  2010/02/20 13:52:35  liors
 * <li>fix the test
 * <li>
 * <li>Revision 1.18  2010/02/16 19:24:53  liors
 * <li>bug
 * <li>
 * <li>Revision 1.17  2010/02/16 15:56:16  liors
 * <li>big big commit
 * <li>
 * <li>Revision 1.16  2010/02/07 16:22:11  lorenz
 * <li>testInk
 * <li>
 * <li>Revision 1.15  2010/02/07 14:57:14  lorenz
 * <li>more tests
 * <li>
 * <li>Revision 1.14  2010/02/06 23:27:26  lorenz
 * <li>cvs bug fixed
 * <li>
 * <li>Revision 1.13  2010/02/06 14:13:22  lorenz
 * <li>inkTest improved
 * <li>
 * </ul>
 */
public class CoreBasicTests extends TestCase{
	
	private Context context = InkVM.instance().getContext();
	
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
		referenceAttribute.setName("myoBJECT");
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
		List<Property> properties = (List<Property>)mirror.getPropertyValue(InkClassState.p_properties);
		assertNotNull(properties);
		assertFalse(properties.isEmpty());
		for(Property prop : properties){
			assertTrue(mirror==prop.reflect().getOwner());
			assertTrue(InkClassState.p_properties==prop.reflect().getDefiningPropertyIndex());
		}
		//test MirrorProxy
		Property prop = properties.get(0);
		InkObject type = prop.getType();
		assertTrue(type.reflect().getOwner().getOwner()==mirror);
		assertTrue(type.reflect().getDefiningPropertyIndex()==PropertyState.p_type);
	}
	
	public void testDefaultValue(){
		InkClass inkClass = context.getObject(CoreNotations.Ids.INK_CLASS);
		assertNotNull(inkClass);
		InkClassState o = (InkClassState) inkClass.newInstance();
		assertNotNull(o.getComponentType());
		assertTrue(o.getJavaMapping().hasBeahvior());
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
		InkClassState subClass = superClass.cloneState();
		((ClassMirrorAPI)subClass).setJavaMapping(JavaMapping.No_Java);
		((ClassMirrorAPI)subClass).setSuper(superClass);
		((ClassMirrorAPI)subClass).afterPropertiesSet();
		//assemble properties
		StringAttributeState stringState2 = stringState.cloneState();
		List props = new ArrayList();
		List<? extends Property> existingProperties =  subClass.getProperties();
		for(Property prop : existingProperties){
			props.add(prop.cloneState());
		}
		props.add(stringState2);
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

	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
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
		List<PropertyState> props = new ArrayList<PropertyState>();
		props.add(stringState);
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
		if (!zuperImplName.equals(zuperImplNameExp))
			System.out.println(baseImplName 
					+ ": Expected to extend " + zuperImplNameExp 
					+ " but extending instead " + zuperImplName);
		final boolean isInkObject = base==InkObject.class;
		assertTrue(isInkObject || zuperImplName.equals(zuperImplNameExp));

		//baseImpl implements correctly?
		assertTrue(baseImpl.getInterfaces().length==1);
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
			if (!zuperStateName.equals(zuperStateNameExp))
				System.out.println("Expected " + zuperStateNameExp 
						+ " but found " + zuperState);
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

		
	
