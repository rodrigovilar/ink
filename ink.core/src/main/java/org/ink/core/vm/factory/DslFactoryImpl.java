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
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.ink.core.utils.StringUtils;
import org.ink.core.vm.constraints.SystemState;
import org.ink.core.vm.constraints.ValidationContext;
import org.ink.core.vm.exceptions.CoreException;
import org.ink.core.vm.exceptions.WeaveException;
import org.ink.core.vm.factory.internal.CoreLoaderImpl;
import org.ink.core.vm.factory.internal.CoreLoaderState;
import org.ink.core.vm.factory.internal.CoreNotations;
import org.ink.core.vm.factory.internal.CoreObjectDescriptor;
import org.ink.core.vm.lang.InkClass;
import org.ink.core.vm.lang.InkClassImpl;
import org.ink.core.vm.lang.InkClassState;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.Struct;
import org.ink.core.vm.lang.internal.MirrorAPI;
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
public class DslFactoryImpl<S extends DslFactoryState> extends InkClassImpl<S> implements DslFactory{

	private static final String FACTORY_CONF_FILE = "factory_conf_file";
	public long numberOfStateInstance=0;
	public long numberOfBehaviorInstance=0;
	public List<Trait> detachableTraits = new ArrayList<Trait>();
	private final ProxyFactory proxyFactory = new ProxyFactory();
	protected transient ClassLoader classLoader;
	protected DslLoader loader;
	protected DslRepository repository;
	private final Map<String, Class<?>> classRepository = new ConcurrentHashMap<String, Class<?>>(200);
	protected Map<String, DslFactory> boundedFactories;
	protected Set<String> scope;
	private InstanceFactory instanceFactory;

	@Override
	public InkObject newBehaviorProxy(InkObject behaviorInstance, Class<?>[] types, Proxiability.Kind t){
		return proxyFactory.newBehaviorProxy(this, behaviorInstance, types, t);
	}
	@Override
	public InkObject newBehaviorProxy(InkObject behaviorInstance, InkObjectState state, Class<?>[] types, Proxiability.Kind t, InkObjectState owner, PropertyMirror definingProperty, byte definingPropertyIndex){
		return proxyFactory.newBehaviorProxy(this, behaviorInstance, state, types, t, owner, definingProperty, definingPropertyIndex);
	}

	@Override
	public Mirror newMirrorProxy(Mirror behaviorInstance, Class<?>[] types, InkObjectState owner, PropertyMirror definingProperty, byte definingPropertyIndex){
		return proxyFactory.newMirrorProxy(behaviorInstance, types, owner, definingProperty, definingPropertyIndex);
	}

	@Override
	public Struct newStructProxy(InkObjectState stateInstance, Class<?>[] type, InkObjectState owner, PropertyMirror definingProperty, byte definingPropertyIndex){
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
		if(importsList!=null && !importsList.isEmpty()){
			for(DslFactory f : importsList){
				if(f.isProxied()){
					f = (DslFactory) ((Proxiability)f).getVanillaBehavior();
				}
				boundedFactories.put(f.getNamespace(), f);
				this.scope.addAll(f.getScope());
			}
		}
		scope = Collections.unmodifiableSet(scope);
		for (InstanceFactory currentInstanceFactory : ServiceLoader.load(InstanceFactory.class)) {

			// Later we'll add prioritization.
			instanceFactory = currentInstanceFactory;
		}
	}

	@Override
	public void scan() {
		if(!reflect().isAbstract() && !reflect().isCoreObject()){
			loader.scan(this);
			if(VMConfig.instance().getInstantiationStrategy().enableEagerFetch()){
				Iterator<String> iter = loader.iterator();
				while(iter.hasNext()){
					try{
						getState(iter.next());
					}catch(Exception e){
						//TODO - log error
						e.printStackTrace();
					}
				}

			}
		}
	}


	@Override
	public List<Trait> getDetachableTraits(){
		return new ArrayList<Trait>(detachableTraits);
	}

	@Override
	public String getNamespace() {
		return getState().getNamespace();
	}

	private String extractNamespace(String id){
		try{
			return id.substring(0, id.indexOf(InkNotations.Path_Syntax.NAMESPACE_DELIMITER_C));
		}catch(StringIndexOutOfBoundsException e){
			throw new CoreException("Illegal Ink object id '"+id +"'. Could not extract namespace.");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends InkObjectState> T getState(String id, boolean reportErrorIfNotExists){
		InkObjectState result = repository.getObject(id);
		if(result==null){
			String ns = extractNamespace(id);
			if(getNamespace().equals(ns)){
				result = loader.getObject(id, getAppContext());
				if(result!=null){
					repository.setObject(id, result);
					ModelInfoFactory.getWriteableInstance().register(result.getBehavior());
				}else if(reportErrorIfNotExists){
					throw new CoreException("The object with id '" +id+"', could not be found.");
				}
			}else if(scope.contains(ns)){
				DslFactory factory = boundedFactories.get(ns);
				if(factory!=null){
					result = factory.getState(id, reportErrorIfNotExists);
				}else{
					for(DslFactory p : boundedFactories.values()){
						if(p.isNamespacesInScope(ns)){
							//TODO should add code to merge multiple detachable traits and to chache result if necessary
							result = p.getState(id, false);
							break;
						}
					}
				}
				if(result!=null){
					repository.setObject(id, result);
				}else if(reportErrorIfNotExists){
					throw new CoreException("The object with id '" +id+"', could not be found");
				}
			}else if(reportErrorIfNotExists){
				throw new CoreException("The object with id '" +id+"', could not be found. The namespace '"+ ns +"' is unknown in this scope ( " + getNamespace()+").");
			}
			if(result!=null && ((MirrorAPI)result).isClass()){
				applyDetachableTraits((InkClassState) result);
			}
		}
		return (T)result;
	}


	private void applyDetachableTraits(InkClassState cls) {
		InkClass clsBehav = cls.getBehavior();
		ClassMirror clsMrr = cls.reflect();
		TraitClass traitCls;
		for(Trait t : detachableTraits){
			traitCls = t.getMeta();
			if(t.isAcceptable(clsBehav) && !clsMrr.hasRole(t.reflect().getNamespace(), traitCls.getRole())){
				try {
					((ClassEditor)clsMrr.edit()).weaveDetachableTrait(t);
				} catch (WeaveException e) {
					throw new CoreException("Could not dynamically weave trait '" + t.reflect().getId() +"' to class '" +clsMrr.getId()+"'.", e);
				}
			}
		}
	}
	@SuppressWarnings("unchecked")
	@Override
	public <T extends InkObjectState> T getState(String id){
		return (T)getState(id, true);
	}

	@Override
	public <T extends InkObject> T getObject(String id){
		return getState(id, true).getBehavior();
	}

	@Override
	public <T extends InkObject> T getObject(String id, boolean reportErrorIfNotExists){
		InkObjectState result = getState(id, reportErrorIfNotExists);
		if(result==null){
			return null;
		}
		return result.getBehavior();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends InkClass> T getObject(Class<InkObjectState> stateClass) {
		return (T)getInkClass(stateClass);
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
		ModelInfoFactory.getWriteableInstance().register(state.getBehavior());
	}

	@Override
	public synchronized void registerTrait(TraitState state) {
		if(((TraitClass)state.getMeta()).getKind().isDetachable()){
			detachableTraits.add((Trait)state.getMeta());
		}
		register(state);
	}



	@Override
	public void register(InkObject o) {
		// TODO maybe to remove
	}


	private InkClass getInkClass(Class<?> stateClass) {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends InkObjectState> T newInstance(Class<T> stateClass) {
		InkClass cls = getInkClass(stateClass);
		if(cls!=null){
			return (T) cls.newInstance(getContext());
		}else{
			throw new CoreException("Could not find Ink class for Java class :" +stateClass.getName());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends InkObjectState> T newInstance(Class<T> stateClass,
			boolean initObjectId, boolean initDefaults) {
		InkClass cls = getInkClass(stateClass);
		if(cls!=null){
			return (T) cls.newInstance(this.getContext(), initObjectId, initDefaults);
		}else{
			throw new CoreException("Could not find Ink class for Java class :" +stateClass.getName());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<InkObjectState> resolveDataClass(InkClassState cls) {
		String className = getInstantiationStrategy().getDataClassName(cls, this);
		return (Class<InkObjectState>) getClass(className, true);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<InkObjectState> resolveStructDataClass(InkClassState cls) {
		String className = getInstantiationStrategy().getStructDataClassName(cls, this);
		return (Class<InkObjectState>) getClass(className, true);
	}


	@SuppressWarnings("unchecked")
	@Override
	public Class<InkObject> resolveBehaviorClass(InkClassState cls) {
		String className = getInstantiationStrategy().getBehaviorClassName(cls, this);
		return (Class<InkObject>) getClass(className, true);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<InkObject> resolveInterfaceClass(InkClassState cls) {
		String className = getInstantiationStrategy().getInterfaceClassName(cls, this);
		return (Class<InkObject>) getClass(className, true);
	}

	private InstantiationStrategy getInstantiationStrategy() {
		return VMConfig.instance().getInstantiationStrategy();
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
		return instanceFactory.newInstance(getNamespace(), stateClass.getName());
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
				}
				catch (ClassNotFoundException e) {
					if (reportErrorIfNotFound) {
						throw new CoreException("Could not load class :" +className, e);
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
	public Class<?> resolveCollectionClass(CollectionTypeMarker marker){
		if(marker==CollectionTypeMarker.List){
			return List.class;
		}else if(marker==CollectionTypeMarker.Map){
			return Map.class;
		}
		return null;
	}


	@Override
	public Class<?> resolveEnumClass(EnumTypeState enumState) {
		String className = getInstantiationStrategy().getEnumClassName(enumState, this);
		return getClass(className, true);
	}

	@Override
	public Class<?> resolvePrimitiveClass(PrimitiveTypeMarker marker) {
		Class<?> typeClass = null;
		switch(marker){
		case Boolean:
			typeClass = Boolean.class;
			break;
		case Byte:
			typeClass = Byte.class;
			break;
		case Date:
			typeClass = Date.class;
			break;
		case Double:
			typeClass = Double.class;
			break;
		case Float:
			typeClass = Float.class;
			break;
		case Integer:
			typeClass = Integer.class;
			break;
		case Long:
			typeClass = Long.class;
			break;
		case Short:
			typeClass = Short.class;
			break;
		case String:
			typeClass = String.class;
			break;
		default:
			//do nothing - maybe taken care of by descendent
		}
		return typeClass;
	}

	public void boot(){
		CoreLoaderImpl<CoreLoaderState> coreLoader = new CoreLoaderImpl<CoreLoaderState>();
		this.classLoader = Thread.currentThread().getContextClassLoader();
		this.boundedFactories = new HashMap<String, DslFactory>();
		this.scope = new HashSet<String>();
		scope.add(CoreNotations.CORE_NAMESPACE);
		scope = Collections.unmodifiableSet(scope);
		this.loader = coreLoader;
		Collection<CoreObjectDescriptor> elements = coreLoader.start(this);
		for(CoreObjectDescriptor elem : elements){
			repository.setObject(elem.getId(), elem.getObject());
		}
	}

	@Override
	public void afterVmStart() {
		ModelInfoWriteableRepository repo = ModelInfoFactory.getWriteableInstance();
		for(InkObjectState elem : repository){
			repo.register(elem.getBehavior());
		}
	}

	@Override
	public void printElements(String toFile) throws IOException{
		File f = new File(toFile);
		if(!f.exists()){
			f.getParentFile().mkdirs();
			f.createNewFile();
		}
		FileWriter writer = new FileWriter(f);
		try{
			InkObjectState o;
			Mirror m;
			Iterator<InkObjectState> iter = repository.iterator();
			while(iter.hasNext()){
				o = iter.next();
				m = o.reflect();
				if(m.getObjectTypeMarker()==ObjectTypeMarker.Metaclass){
					writer.append(o.toString());
					writer.append(StringUtils.LINE_SEPARATOR).append(StringUtils.LINE_SEPARATOR);
				}
			}
			iter = repository.iterator();
			while(iter.hasNext()){
				o = iter.next();
				m = o.reflect();
				if(m.getObjectTypeMarker()==ObjectTypeMarker.Class){
					writer.append(o.toString());
					writer.append(StringUtils.LINE_SEPARATOR).append(StringUtils.LINE_SEPARATOR);
				}
			}
			iter = repository.iterator();
			while(iter.hasNext()){
				o = iter.next();
				m = o.reflect();
				if(m.getObjectTypeMarker()==ObjectTypeMarker.Object){
					writer.append(o.toString());
					writer.append(StringUtils.LINE_SEPARATOR).append(StringUtils.LINE_SEPARATOR);
				}
			}
			iter = repository.iterator();
			while(iter.hasNext()){
				o = iter.next();
				m = o.reflect();
				if(m.getObjectTypeMarker()==ObjectTypeMarker.Enumeration){
					writer.append(o.toString());
					writer.append(StringUtils.LINE_SEPARATOR).append(StringUtils.LINE_SEPARATOR);
				}
			}
		}finally{
			if(writer!=null){
				try{
					writer.flush();
				}finally{
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
	public void validateAllElements(ValidationContext vc){
		Iterator<InkObjectState> iter = repository.iterator();
		while(iter.hasNext()){
			iter.next().getBehavior().validate(vc, SystemState.Run_Time);
		}
	}

	@Override
	public int compareTo(DslFactory o) {
		if(o.getScope().contains(getNamespace())){
			return -1;
		}
		if(getScope().contains(o.getNamespace())){
			return 1;
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
}
