package org.ink.codegen.velocity.class_gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ink.codegen.velocity.VelocityGenerator;
import org.ink.codegen.velocity.VelocityGeneratorImpl;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.mirror.ClassMirror;

public class ClassGeneratorImpl<S extends ClassGeneratorState> extends
		VelocityGeneratorImpl<S> implements VelocityGenerator {
	
	
	@Override
	protected void prepareContext(Map context) {
		super.prepareContext(context);
		ClassDescriptor clsDesc = newClassDescriptor();
		ClassMirror cMirror = findTargetClass();
		clsDesc.id = cMirror.getShortId();
		clsDesc.fullId = cMirror.getId();
		clsDesc.javaPath = cMirror.getFullJavaPackage();
		clsDesc.superID = cMirror.getSuper().getId();
		clsDesc.description = cMirror.getDescription();
		addClassData(clsDesc, cMirror);
		List<PropertyDescriptor> props = new ArrayList<PropertyDescriptor>();
		for(PropertyMirror pm : cMirror.getClassPropertiesMirrors()){
			PropertyDescriptor pd = newPropertyDescriptor();
			pd.name = pm.getName();
			pd.javaType = pm.getTypeClass().getName();
			addPropertyData(pm, pd);
			props.add(pd);
		}
		clsDesc.properties = props;
		//todo - finish
		context.put("target", clsDesc);
	}

	protected ClassMirror findTargetClass() {
		return getState().getTarget().reflect();
	}

	protected PropertyDescriptor newPropertyDescriptor() {
		return new PropertyDescriptor();
	}

	protected ClassDescriptor newClassDescriptor() {
		return new ClassDescriptor();
	}

	protected void addClassData(ClassDescriptor clsDesc, ClassMirror cMirror) {
	}

	protected void addPropertyData(PropertyMirror pm, PropertyDescriptor pd) {
	}
	

}