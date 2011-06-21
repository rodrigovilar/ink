package org.ink.core.vm.types;

import org.ink.core.vm.lang.InkTypeState;
import org.ink.core.vm.lang.JavaMapping;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;

/**
 * @author Lior Schachter
 */
@CoreClassSpec(javaMapping=JavaMapping.State_Behavior_Interface)
public interface CollectionTypeState extends InkTypeState{

	public static final byte p_type_marker = 0;

	public CollectionTypeMarker getTypeMarker();
	public void setTypeMarker(CollectionTypeMarker value);

	public class Data extends InkTypeState.Data implements CollectionTypeState{

		@Override
		public CollectionTypeMarker getTypeMarker() {
			return (CollectionTypeMarker) getValue(p_type_marker);
		}

		@Override
		public void setTypeMarker(CollectionTypeMarker value) {
			setValue(p_type_marker, value);
		}


	}

}
