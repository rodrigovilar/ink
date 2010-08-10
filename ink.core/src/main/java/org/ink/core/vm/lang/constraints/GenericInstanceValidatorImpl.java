package org.ink.core.vm.lang.constraints;

import org.ink.core.vm.constraints.InstanceValidator;
import org.ink.core.vm.constraints.SystemState;
import org.ink.core.vm.constraints.ValidationContext;
import org.ink.core.vm.constraints.ValidatorImpl;
import org.ink.core.vm.lang.DataTypeMarker;
import org.ink.core.vm.lang.InheritanceConstraints;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.mirror.Mirror;

/**
 * @author Lior Schachter
 */
public class GenericInstanceValidatorImpl<S extends GenericInstanceValidatorState> extends ValidatorImpl<S> implements InstanceValidator{

	@Override
	public void validate(InkObjectState target, Mirror targetSuper,
			ValidationContext context, SystemState systemState) {
		ClassMirror cMirror = target.getMeta().reflect();
		checkComponentType(target, context, cMirror);
		if(targetSuper!=null){
			checkRefinement(target, targetSuper, context, cMirror);
		}
	}

	private void checkRefinement(InkObjectState target, Mirror targetSuper,
			ValidationContext context, ClassMirror cMirror) {
		Mirror targetMirror = target.reflect();
		if(targetMirror.isRoot()){
			ClassMirror cm = target.getMeta().reflect();
			if(!cm.isSubClassOf(targetSuper.getClassMirror())){
				context.addError(target, this, "refinement.violation", target.reflect().getId(), cm.getId(), targetSuper.getClassMirror().getId());
			}
		}else{
			PropertyMirror pm = targetMirror.getDefiningProperty();
			if(pm.getTypeMarker()==DataTypeMarker.Class && pm.getInheritanceConstraints()==InheritanceConstraints.Instance_Can_Refine_Inherited_Value){
				ClassMirror cm = target.getMeta().reflect();
				if(!cm.isSubClassOf(targetSuper.getClassMirror())){
					//TODO - should use path here
					context.addError(target, this, "refinement.violation", pm.getName(), cm.getId(), targetSuper.getClassMirror().getId());
				}
			}
		}
	}

	protected void checkComponentType(InkObjectState target,
			ValidationContext context, ClassMirror cMirror) {
		switch(cMirror.getComponentType()){
		case Pure_Component:
			if(target.reflect().isRoot()){
				context.addError(target, this, "component.type.violation", target.getId(), cMirror.getComponentType().toString());
			}
			break;
		case Root:
			if(!target.reflect().isRoot()){
				context.addError(target, this, "component.type.violation", target.getId(), cMirror.getComponentType().toString());
			}
			break;
		}
	}

	
}
