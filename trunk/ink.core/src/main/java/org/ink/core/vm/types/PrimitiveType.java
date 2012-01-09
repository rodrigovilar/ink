package org.ink.core.vm.types;

import org.ink.core.vm.lang.InkType;

/**
 * @author Lior Schachter
 */
public interface PrimitiveType extends InkType {
	public boolean isDate();

	public boolean isNumeric();

	public boolean isString();

	public boolean isBoolean();

	public PrimitiveTypeMarker getPrimitiveMarker();
}
