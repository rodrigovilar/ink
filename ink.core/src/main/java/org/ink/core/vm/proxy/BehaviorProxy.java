package org.ink.core.vm.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.ink.core.vm.exceptions.CoreException;
import org.ink.core.vm.factory.DslFactory;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.operation.Operation;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.mirror.Mirror;

/**
 * @author Lior Schachter
 */
public class BehaviorProxy implements InvocationHandler {

	private Map<String, Operation> operationsCache = new HashMap<String, Operation>(4);
	private InkObject target = null;
	private InkObjectState state = null;
	private InkObjectState owner = null;
	private byte definingPropertyIndex = -1;
	private PropertyMirror definingProperty = null;
	private DslFactory factory;
	private Mirror mirror = null;
	private Proxiability.Kind t;

	private final static String GET_META = "getMeta";
	private static final int GET_META_HASH = GET_META.hashCode();
	private static Method getMeta = null;
	private Method getMeta_I = null;

	private static final String REFLECT = "reflect";
	private static final int REFLECT_HASH = REFLECT.hashCode();
	private static Method reflect = null;
	private Method reflect_I = null;

	private static final String AS_TRAIT = "asTrait";
	private static final int AS_TRAIT_HASH = AS_TRAIT.hashCode();
	private static Method asTrait = null;
	private Method asTrait_I = null;

	private static final String IS_PROXIED = "isProxied";
	private static final int IS_PROXIED_HASH = IS_PROXIED.hashCode();
	private static Method isProxied = null;
	private Method isProxied_I = null;

	private static final String GET_VANILLA_STATE = "getVanillaState";
	private static final int GET_VANILLA_STATE_HASH = GET_VANILLA_STATE.hashCode();
	private static Method getVanillaState = null;
	private Method getVanillaState_I = null;

	private static final String GET_VANILLA_BEHAVIOR = "getVanillaBehavior";
	private static final int GET_VANILLA_BEHAVIOR_HASH = GET_VANILLA_BEHAVIOR.hashCode();
	private static Method getVanillaBehavior = null;
	private Method getVanillaBehavior_I = null;

	private static final String GET_PROXY_KIND = "getProxyKind";
	private static final int GET_PROXY_KIND_HASH = GET_PROXY_KIND.hashCode();
	private static Method getProxyKind = null;
	private Method getProxyKind_I = null;

	static {
		try {
			getMeta = InkObject.class.getMethod(GET_META, (Class[]) null);
			reflect = InkObject.class.getMethod(REFLECT, (Class[]) null);
			asTrait = InkObject.class.getMethod(AS_TRAIT, byte.class);
			isProxied = Proxiable.class.getMethod(IS_PROXIED, (Class[]) null);
			getVanillaState = Proxiability.class.getMethod(GET_VANILLA_STATE, (Class[]) null);
			getVanillaBehavior = Proxiability.class.getMethod(GET_VANILLA_BEHAVIOR, (Class[]) null);
			getProxyKind = Proxiability.class.getMethod(GET_PROXY_KIND, (Class[]) null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected BehaviorProxy(DslFactory factory, InkObject target, InkObjectState state, Proxiability.Kind t) {
		this.factory = factory;
		this.target = target;
		this.state = state;
		this.t = t;
	}

	protected BehaviorProxy(DslFactory factory, InkObject target, InkObjectState state, Proxiability.Kind t, InkObjectState owner, PropertyMirror definingProperty, byte definingPropertyIndex) {
		this.factory = factory;
		this.target = target;
		this.state = state;
		this.t = t;
		this.owner = owner;
		this.definingProperty = definingProperty;
		this.definingPropertyIndex = definingPropertyIndex;
	}

	@Override
	public Object invoke(Object proxyObject, Method method, Object[] args) throws Throwable {
		String methodName = method.getName();
		int methodNameHash = methodName.hashCode();
		Class<?> declaringClass = method.getDeclaringClass();
		if (declaringClass.equals(Proxiability.class)) {
			if ((getVanillaState_I == null && methodNameHash == GET_VANILLA_STATE_HASH && method.equals(getVanillaState)) || method == getVanillaState_I) {
				getVanillaState_I = method;
				return state;
			} else if ((getVanillaBehavior_I == null && methodNameHash == GET_VANILLA_BEHAVIOR_HASH && method.equals(getVanillaBehavior)) || method == getVanillaBehavior_I) {
				getVanillaBehavior_I = method;
				return target;
			} else if ((getProxyKind_I == null && methodNameHash == GET_PROXY_KIND_HASH && method.equals(getProxyKind)) || method == getProxyKind_I) {
				getProxyKind_I = method;
				return t;
			} else {
				throw new CoreException("Unknown method " + methodName);
			}
		}
		if ((isProxied_I == null && methodNameHash == IS_PROXIED_HASH && method.equals(isProxied)) || method == isProxied_I) {
			isProxied_I = method;
			return true;
		} else if ((getMeta_I == null && methodNameHash == GET_META_HASH && method.equals(getMeta)) || method == getMeta_I) {
			getMeta_I = method;
			return target.getMeta();
		} else if ((reflect_I == null && methodNameHash == REFLECT_HASH && method.equals(reflect)) || method == reflect_I) {
			reflect_I = method;
			switch (t) {
			case BEHAVIOR_INTERCEPTION:
				return target.reflect();
			case BEHAVIOR_OWNER:
				return getProxiedMirror();
			case BEHAVIOR_BOTH:
				return getProxiedMirror();
			default:
				throw new CoreException("Unknown proxy type : " + t);
			}
		} else if ((asTrait_I == null && methodNameHash == AS_TRAIT_HASH && method.equals(asTrait)) || method == asTrait_I) {
			asTrait_I = method;
			return target.asTrait((Byte) args[0]);
		}
		switch (t) {
		case BEHAVIOR_INTERCEPTION:
			return runManagedMethod(method, args, methodName);
		case BEHAVIOR_OWNER:
			return runMethod(method, args);
		case BEHAVIOR_BOTH:
			return runManagedMethod(method, args, methodName);
		default:
			throw new CoreException("Unknown proxy type : " + t);
		}
	}

	private Object runManagedMethod(Method method, Object[] args, String methodName) throws Throwable {
		Operation theMethod = findBehaviorMethodProfile(methodName, args);
		if (theMethod != null) {
			return theMethod.execute(target, method, args);
		} else {
			return runMethod(method, args);
		}
	}

	private Object runMethod(Method method, Object[] args) throws Throwable {
		try {
			return method.invoke(target, args);
		} catch (InvocationTargetException e) {
			Throwable target = e.getTargetException();
			throw target != null ? target : e;
		}
	}

	private Object getProxiedMirror() {
		if (mirror == null) {
			Mirror vanillaMirror = target.reflect();
			mirror = factory.newMirrorProxy(vanillaMirror, vanillaMirror.reflect().getClassMirror().getBehaviorProxyInterfaces(), owner, definingProperty, definingPropertyIndex);
		}
		return mirror;
	}

	private Operation findBehaviorMethodProfile(String methodName, Object[] args) {
		Operation result = null;
		if ((result = operationsCache.get(methodName)) != null) {
			return result;
		}
		result = target.reflect().getClassMirror().getMethod(methodName, args);
		operationsCache.put(methodName, result);
		return result;
	}

}
