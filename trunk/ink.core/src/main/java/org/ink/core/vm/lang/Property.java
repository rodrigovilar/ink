package org.ink.core.vm.lang;


/**
 * @author Lior Schachter
 */
public interface Property extends TypedObject {
	
	public static final byte UNBOUNDED_PROPERTY_INDEX=-1;
	
	
	public boolean isMandatory();
	public String getName();
	public String getDisplayName();
	public InkType getType();
	public boolean isPlural();
	public Object getDefaultValue();
	public Object getFinalValue();
}
