package org.ink.core.vm.lang.store;

/**
 * @author Lior Schachter
 */
public class DefaultDataHolder implements DataHolder {

	private Object[] data = null;

	public DefaultDataHolder(byte size) {
		data = new Object[size];
	}

	@Override
	public Object getValue(byte loc) {
		if (loc >= data.length) {
			throw new ArrayIndexOutOfBoundsException(loc);
		}
		return data[loc];
	}

	@Override
	public void setValue(byte index, Object value) {
		data[index] = value;
	}

}
