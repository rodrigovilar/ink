package org.ink.core.vm.lang;

/**
 * @author Lior Schachter
 */
public interface InkType extends InkObject{

	public Class<?> getTypeClass();
	public DataTypeMarker getTypeMarker();
	public boolean isPrimitive();
	public boolean isObject();
	public boolean isCollection();
	public boolean isEnumeration();
	
}
