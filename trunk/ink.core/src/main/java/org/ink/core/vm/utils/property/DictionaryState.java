package org.ink.core.vm.utils.property;

import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.internal.annotations.CoreField;

/**
 * @author Lior Schachter
 */
public interface DictionaryState extends InkObjectState{

	@CoreField(defaultValue="false")
	public static final byte p_ordered = 0;

	public Boolean getOrdered();
	public void setOrdered(Boolean value);

	public class Data extends InkObjectState.Data implements DictionaryState{

		@Override
		public Boolean getOrdered() {
			return (Boolean)getValue(p_ordered);
		}

		@Override
		public void setOrdered(Boolean value) {
			setValue(p_ordered, value);
		}

	}

}
