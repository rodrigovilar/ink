package org.ink.core.vm.factory.resources;

import java.util.HashSet;
import java.util.Set;

public class JavaClassDescription {

	private static final Set<String> EMPTY = new HashSet<String>();
	private String sClass = null;
	private Set<String> interfaces = EMPTY;
	private Set<String> methods = EMPTY;


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
