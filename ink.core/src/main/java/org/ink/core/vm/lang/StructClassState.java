package org.ink.core.vm.lang;

import java.util.List;

import org.ink.core.vm.lang.internal.ClassMirrorAPI;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;
import org.ink.core.vm.mirror.StructClassMirrorState;
import org.ink.core.vm.utils.property.PrimitiveAttribute;

/**
 * @author Lior Schachter
 */
@CoreClassSpec(mirrorClass=StructClassMirrorState.class, javaMapping=JavaMapping.Only_State,
		finalValues={"Only_State"}, finalValuesLocation={InkClassState.p_java_mapping})
public interface StructClassState extends InkClassState{

	@Override
	public List<? extends PrimitiveAttribute> getProperties();

	public class Data extends InkClassState.Data implements StructClassState{

		@SuppressWarnings("unchecked")
		@Override
		public List<? extends PrimitiveAttribute> getProperties() {
			return (List<? extends PrimitiveAttribute>) super.getProperties();
		}

		@Override
		protected Class<InkObjectState> resolveDataClass(){
			if(!this.getJavaMapping().hasState()){
				return ((ClassMirrorAPI)getSuper()).getDataClass();
			}
			return myFactory.resolveStructDataClass(this);
		}
	}

}
