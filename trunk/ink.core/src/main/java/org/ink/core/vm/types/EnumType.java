package org.ink.core.vm.types;

import org.ink.core.vm.lang.InkType;

/**
 * @author Lior Schachter
 */
public interface EnumType extends InkType{
	
	public Object getEnumObject(String value);
	
}
