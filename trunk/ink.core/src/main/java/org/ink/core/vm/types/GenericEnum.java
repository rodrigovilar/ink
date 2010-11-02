package org.ink.core.vm.types;

public class GenericEnum{
	
	private String value;
	
	private GenericEnum(String value) {
		this.value = value;
	}
	
	public static GenericEnum valueOf(String value){
		return new GenericEnum(value);
	}
	
	@Override
	public String toString() {
		return value;
	}
	
	@Override
	public int hashCode() {
		return value.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof GenericEnum){
			//TODO - fix this
			return toString().equals(obj.toString());
		}
		return false;
	}

}
