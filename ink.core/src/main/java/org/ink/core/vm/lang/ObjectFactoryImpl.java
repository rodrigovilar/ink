package org.ink.core.vm.lang;

import java.util.ArrayList;
import java.util.List;

import org.ink.core.vm.exceptions.CoreException;
import org.ink.core.vm.factory.DslFactory;
import org.ink.core.vm.lang.internal.MirrorAPI;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.proxy.Proxiability;
import org.ink.core.vm.traits.TraitImpl;
import org.ink.core.vm.traits.TraitState;
import org.ink.core.vm.utils.CoreUtils;

/**
 * @author Lior Schachter
 */
public class ObjectFactoryImpl<S extends ObjectFactoryState> extends InkObjectImpl<S> implements ObjectFactory {

	private DslFactory factory;
	private ClassMirror cMirror;
	private Object[] finalState;
	private Object[] defaultState;
	private Byte[] finalValuesIndexes;
	private Byte[] defaultValuesIndexes;

	public ObjectFactoryImpl() {
	}

	@Override
	public boolean isPropertyFinal(byte index) {
		if (finalValuesIndexes != null) {
			for (byte loc : finalValuesIndexes) {
				if (loc == index) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void afterStateSet() {
		super.afterStateSet();
		factory = getContext().getFactory();
	}

	@Override
	public void bind(ClassMirror cMirror) {
		this.cMirror = cMirror;
		caluculateStates();
	}

	private void caluculateStates() {
		List<Byte> finalLocations = new ArrayList<Byte>();
		List<Byte> defaultLocations = new ArrayList<Byte>();
		List<Object> finalData = new ArrayList<Object>();
		List<Object> defaultData = new ArrayList<Object>();
		PropertyMirror[] mirrors = cMirror.getClassPropertiesMirrors();
		Object value;
		Property prop;
		for (byte i = 0; i < mirrors.length; i++) {
			prop = mirrors[i].getTargetBehavior();
			if ((value = prop.getFinalValue()) != null) {
				finalLocations.add(i);
				finalData.add(value);
			} else if ((value = prop.getDefaultValue()) != null) {
				defaultLocations.add(i);
				defaultData.add(value);
			}
		}
		if (!finalLocations.isEmpty()) {
			finalValuesIndexes = finalLocations.toArray(new Byte[] {});
			finalState = finalData.toArray(new Object[] {});
		}
		if (!defaultLocations.isEmpty()) {
			defaultValuesIndexes = defaultLocations.toArray(new Byte[] {});
			defaultState = defaultData.toArray(new Object[] {});
		}
	}

	@Override
	public String getNamespace() {
		return ((DslFactory) getMeta()).getNamespace();
	}

	@SuppressWarnings("unchecked")
	@Override
	public final <T extends InkObjectState> T newInstance(DslFactory factory, InkClassState cls, boolean initObjectId, boolean initDefaults) {
		MirrorAPI state = (MirrorAPI) factory.newVanillaStateInstance(((ClassMirror) cls.reflect()).getStateClass());
		state.init(cls, factory);
		initInstance(cls, state, initObjectId, initDefaults);
		return (T) state;
	}

	private void initInstance(InkClassState cls, MirrorAPI state, boolean initObjectId, boolean initDefaults) {
		Object value;
		byte loc;
		if (finalState != null) {
			PropertyMirror[] propMirrors = cMirror.getClassPropertiesMirrors();
			for (byte i = 0; i < finalValuesIndexes.length; i++) {
				loc = finalValuesIndexes[i];
				value = finalState[i];
				state.insertValue(loc, CoreUtils.cloneOneValue(propMirrors[loc], value, false));
			}
		}
		if (initDefaults) {
			if (defaultState != null) {
				PropertyMirror[] propMirrors = cMirror.getClassPropertiesMirrors();
				for (byte i = 0; i < defaultValuesIndexes.length; i++) {
					loc = defaultValuesIndexes[i];
					value = defaultState[i];
					state.setPropertyValue(loc, CoreUtils.cloneOneValue(propMirrors[loc], value, false));
				}
			}
			if (cls.getComponentType() == ComponentType.Root) {
				state.setRoot(true);
			}
		}
		((InkClass) cls.getBehavior()).initInstance(state, initObjectId, initDefaults);
		if (initObjectId && state.getId() == null) {
			state.setId(generateId());
		}
	}

	protected String generateId() {
		return CoreUtils.newUUID();
	}

	@Override
	public InkObject newBehviorInstance(InkObjectState state, boolean cacheResult, boolean forceNew) {
		return createBehaviorInstance(state, null, cacheResult, forceNew);
	}

	@Override
	public InkObject newBehviorInstance(TraitState state, InkObjectState targetState, boolean cacheResult, boolean forceNew) {
		return createBehaviorInstance(state, targetState, cacheResult, forceNew);
	}

	private InkObject createBehaviorInstance(InkObjectState state, InkObjectState targetState, boolean cacheResult, boolean forceNew) {
		InkObject result = null;
		if (forceNew || (result = cMirror.getCachedBehavior(state)) == null) {
			boolean interceptable = cMirror.shouldCreateProxyOnBehaviorInstance();
			if (!cacheResult) {
				result = instantiateBehaviorInstance(state, targetState, interceptable, false);
				return result;
			} else if (forceNew) {
				synchronized (state) {
					result = instantiateBehaviorInstance(state, targetState, interceptable, cacheResult);
				}
				return result;
			} else {
				synchronized (state) {
					if ((result = cMirror.getCachedBehavior(state)) == null) {
						result = instantiateBehaviorInstance(state, targetState, interceptable, cacheResult);
					}
				}
				return result;
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private <T extends InkObject> T instantiateBehaviorInstance(InkObjectState state, InkObjectState targetState, boolean interceptable, boolean cacheResults) {
		Class<? extends InkObject> bClass = cMirror.getBehaviorClass();
		try {
			T result = (T) factory.newVanillaBehaviorInstance(bClass);
			InkObject vanillaBehavior = result;
			if (interceptable) {
				result = (T) newBehaviorProxy(result, state, cMirror.getBehaviorProxyInterfaces(), Proxiability.Kind.BEHAVIOR_INTERCEPTION);
			}
			if (cacheResults) {
				cMirror.cacheBeahvior(state, result);
			}
			((InkObjectImpl<?>) vanillaBehavior).setState(state, state.getContext());
			vanillaBehavior.afterStateSet();
			if (targetState != null) {
				((TraitImpl<?>) vanillaBehavior).setTargetState(targetState);
				((TraitImpl<?>) vanillaBehavior).afterTargetSet();
			}
			return result;
		} catch(CoreException e){
			throw e;
		}catch (Exception e) {
			throw new CoreException("Could not instantiate behavior class : " + bClass.getName(), e);
		}
	}

	@Override
	public Struct newStructProxy(InkObjectState stateInstance, Class<?>[] type, InkObjectState owner, PropertyMirror definingProperty, byte definingPropertyIndex) {
		return factory.newStructProxy(stateInstance, type, owner, definingProperty, definingPropertyIndex);
	}

	@Override
	public InkObject newBehaviorProxy(InkObject behaviorInstance, InkObjectState state, Class<?>[] types, Proxiability.Kind t) {
		return factory.newBehaviorProxy(behaviorInstance, state, types, t);
	}

	@Override
	public InkObject newBehaviorProxy(InkObject behaviorInstance, InkObjectState state, Class<?>[] types, Proxiability.Kind t, InkObjectState owner, PropertyMirror definingProperty, byte definingPropertyIndex) {
		return factory.newBehaviorProxy(behaviorInstance, state, types, t, owner, definingProperty, definingPropertyIndex);
	}

}
