package org.ink.core.vm.types;

import java.util.Collection;

import org.ink.core.vm.lang.InkType;

/**
 * @author Lior Schachter
 */
public interface EnumType extends InkType{
	
	public Object getEnumObject(String value);
	public Collection<String> getValues();
	
}
