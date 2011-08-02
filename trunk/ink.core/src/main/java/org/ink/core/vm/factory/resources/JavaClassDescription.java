package org.ink.core.vm.factory.resources;

import java.util.Set;

public class JavaClassDescription {

	private String sClass;
	private Set<String> interfaces;
	private Set<String> methods;


	public String getsClass() {
		return sClass;
	}
	public void setSuperClass(String sClass) {
		this.sClass = sClass;
	}
	public void setInterfaces(Set<String> interfaces) {
		this.interfaces = interfaces;
	}
	public void setMethods(Set<String> methods) {
		this.methods = methods;
	}
	public String getSuperClass() {
		return sClass;
	}
	public Set<String> getInterfaces() {
		return interfaces;
	}
	public Set<String> getMethods() {
		return methods;
	}




}
