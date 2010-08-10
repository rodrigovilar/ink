package org.ink.core.vm.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.ink.core.vm.exceptions.CoreException;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.mirror.Mirror;

/**
 * @author Lior Schachter
 */
public class MirrorProxy implements InvocationHandler {

	private Mirror target;
	private Mirror owner;
	private PropertyMirror definingProperty;
	private byte deinfingPropertyIndex;
	
	private final static String GET_OWNER= "getOwner";
	private static final int GET_OWNER_HASH = GET_OWNER.hashCode();
	private static Method getOwner = null;
	private Method getOwner_I = null;

	private static final String GET_DEFINING_PROPERYT_INDEX = "getDefiningPropertyIndex";
	private static final int GET_DEFINING_PROPERYT_INDEX_HASH = GET_DEFINING_PROPERYT_INDEX.hashCode();
	private static Method getDefiningPropertyIndex = null;
	private Method getDefiningPropertyIndex_I = null;
	
	private static final String GET_DEFINING_PROPERYT = "getDefiningProperty";
	private static final int GET_DEFINING_PROPERYT_HASH = GET_DEFINING_PROPERYT.hashCode();
	private static Method getDefiningProperty = null;
	private Method getDefiningProperty_I = null;
	
	private static final String GET_PROPERTIES_MIRRORS = "getPropertiesMirrors";
	private static final int GET_PROPERTIES_MIRRORS_HASH = GET_PROPERTIES_MIRRORS.hashCode();
	private static Method getPropertiesMirrors = null;
	private Method getPropertiesMirrors_I = null;
	
	private static final String GET_PROPERTY_VALUE = "getPropertyValue";
	private static final int GET_PROPERTY_VALUE_HASH = GET_PROPERTY_VALUE.hashCode();
	private static Method getPropertyValue = null;
	private Method getPropertyValue_I = null;
	
	private static final String GET_PROPERTY_VALUE_2 = "getPropertyValue";
	private static final int GET_PROPERTY_VALUE_HASH_2 = GET_PROPERTY_VALUE_2.hashCode();
	private static Method getPropertyValue_2 = null;
	private Method getPropertyValue_I_2 = null;
	
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
	
	private static final String INIT = "init";
	private static final int INIT_HASH = INIT.hashCode();
	private static Method init = null;
	private Method init_I = null;
	
	static{
		try{
			getOwner = Mirror.class.getMethod(GET_OWNER, (Class[])null);
			getDefiningPropertyIndex= Mirror.class.getMethod(GET_DEFINING_PROPERYT_INDEX, (Class[]) null);
			getDefiningProperty= Mirror.class.getMethod(GET_DEFINING_PROPERYT, (Class[]) null);
			getPropertiesMirrors = Mirror.class.getMethod(GET_PROPERTIES_MIRRORS, (Class[])null);
			getPropertyValue = Mirror.class.getMethod(GET_PROPERTY_VALUE, byte.class);
			getPropertyValue_2 = Mirror.class.getMethod(GET_PROPERTY_VALUE, String.class);
			isProxied = Proxiable.class.getMethod(IS_PROXIED, (Class[])null);
			getVanillaState = Proxiability.class.getMethod(GET_VANILLA_STATE, (Class[])null);
			getVanillaBehavior = Proxiability.class.getMethod(GET_VANILLA_BEHAVIOR, (Class[])null);
			getProxyKind = Proxiability.class.getMethod(GET_PROXY_KIND, (Class[])null);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	public MirrorProxy(Mirror target,InkObjectState owner, PropertyMirror definingProperty, byte definingPropertyIndex) {
		this.target = target;
		this.owner = owner.reflect();
		this.definingProperty = definingProperty;
		this.deinfingPropertyIndex = definingPropertyIndex;
	}

	public Object invoke(Object proxyObject, Method method, Object[] args)
			throws Throwable {
		String methodName = method.getName();
		int methodNameHash = methodName.hashCode();
		if (method.getDeclaringClass().equals(Proxiability.class)) {
			if((getVanillaState_I==null && methodNameHash == GET_VANILLA_STATE_HASH && method.equals(getVanillaState)) || method==getVanillaState_I){
				getVanillaState_I = method;
				return target;
			}else if((getVanillaBehavior_I==null && methodNameHash == GET_VANILLA_BEHAVIOR_HASH && method.equals(getVanillaBehavior)) || method==getVanillaBehavior_I){
				getVanillaBehavior_I = method;
				return null;
			}else if((getProxyKind_I==null && methodNameHash == GET_PROXY_KIND_HASH && method.equals(getProxyKind)) || method==getProxyKind_I){
				getProxyKind_I = method;
				return Proxiability.Kind.MIRROR;
			}else if((init_I==null && methodNameHash == INIT_HASH && method.equals(init)) || method==init_I){
				init_I = method;
				return null;
			}else{
				throw new CoreException("Unknown method " + methodName);
			}
		}
		if((isProxied_I==null && methodNameHash == IS_PROXIED_HASH && method.equals(isProxied)) || method==isProxied_I){
			isProxied_I = method;
			return true;
		}else if((getOwner_I==null && methodNameHash == GET_OWNER_HASH && method.equals(getOwner)) || method == getOwner_I) {
			getOwner_I = method;
			return owner;
		}else if((getDefiningProperty_I==null && methodNameHash == GET_DEFINING_PROPERYT_HASH && method.equals(getDefiningProperty)) || method == getDefiningProperty_I) {
			getDefiningProperty_I = method;
			return definingProperty;
		}else if((getDefiningPropertyIndex_I==null && methodNameHash == GET_DEFINING_PROPERYT_INDEX_HASH && method.equals(getDefiningPropertyIndex)) || method == getDefiningPropertyIndex_I) {
			getDefiningPropertyIndex_I = method;
			return deinfingPropertyIndex;
		}else if((getPropertiesMirrors_I==null && methodNameHash == GET_PROPERTIES_MIRRORS_HASH && method.equals(getPropertiesMirrors)) || method == getPropertiesMirrors_I) {
			getPropertiesMirrors_I = method;
			return target.getPropertiesMirrors();
		}else if((getPropertyValue_I==null && methodNameHash == GET_PROPERTY_VALUE_HASH && method.equals(getPropertyValue)) || method == getPropertyValue_I) {
			getPropertyValue_I = method;
			return target.getPropertyValue(((Byte)args[0]));
		}else if((getPropertyValue_I_2==null && methodNameHash == GET_PROPERTY_VALUE_HASH_2 && method.equals(getPropertyValue_2)) || method == getPropertyValue_I_2) {
			getPropertyValue_I_2 = method;
			return target.getPropertyValue(((String)args[0]));
		}else{
			try {
				return method.invoke(target, args);
			} catch (InvocationTargetException e) {
				Throwable target= e.getTargetException();
				throw target!=null?target:e;
			}
		}
	}
	
}
