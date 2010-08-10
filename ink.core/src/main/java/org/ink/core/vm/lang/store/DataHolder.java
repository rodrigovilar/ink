package org.ink.core.vm.lang.store;

/**
 * @author Lior Schachter
 */
public interface DataHolder {
	
	public Object getValue(byte loc);
	public void setValue(byte loc, Object value);
	
}
