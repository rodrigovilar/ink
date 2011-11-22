package org.ink.core.vm.lang;

import org.ink.core.vm.constraints.ClassConstraintsState;
import org.ink.core.vm.lang.internal.ClassMirrorAPI;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;
import org.ink.core.vm.mirror.StructClassMirrorState;

/**
 * @author Lior Schachter
 */
@CoreClassSpec(constraintsClass=ClassConstraintsState.class, mirrorClass=StructClassMirrorState.class, javaMapping=JavaMapping.Only_State,
		finalValues={"Only_State"}, finalValuesLocation={InkClassState.p_java_mapping})
public interface StructClassState extends InkClassState{

	public class Data extends InkClassState.Data implements StructClassState{

		@Override
		protected Class<InkObjectState> resolveDataClass(){
			if(!this.getJavaMapping().hasState()){
				return ((ClassMirrorAPI)getSuper()).getDataClass();
			}
			return myFactory.resolveStructDataClass(this);
		}
	}

}
