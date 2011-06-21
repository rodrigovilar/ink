package org.ink.core.vm.utils.property;

import org.ink.core.vm.lang.Property;
import org.ink.core.vm.lang.PropertyState;
import org.ink.core.vm.lang.internal.annotations.CoreField;


/**
 * @author Lior Schachter
 */
public interface KeyValueDictionaryState extends DictionaryState{

	@CoreField(defaultValue="item")
	public static final byte p_entry_name = p_ordered+1;
	@CoreField(mandatory=true)
	public static final byte p_key = p_entry_name+1;
	@CoreField(mandatory=true)
	public static final byte p_value = p_key+1;

	public String getEntryName();
	public void setEntryName(String value);

	public Property getKey();
	public void setKey(PropertyState value);

	public Property getValue();
	public void setValue(PropertyState value);

	public class Data extends DictionaryState.Data implements KeyValueDictionaryState{

		@Override
		public String getEntryName() {
			return (String)getValue(p_entry_name);
		}

		@Override
		public void setEntryName(String value) {
			setValue(p_entry_name, value);
		}

		@Override
		public Property getKey() {
			return (Property)getValue(p_key);
		}

		@Override
		public void setKey(PropertyState value) {
			setValue(p_key, value);
		}

		@Override
		public Property getValue() {
			return (Property)getValue(p_value);
		}

		@Override
		public void setValue(PropertyState value) {
			setValue(p_value, value);
		}
	}
}
