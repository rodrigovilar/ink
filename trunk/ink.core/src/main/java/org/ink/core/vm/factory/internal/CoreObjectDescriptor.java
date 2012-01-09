package org.ink.core.vm.factory.internal;

import org.ink.core.vm.lang.internal.MirrorAPI;

/**
 * @author Lior Schachter
 */
public interface CoreObjectDescriptor extends ObjectDescriptor {

	public MirrorAPI getObject();

	public boolean isClass();

}