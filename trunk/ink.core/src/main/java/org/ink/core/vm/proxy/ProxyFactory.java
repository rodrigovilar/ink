package org.ink.core.vm.proxy;

import java.lang.reflect.Proxy;

import org.ink.core.vm.factory.DslFactory;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.Struct;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.mirror.Mirror;

/**
 * @author Lior Schachter
 */
public class ProxyFactory {
	
	private int numberOfBehviorProxyInstances = 0;
	private int numberOfMirrorProxyInstances = 0;
	private int numberOfStructProxyInstances = 0;
	private transient ClassLoader loader;
	
	public ProxyFactory(){
		loader = Thread.currentThread().getContextClassLoader();
	}
	
	public InkObject newBehaviorProxy(DslFactory factory, InkObject behaviorInstance, Class<?>[] types, Proxiability.Kind t) {
		numberOfBehviorProxyInstances++;
		return (InkObject) Proxy.newProxyInstance(loader, types , new BehaviorProxy(factory, behaviorInstance, t));
	}
	
	public Mirror newMirrorProxy(Mirror behaviorInstance, Class<?>[] types, InkObjectState owner, PropertyMirror definingProperty, byte definingPropertyIndex) {
		numberOfMirrorProxyInstances++;
		return (Mirror) Proxy.newProxyInstance(loader, types , new MirrorProxy(behaviorInstance, owner, definingProperty, definingPropertyIndex));
	}
	
	public InkObject newBehaviorProxy(DslFactory factory, InkObject behaviorInstance, InkObjectState state, Class<?>[] types, Proxiability.Kind t, InkObjectState owner, PropertyMirror definingProperty, byte definingPropertyIndex){
		numberOfBehviorProxyInstances++;
		return (InkObject) Proxy.newProxyInstance(loader, types , new BehaviorProxy(factory, behaviorInstance, state, t, owner, definingProperty, definingPropertyIndex));
	}
	
	public Struct newStructProxy(DslFactory factory, InkObjectState stateInstance, Class<?>[] types, InkObjectState owner, PropertyMirror definingProperty, byte definingPropertyIndex) {
		numberOfStructProxyInstances++;
		return (Struct) Proxy.newProxyInstance(loader, types, new StructProxy(factory, stateInstance, owner, definingProperty, definingPropertyIndex));
	}

	public int getNumberOfBehviorProxyInstances() {
		return numberOfBehviorProxyInstances;
	}

	public int getNumberOfStructProxyInstances() {
		return numberOfStructProxyInstances;
	}
	
	public int getNumberOfMirrorProxyInstances() {
		return numberOfMirrorProxyInstances;
	}
	
}
