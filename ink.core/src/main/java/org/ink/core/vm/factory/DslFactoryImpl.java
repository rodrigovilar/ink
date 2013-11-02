package org.ink.core.vm.factory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.ink.core.utils.StringUtils;
import org.ink.core.vm.constraints.SystemState;
import org.ink.core.vm.constraints.ValidationContext;
import org.ink.core.vm.exceptions.CoreException;
import org.ink.core.vm.exceptions.ObjectLoadingException;
import org.ink.core.vm.exceptions.WeaveException;
import org.ink.core.vm.factory.internal.CoreLoaderImpl;
import org.ink.core.vm.factory.internal.CoreLoaderState;
import org.ink.core.vm.factory.internal.CoreNotations;
import org.ink.core.vm.factory.internal.CoreObjectDescriptor;
import org.ink.core.vm.factory.resources.ResourceResolver;
import org.ink.core.vm.lang.InkClass;
import org.ink.core.vm.lang.InkClassImpl;
import org.ink.core.vm.lang.InkClassState;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.Struct;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.mirror.editor.ClassEditor;
import org.ink.core.vm.modelinfo.ModelInfoFactory;
import org.ink.core.vm.modelinfo.ModelInfoWriteableRepository;
import org.ink.core.vm.proxy.Proxiability;
import org.ink.core.vm.proxy.ProxyFactory;
import org.ink.core.vm.traits.Trait;
import org.ink.core.vm.traits.TraitClass;
import org.ink.core.vm.traits.TraitState;
import org.ink.core.vm.types.CollectionTypeMarker;
import org.ink.core.vm.types.EnumTypeState;
import org.ink.core.vm.types.ObjectTypeMarker;
import org.ink.core.vm.types.PrimitiveTypeMarker;
import org.ink.core.vm.utils.InkNotations;

/**
 * @author Lior Schachter
 */
public class DslFactoryImpl<S extends DslFactoryState> extends InkClassImpl<S> implements DslFactory {

	private static final String FACTORY_CONF_FILE = "factory_conf_file";
	public long numberOfStateInstance = 0;
	public long numberOfBehaviorInstance = 0;
	public List<Trait> detachableTraits = new ArrayList<Trait>();
	private final ProxyFactory proxyFactory = new ProxyFactory();
	protected transient ClassLoader classLoader;
	protected DslLoader loader;
	protected DslRepository repository;
	private final Map<String, Class<?>> classRepository = new ConcurrentHashMap<String, Class<?>>(200);
	protected Map<String, DslFactory> boundedFactories;
	protected Set<String> scope;
	private final List<DslFactory> dependentFactories = new ArrayList<DslFactory>();

	@Override
	public void reload(boolean propagateChange) {
		System.out.println("Reloading DSL " + getNamespace());
		repository.clear();
		loader.init();
		scan();
		ModelInfoWriteableRepository repo = ModelInfoFactory.getWriteableInstance();
		repo.reset(getNamespace());
		loadModelReopsitory();
		if(propagateChange){
			DslFactoryEventDispatcher dispatcher = asTrait(DslFactoryState.t_event_dispatcher);
			DslFactoryEvent event = newInstance(CoreNotations.Ids.DSL_FACTORY_EVENT);
			event.setKind(DslFactoryEventKind.RELOAD);
			dispatcher.publishEvent(event);
		}
	}

	@Override
	public InkObject newBehaviorProxy(InkObject behaviorInstance, InkObjectState state, Class<?>[] types, Proxiability.Kind t) {
		return proxyFactory.newBehaviorProxy(this, behaviorInstance, state, types, t);
	}

	@Override
	public InkObject newBehaviorProxy(InkObject behaviorInstance, InkObjectState state, Class<?>[] types, Proxiability.Kind t, InkObjectState owner, PropertyMirror definingProperty, byte definingPropertyIndex) {
		return proxyFactory.newBehaviorProxy(this, behaviorInstance, state, types, t, owner, definingProperty, definingPropertyIndex);
	}

	@Override
	public Mirror newMirrorProxy(Mirror behaviorInstance, Class<?>[] types, InkObjectState owner, PropertyMirror definingProperty, byte definingPropertyIndex) {
		return proxyFactory.newMirrorProxy(behaviorInstance, types, owner, definingProperty, definingPropertyIndex);
	}

	@Override
	public Struct newStructProxy(InkObjectState stateInstance, Class<?>[] type, InkObjectState owner, PropertyMirror definingProperty, byte definingPropertyIndex) {
		return proxyFactory.newStructProxy(this, stateInstance, type, owner, definingProperty, definingPropertyIndex);
	}

	@Override
	public void afterStateSet() {
		super.afterStateSet();
		this.loader = getState().getLoader();
		this.classLoader = Thread.currentThread().getContextClassLoader();
		this.repository = getState().getRepository();
		this.repository.setObject(getState().getId(), getState());
		this.boundedFactories = new HashMap<String, DslFactory>();
		this.scope = new HashSet<String>();
		scope.add(getNamespace());
		List<? extends DslFactory> importsList = getState().getImports();
		if (importsList != null && !importsList.isEmpty()) {
			for (DslFactory f : importsList) {
				if (f.isProxied()) {
					f = (DslFactory) ((Proxiability) f).getVanillaBehavior();
				}
				boundedFactories.put(f.getNamespace(), f);
				f.addDependentFactory(this);
				this.scope.addAll(f.getScope());
			}
		}
		scope = Collections.unmodifiableSet(scope);
	}

	@Override
	public void scan() {
		if (!reflect().isAbstract() && !reflect().isCoreObject()) {
			loader.scan(this);
			Iterator<InkObjectState> objectIterator = repository.iterator();
			while (objectIterator.hasNext()) {
				InkObjectState o = objectIterator.next();
				if (o.reflect().isClass()) {
					applyDetachableTraits((InkClassState) o, false);
				}
			}
			if (VMConfig.instance().getResourceResolver().enableEagerFetch()) {
				Iterator<String> iter = loader.iterator();
				while (iter.hasNext()) {
					try {
						String id = iter.next();
						getState(id, false);
					} catch (Exception e) {
						e.printStackTrace();
						throw new CoreException("Internal Error.", e);
					}
				}

			}
		}
	}

	@Override
	public List<InkErrorDetails> collectErrors() {
		return loader.collectErrors();
	}

	@Override
	public List<Trait> getDetachableTraits() {
		return new ArrayList<Trait>(detachableTraits);
	}

	@Override
	public String getNamespace() {
		return getState().getNamespace();
	}

	private String extractNamespace(String id, boolean reportError) {
		try {
			return id.substring(0, id.indexOf(InkNotations.Path_Syntax.NAMESPACE_DELIMITER_C));
		} catch (StringIndexOutOfBoundsException e) {
			if (reportError) {
				throw new CoreException("Illegal Ink object id '" + id + "'. Could not extract namespace.");
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends InkObjectState> T getState(String id, boolean reportErrorIfNotExists) {
		InkObjectState result = repository.getObject(id);
		if (result == null) {
			String ns = extractNamespace(id, reportErrorIfNotExists);
			if (ns != null) {
				if (getNamespace().equals(ns)) {
					try {
						result = loader.getObject(id, getAppContext());
						if (result != null) {
							repository.setObject(id, result);
							if (result != null && result.reflect().isClass()) {
								applyDetachableTraits((InkClassState) result, false);
							}
							ModelInfoFactory.getWriteableInstance().register(result.reflect());
						} else if (reportErrorIfNotExists) {
							throw new CoreException("The object with id '" + id + "', could not be found.");
						}
					} catch (ObjectLoadingException e) {
						if (!VMConfig.instance().getResourceResolver().enableEagerFetch()) {
							throw new RuntimeException(e);
						}
						result = e.getObject();
						if (result != null) {
							repository.setObject(result.getId(), result);
							try {
								ModelInfoFactory.getWriteableInstance().register(result.reflect());
							} catch (Throwable e1) {
								if (!VMConfig.instance().getResourceResolver().enableEagerFetch()) {
									throw new RuntimeException(e);
								}
								e.printStackTrace();
							}
						}
					}
				} else if (scope.contains(ns)) {
					DslFactory factory = boundedFactories.get(ns);
					if (factory != null) {
						result = factory.getState(id, reportErrorIfNotExists);
					} else {
						for (DslFactory p : boundedFactories.values()) {
							if (p.isNamespacesInScope(ns)) {
								result = p.getState(id, false);
								if (result != null) {
									// TODO should add code to merge multiple detachable traits and to cache result if necessary
								}
								break;
							}
						}
					}
					if (result != null) {
						if (result.reflect().isClass()) {
							InkObjectState temp = applyDetachableTraits((InkClassState) result, true);
							if (temp != null) {
								result = temp;
							}
						}
						repository.setObject(id, result);
					} else if (reportErrorIfNotExists) {
						throw new CoreException("The object with id '" + id + "', could not be found");
					}
				} else if (reportErrorIfNotExists) {
					throw new CoreException("The object with id '" + id + "', could not be found. The namespace '" + ns + "' is unknown in this scope ( " + getNamespace() + ").");
				}
			}
		}
		return (T) result;
	}

	private InkClassState applyDetachableTraits(InkClassState cls, boolean cloneBeforeChange) {
		InkClassState result = null;
		if (cls.reflect().isValid()) {
			ClassMirror clsMrr = cls.reflect();
			TraitClass traitCls;
			for (Trait t : detachableTraits) {
				traitCls = t.getMeta();
				if (t.isAcceptable(clsMrr) && !clsMrr.hasRole(traitCls.getRole())) {
					try {
						if (cloneBeforeChange && result == null) {
							result = cls.reflect().cloneTargetState(true);
							result.reflect().edit().save();
							clsMrr = result.reflect();
						}
						((ClassEditor) clsMrr.edit()).weaveDetachableTrait(t);
					} catch (WeaveException e) {
						throw new CoreException("Could not dynamically weave trait '" + t.reflect().getId() + "' to class '" + clsMrr.getId() + "'.", e);
					}
				}
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends InkObjectState> T getState(String id) {
		return (T) getState(id, true);
	}

	@Override
	public <T extends InkObject> T getObject(String id) {
		return getState(id, true).getBehavior();
	}

	@Override
	public <T extends InkObject> T getObject(String id, boolean reportErrorIfNotExists) {
		InkObjectState result = getState(id, reportErrorIfNotExists);
		if (result == null) {
			return null;
		}
		return result.getBehavior();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Struct> T getStruct(String id) {
		return (T) getState(id, true);
	}

	@Override
	public synchronized void register(InkObjectState state) {
		// TODO add validation code
		repository.setObject(state.getId(), state);
		ModelInfoFactory.getWriteableInstance().register(state.reflect());
	}

	@Override
	public synchronized void registerTrait(TraitState state) {
		if (((TraitClass) state.getMeta()).getKind().isDetachable()) {
			detachableTraits.add((Trait) state.getBehavior());
			register(state);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends InkObjectState> T newInstance(String classId) {
		InkClass cls = getObject(classId);
		if (cls != null) {
			return (T) cls.newInstance(getContext());
		} else {
			throw new CoreException("Could not find Ink class with ID :" + classId);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends InkObjectState> T newInstance(String classId, boolean initObjectId, boolean initDefaults) {
		InkClass cls = getObject(classId);
		if (cls != null) {
			return (T) cls.newInstance(this.getContext(), initObjectId, initDefaults);
		} else {
			throw new CoreException("Could not find Ink class with ID :" + classId);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<InkObjectState> resolveDataClass(InkClassState cls) {
		String className = getInstantiationStrategy().getDataClassName(cls);
		return (Class<InkObjectState>) getClass(className, true);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<InkObjectState> resolveStructDataClass(InkClassState cls) {
		String className = getInstantiationStrategy().getStructDataClassName(cls);
		return (Class<InkObjectState>) getClass(className, true);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends InkObject> resolveBehaviorClass(InkClassState cls) {
		String className = getInstantiationStrategy().getBehaviorClassName(cls);
		return (Class<InkObject>) getClass(className, true);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends InkObject> resolveInterfaceClass(InkClassState cls) {
		String className = getInstantiationStrategy().getInterfaceClassName(cls);
		return (Class<InkObject>) getClass(className, true);
	}

	private ResourceResolver getInstantiationStrategy() {
		return VMConfig.instance().getResourceResolver();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends InkObject> T newVanillaBehaviorInstance(Class<T> stateClass) {
		numberOfBehaviorInstance++;
		return (T) instantiate(stateClass);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends InkObjectState> T newVanillaStateInstance(Class<T> stateClass) {
		numberOfStateInstance++;
		return (T) instantiate(stateClass);
	}

	private Object instantiate(Class<?> stateClass) {
		return VMMain.getInstanceFactory().newInstance(getNamespace(), stateClass);
	}

	protected Class<?> getClass(String className) {
		return getClass(className, true);
	}

	protected Class<?> getClass(String className, boolean reportErrorIfNotFound) {
		Class<?> result = null;
		if (className != null) {
			if ((result = classRepository.get(className)) == null) {
				try {
					result = classLoader.loadClass(className);
					classRepository.put(className, result);
				} catch (ClassNotFoundException e) {
					if (reportErrorIfNotFound) {
						throw new CoreException("Could not load class :" + className, e);
					}
				}
			}
		}
		return result;
	}

	@Override
	public Context getAppContext() {
		return getState().getAppContext();
	}

	@Override
	public InkObject newBehaviorInstance(TraitState state, InkObjectState targetState, boolean cacheResult, boolean forceNew) {
		ClassMirror cMirror = state.reflect().getClassMirror();
		return cMirror.getFactory().newBehviorInstance(state, targetState, cacheResult, forceNew);
	}

	@Override
	public boolean isNamespacesInScope(String ns) {
		return scope.contains(ns);
	}

	@Override
	public Set<String> getScope() {
		return scope;
	}

	@Override
	public Class<?> resolveCollectionClass(CollectionTypeMarker marker) {
		if (marker == CollectionTypeMarker.LIST) {
			return List.class;
		} else if (marker == CollectionTypeMarker.MAP) {
			return Map.class;
		}
		return null;
	}

	@Override
	public Class<?> resolveEnumClass(EnumTypeState enumState) {
		String className = getInstantiationStrategy().getEnumClassName(enumState);
		return getClass(className, true);
	}

	@Override
	public Class<?> resolvePrimitiveClass(PrimitiveTypeMarker marker) {
		Class<?> typeClass = null;
		switch (marker) {
		case BOOLEAN:
			typeClass = Boolean.class;
			break;
		case BYTE:
			typeClass = Byte.class;
			break;
		case DATE:
			typeClass = Date.class;
			break;
		case DOUBLE:
			typeClass = Double.class;
			break;
		case FLOAT:
			typeClass = Float.class;
			break;
		case INTEGER:
			typeClass = Integer.class;
			break;
		case LONG:
			typeClass = Long.class;
			break;
		case SHORT:
			typeClass = Short.class;
			break;
		case STRING:
			typeClass = String.class;
			break;
		default:
			// do nothing - maybe taken care of by descendent
		}
		return typeClass;
	}

	public void boot() {
		CoreLoaderImpl<CoreLoaderState> coreLoader = new CoreLoaderImpl<CoreLoaderState>();
		this.classLoader = Thread.currentThread().getContextClassLoader();
		this.boundedFactories = new HashMap<String, DslFactory>();
		this.scope = new HashSet<String>();
		scope.add(CoreNotations.CORE_NAMESPACE);
		scope = Collections.unmodifiableSet(scope);
		this.loader = coreLoader;
		Collection<CoreObjectDescriptor> elements = coreLoader.start(this);
		for (CoreObjectDescriptor elem : elements) {
			repository.setObject(elem.getId(), elem.getObject());
		}
	}

	@Override
	public void afterVmStart() {
		loadModelReopsitory();
	}

	private void loadModelReopsitory() {
		ModelInfoWriteableRepository repo = ModelInfoFactory.getWriteableInstance();
		for (InkObjectState elem : repository) {
			try {
				if (elem.reflect().getNamespace().equals(getNamespace())) {
					repo.register(elem.reflect());
				}
			} catch (Throwable e) {
				if (!VMConfig.instance().getResourceResolver().enableEagerFetch()) {
					throw new RuntimeException(e);
				}
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean containsFile(File f) {
		List<File> allFiles = loader.getInkFiles();
		return allFiles.contains(f);
	}

	@Override
	public void destroy() {
		getState().getRepository().clear();
		getState().getLoader().init();
		classRepository.clear();
		scope = Collections.unmodifiableSet(new HashSet<String>());
		boundedFactories.clear();
	}

	@Override
	public void printElements(String toFile) throws IOException {
		File f = new File(toFile);
		if (!f.exists()) {
			f.getParentFile().mkdirs();
			f.createNewFile();
		}
		FileWriter writer = new FileWriter(f);
		try {
			InkObjectState o;
			Mirror m;
			Iterator<InkObjectState> iter = repository.iterator();
			while (iter.hasNext()) {
				o = iter.next();
				m = o.reflect();
				if (m.getObjectTypeMarker() == ObjectTypeMarker.Metaclass) {
					writer.append(o.toString());
					writer.append(StringUtils.LINE_SEPARATOR).append(StringUtils.LINE_SEPARATOR);
				}
			}
			iter = repository.iterator();
			while (iter.hasNext()) {
				o = iter.next();
				m = o.reflect();
				if (m.getObjectTypeMarker() == ObjectTypeMarker.Class) {
					writer.append(o.toString());
					writer.append(StringUtils.LINE_SEPARATOR).append(StringUtils.LINE_SEPARATOR);
				}
			}
			iter = repository.iterator();
			while (iter.hasNext()) {
				o = iter.next();
				m = o.reflect();
				if (m.getObjectTypeMarker() == ObjectTypeMarker.Object) {
					writer.append(o.toString());
					writer.append(StringUtils.LINE_SEPARATOR).append(StringUtils.LINE_SEPARATOR);
				}
			}
			iter = repository.iterator();
			while (iter.hasNext()) {
				o = iter.next();
				m = o.reflect();
				if (m.getObjectTypeMarker() == ObjectTypeMarker.Enumeration) {
					writer.append(o.toString());
					writer.append(StringUtils.LINE_SEPARATOR).append(StringUtils.LINE_SEPARATOR);
				}
			}
		} finally {
			if (writer != null) {
				try {
					writer.flush();
				} finally {
					writer.close();
				}
			}
		}
	}

	@Override
	public String getJavaPackage() {
		return getState().getJavaPackage();
	}

	@Override
	public String getDslPackage() {
		return getState().getDslPackage();
	}

	@Override
	public void validateAllElements(ValidationContext vc) {
		Iterator<InkObjectState> iter = repository.iterator();
		while (iter.hasNext()) {
			iter.next().getBehavior().validate(vc, SystemState.RUN_TIME);
		}
	}

	@Override
	public int compareTo(DslFactory o) {
		if (o.getScope().contains(getNamespace())) {
			return 1;
		}
		if (getScope().contains(o.getNamespace())) {
			return -1;
		}
		return 0;
	}

	@Override
	public File getConfigurationFile() {
		return (File) reflect().get(FACTORY_CONF_FILE);
	}

	@Override
	public void setConfigurationFile(File f) {
		reflect().put(FACTORY_CONF_FILE, f);
	}

	@Override
	public ElementDescriptor<?> getDescriptor(String id) {
		String ns = extractNamespace(id, false);
		if (ns != null) {
			if (getNamespace().equals(ns)) {
				return loader.getDescriptor(id);
			} else if (scope.contains(ns)) {
				DslFactory factory = boundedFactories.get(ns);
				return factory.getDescriptor(id);
			}
		}
		return null;
	}

	@Override
	public List<String> getElements(String filepath) {
		return loader.getElements(filepath);
	}

	@Override
	public void handleEvent(DslFactoryEvent event) {
		switch (event.getKind()) {
		case RELOAD:
			reload(true);
			break;
		}
	}

	@Override
	public List<File> getSourceFiles() {
		return loader.getInkFiles();
	}

	@Override
	public List<DslFactory> getDependentFactories() {
		return new ArrayList<DslFactory>(dependentFactories);
	}

	@Override
	public void addDependentFactory(DslFactory factory) {
		this.dependentFactories .add(factory);
		DslFactoryEventDispatcher dispatcher = asTrait(DslFactoryState.t_event_dispatcher);
		dispatcher.addListener(factory);
	}
	
	@Override
	public List<String> getElementsIds(){
		return loader.getElementsIds();
	}

	@Override
	public void clearCaches() {
		repository.clear();
		loader.init();
	}
	
	

}
