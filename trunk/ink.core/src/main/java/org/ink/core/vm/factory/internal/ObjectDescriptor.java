package org.ink.core.vm.factory.internal;

/**
 * @author Lior Schachter
 */
public interface ObjectDescriptor {

	public String getId();

	public String getClassId();

	public Class<?> getStateClass();

}