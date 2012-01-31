package org.ink.codegen.velocity.class_gen;

import java.util.List;

public class ClassDescriptor {
	String fullId;
	String id;
	String interfaceId;
	String interfaceJavaClass;
	String javaPath;
	String superID;
	String description;
	String superJavaClass;
	List<PropertyDescriptor> properties;
	
	
	public String getJavaPath() {
		return javaPath;
	}
	
	public String getId() {
		return id;
	}

	public List<PropertyDescriptor> getProperties() {
		return properties;
	}
	
	public String getDescription(){
		return description;
	}
	
	
	
	
}
