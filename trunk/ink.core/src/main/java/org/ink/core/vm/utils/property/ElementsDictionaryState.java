package org.ink.core.vm.utils.property;

import org.ink.core.vm.lang.Property;
import org.ink.core.vm.lang.PropertyState;
import org.ink.core.vm.lang.internal.annotations.CoreField;

/**
 * @author Lior Schachter
 */
public interface ElementsDictionaryState extends DictionaryState {

	@CoreField(mandatory = true)
	public static final byte p_key_property = p_ordered + 1;
	@CoreField(mandatory = true)
	public static final byte p_item = p_key_property + 1;

	public String getKeyProperty();

	public void setKeyProperty(String value);

	public Property getItem();

	public void setItem(PropertyState value);

	public class Data extends DictionaryState.Data implements ElementsDictionaryState {

		@Override
		public String getKeyProperty() {
			return (String) getValue(p_key_property);
		}

		@Override
		public void setKeyProperty(String value) {
			setValue(p_key_property, value);
		}

		@Override
		public Property getItem() {
			return (Property) getValue(p_item);
		}

		@Override
		public void setItem(PropertyState value) {
			setValue(p_item, value);
		}

	}
}
