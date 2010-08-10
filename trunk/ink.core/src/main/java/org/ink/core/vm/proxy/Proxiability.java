package org.ink.core.vm.proxy;

import org.ink.core.vm.lang.internal.MirrorAPI;

/**
 * @author Lior Schachter
 */
public interface Proxiability {
	
	public enum Kind{
		MIRROR,STRUCTURE,BEHAVIOR_INTERCEPTION, BEHAVIOR_OWNER,BEHAVIOR_BOTH;
	}
	
	public MirrorAPI getVanillaState();
	public MirrorAPI getVanillaBehavior();
	public Kind getProxyKind();
	
}
