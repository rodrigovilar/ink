package org.ink.codegen.velocity.class_gen;

public class PropertyDescriptor {
	
	String name;
	String javaType;
	boolean Isinherited;
	boolean isSimpleType;
	boolean isClassType;
	boolean isList;
	boolean isMap;
	public String getName() {
		return name;
	}
	public String getJavaType() {
		return javaType;
	}

}
