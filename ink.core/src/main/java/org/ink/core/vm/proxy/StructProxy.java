package org.ink.core.vm.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.ink.core.vm.exceptions.CoreException;
import org.ink.core.vm.factory.DslFactory;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.mirror.Mirror;

/**
 * @author Lior Schachter
 */
public class StructProxy implements InvocationHandler {

	private InkObjectState state = null;
	private InkObjectState owner = null;
	private byte definingPropertyIndex = -1;
	private PropertyMirror definingProperty = null;
	private Mirror mirror = null;
	private DslFactory factory;

	private final static String GET_META= "getMeta";
	private static final int GET_META_HASH = GET_META.hashCode();
	private static Method getMeta = null;
	private Method getMeta_I = null;

	private static final String REFLECT = "reflect";
	private static final int REFLECT_HASH = REFLECT.hashCode();
	private static Method reflect = null;
	private Method reflect_I = null;
	
	private static final String ADAPT = "adapt";
	private static final int ADAPT_HASH = ADAPT.hashCode();
	private static Method adapt = null;
	private Method adapt_I = null;
	
	private static final String GET_BEHAVIOR = "getBehavior";
	private static final int GET_BEHAVIOR_HASH = GET_BEHAVIOR.hashCode();
	private static Method getBehavior = null;
	private Method getBehavior_I = null;
	
	private static final String GET_OWNER = "getOwner";
	private static final int GET_OWNER_HASH = GET_OWNER.hashCode();
	private static Method getOwner = null;
	private Method getOwner_I = null;
	
	private static final String GET_HOLDING_PROPERTY_INDEX = "getHoldingPropertyIndex";
	private static final int GET_HOLDING_PROPERTY_INDEX_HASH = GET_HOLDING_PROPERTY_INDEX.hashCode();
	private static Method getHoldingPropertyIndex = null;
	private Method getHoldingPropertyIndex_I = null;
	
	private static final String IS_PROXIED = "isProxied";
	private static final int IS_PROXIED_HASH = IS_PROXIED.hashCode();
	private static Method isProxied = null;
	private Method isProxied_I = null;
	
	private static final String GET_VANILLA_STATE = "getVanillaState";
	private static final int GET_VANILLA_STATE_HASH = GET_VANILLA_STATE.hashCode();
	private static Method getVanillaState = null;
	private Method getVanillaState_I = null;
	
	private static final String GET_VANILLA_BEHAVIOR = "getVanillaBehavior";
	private static final int GET_VANILLA_BEHAVIOR_HASH = GET_VANILLA_STATE.hashCode();
	private static Method getVanillaBehavior = null;
	private Method getVanillaBehavior_I = null;
	
	private static final String GET_PROXY_KIND = "getProxyKind";
	private static final int GET_PROXY_KIND_HASH = GET_PROXY_KIND.hashCode();
	private static Method getProxyKind = null;
	private Method getProxyKind_I = null;
	
	static{
		try{
			getMeta = InkObjectState.class.getMethod(GET_META, (Class[])null);
			reflect= InkObjectState.class.getMethod(REFLECT, (Class[]) null);
			adapt = InkObjectState.class.getMethod(ADAPT, byte.class);
			getBehavior = InkObjectState.class.getMethod(GET_BEHAVIOR, (Class[])null);
			isProxied = Proxiable.class.getMethod(IS_PROXIED, (Class[])null);
			getVanillaState = Proxiability.class.getMethod(GET_VANILLA_STATE, (Class[])null);
			getVanillaBehavior = Proxiability.class.getMethod(GET_VANILLA_BEHAVIOR, (Class[])null);
			getProxyKind = Proxiability.class.getMethod(GET_PROXY_KIND, (Class[])null);
		}catch(Exception e){
			throw new CoreException("Could not find method.", e);
		}
	}
	
	protected StructProxy(DslFactory factory, InkObjectState state, InkObjectState owner, PropertyMirror definingProperty, byte definingPropertyIndex) {
		this.state = state;
		this.factory = factory;
		this.owner = owner;
		this.definingProperty = definingProperty;
		this.definingPropertyIndex = definingPropertyIndex;
	}

	public Object invoke(Object proxyObject, Method method, Object[] args)
			throws Throwable {
		String methodName = method.getName();
		int methodNameHash = methodName.hashCode();
		if (method.getDeclaringClass().equals(Proxiability.class)) {
			if((getVanillaState_I==null && methodNameHash == GET_VANILLA_STATE_HASH && method.equals(getVanillaState)) || method==getVanillaState_I){
				getVanillaState_I = method;
				return state;
			}else if((getVanillaBehavior_I==null && methodNameHash == GET_VANILLA_BEHAVIOR_HASH && method.equals(getVanillaBehavior)) || method==getVanillaBehavior_I){
				getVanillaBehavior_I = method;
				return null;
			}else if((getProxyKind_I==null && methodNameHash == GET_PROXY_KIND_HASH && method.equals(getProxyKind)) || method==getProxyKind_I){
				getProxyKind_I = method;
				return Proxiability.Kind.STRUCTURE;
			}else{
				throw new CoreException("Unknown method " + methodName);
			}
		}
		if((isProxied_I==null && methodNameHash == IS_PROXIED_HASH && method.equals(isProxied)) || method==isProxied_I){
			isProxied_I = method;
			return true;
		}else if((getMeta_I==null && methodNameHash == GET_META_HASH && method.equals(getMeta)) || method == getMeta_I) {
			getMeta_I = method;
			return state.getMeta();
		}else if((getBehavior_I==null && methodNameHash == GET_BEHAVIOR_HASH && method.equals(getBehavior)) || method == getBehavior_I) {
			getBehavior_I = method;
			return state.getBehavior();
		}else if((reflect_I==null && methodNameHash == REFLECT_HASH && method.equals(reflect)) || method == reflect_I) {
			reflect_I = method;
			return getProxiedMirror();
		}else if((adapt_I==null && methodNameHash == ADAPT_HASH && method.equals(adapt)) || method == adapt_I) {
			adapt_I = method;
			return state.asTrait((Byte)args[0]);
		}else if((getOwner_I==null && methodNameHash == GET_OWNER_HASH && method.equals(getOwner)) || method == getOwner_I) {
			getOwner_I = method;
			return owner;
		}else if((getHoldingPropertyIndex_I==null && methodNameHash == GET_HOLDING_PROPERTY_INDEX_HASH && method.equals(getHoldingPropertyIndex)) || method == getHoldingPropertyIndex_I) {
			getHoldingPropertyIndex_I = method;
			return definingPropertyIndex;
		}else{
			try {
				return method.invoke(state, args);
			} catch (InvocationTargetException e) {
				Throwable target= e.getTargetException();
				throw target!=null?target:e;
			}
		}
	}
	
	private Object getProxiedMirror() {
		if(mirror==null){
			Mirror vanillaMirror = state.reflect();
			mirror = (Mirror) factory.newMirrorProxy(vanillaMirror, vanillaMirror.reflect().getClassMirror().getBehaviorProxyInterfaces(), owner, definingProperty, definingPropertyIndex);
		}
		return mirror;
	}
	
}
