package org.ink.core.vm.traits;

import org.ink.core.vm.lang.InkClass;
import org.ink.core.vm.lang.InkClassState;
import org.ink.core.vm.lang.JavaMapping;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;
import org.ink.core.vm.lang.internal.annotations.CoreField;

/**
 * @author Lior Schachter
 */
@CoreClassSpec(javaMapping = JavaMapping.State_Behavior)
public interface ClassHierarchyLocatorState extends TargetLocatorState {

	@CoreField(mandatory = true)
	public static final byte p_root_class = 0;

	public InkClass getRootClass();

	public void setRootClass(InkClassState value);

	public class Data extends TargetLocatorState.Data implements ClassHierarchyLocatorState {

		@Override
		public InkClass getRootClass() {
			return (InkClass) getValue(p_root_class);
		}

		@Override
		public void setRootClass(InkClassState value) {
			setValue(p_root_class, value);
		}
	}
}
