package org.ink.core.vm.factory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import org.ikayzo.sdl.SDLParseException;
import org.ikayzo.sdl.Tag;
import org.ink.core.utils.sdl.SdlParser;
import org.ink.core.vm.exceptions.CoreException;
import org.ink.core.vm.exceptions.InkBootException;
import org.ink.core.vm.exceptions.InkException;
import org.ink.core.vm.factory.internal.CoreNotations;
import org.ink.core.vm.lang.InkClass;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.JavaMapping;
import org.ink.core.vm.mirror.editor.ObjectEditor;
import org.ink.core.vm.modelinfo.ModelInfoFactory;
import org.ink.core.vm.modelinfo.ModelInfoWriteableRepository;
import org.ink.core.vm.serialization.InkReader;
import org.ink.core.vm.serialization.ParseError;
import org.ink.core.vm.utils.InkNotations;
import org.ink.core.vm.utils.file.FileUtils;

/**
 * @author Lior Schachter
 */
public class VMMain {

	private static DslFactory factory = null;
	private static Set<String> namespaces = new HashSet<String>();
	private static DslFactory coreFactory = null;
	private static String[] paths = null;
	private static String defaultNS = null;
	private static Map<String, DslFactory> allFactories = new HashMap<String, DslFactory>();
	private static boolean startupInProgress = false;
	private static InstanceFactory instanceFactory = null;

	public static void restart() {
		stop();
		start(defaultNS, paths);
	}

	public static void stop() {
		List<DslFactory> factories = new ArrayList<DslFactory>(allFactories.values());
		Collections.sort(factories);
		for (DslFactory f : factories) {
			f.destroy();
		}
		allFactories.clear();
		namespaces.clear();
		factory = null;
		coreFactory = null;
		defaultNS = null;
		startupInProgress = false;
		instanceFactory = null;
	}

	protected static DslFactory getFactory(String namespace) {
		return allFactories.get(namespace);
	}

	public static DslFactory getDefaultFactory() {
		if (factory == null) {
			throw new InkBootException("Error in intialization. The start method should be called first.");
		}
		return factory;
	}

	public static void start(String defaultNamespace, String[] paths) {
		defaultNS = defaultNamespace;
		VMMain.paths = paths;
		if (factory == null) {
			synchronized (VMMain.class) {
				if (factory == null && !startupInProgress) {
					for (InstanceFactory currentInstanceFactory : ServiceLoader.load(InstanceFactory.class)) {
						// Later we'll add prioritization.
						instanceFactory = currentInstanceFactory;
					}
					startupInProgress = true;
					if (paths == null) {
						String pathArgument = System.getProperty("ink.classpath");
						if (pathArgument != null) {
							List<String> pathsList = new ArrayList<String>();
							StringBuilder builder = new StringBuilder(40);
							for (char c : pathArgument.toCharArray()) {
								if (c == ',') {
									pathsList.add(builder.toString());
									builder = new StringBuilder(40);
								} else if (builder.length() == 0 && c == ' ') {
									continue;
								} else {
									builder.append(c);
								}
							}
							if (builder.length() > 0) {
								pathsList.add(builder.toString());
							}
							if (pathsList.size() > 0) {
								loadApplication(defaultNamespace, pathsList.toArray(new String[] {}));
							}
						} else {
							try {
								Enumeration<URL> urls = VMMain.class.getClassLoader().getResources("dsls.ink");
								List<String> dslsDefPaths = new ArrayList<String>(); 
								while(urls.hasMoreElements()){
									URL url = urls.nextElement();
									dslsDefPaths.add(0, new File(url.toURI()).getAbsolutePath());
								}
								loadApplication(defaultNamespace, dslsDefPaths.toArray(new String[]{}));
							} catch (Exception e) {
								throw new CoreException(e);
							}
						}
					} else {
						loadApplication(defaultNamespace, paths);
					}
					for (DslFactory currentFactory : allFactories.values()) {
						currentFactory.afterVmStart();
					}
					startupInProgress = false;
				}
			}
		}
	}

	private static void loadApplication(String defaultNamespace, String[] sourcePaths) {
		loadFactories(defaultNamespace, sourcePaths);
	}

	public static DslFactory getCoreFactory() {
		return coreFactory;
	}

	public static Set<String> getDsls() {
		return namespaces;
	}

	private static void loadFactories(String defaultNamespace, String[] sourcePaths) {
		if (sourcePaths == null) {
			coreFactory = factory = loadCoreFactory();
			allFactories.put(factory.getNamespace(), factory);
		} else {
			coreFactory = loadCoreFactory();
			allFactories.put(coreFactory.getNamespace(), coreFactory);
			namespaces.add(coreFactory.getNamespace());
			List<DslFactory> factories = new ArrayList<DslFactory>();
			for (String p : sourcePaths) {
				factories.addAll(collectFactories(p, coreFactory));
			}
			for (DslFactory f : factories) {
				allFactories.put(f.getNamespace(), f);
				namespaces.add(f.getNamespace());
			}
			ModelInfoFactory.getInstance().reload();
			for (DslFactory f : factories) {
				f.scan();
			}
			arrangeFactories(defaultNamespace, factories);
		}
	}

	private static void arrangeFactories(String defaultNamespace, List<DslFactory> factories) {
		if (factories.isEmpty()) {
			factory = coreFactory;
		} else {
			Collections.sort(factories);
			if (factories.size() == 1) {
				factory = factories.get(0);
			} else if (factories.size() > 1) {
				if (factories.get(0).compareTo(factories.get(1)) == 0) {
					if (defaultNamespace != null) {
						factory = allFactories.get(defaultNamespace);
						if (factory == null) {
							System.out.println("Could not find DSL factory '" + defaultNamespace + "', can't load");
							throw new CoreException("Could not find DSL factory '" + defaultNamespace + "', can't load.");
						}
					} else {
						String defaultFactoryNS = System.getProperty("default.factory");
						if (defaultFactoryNS == null) {
							System.out.println("More than one possible default factory was found, creating a facade factory...");
							factory = createFacadeFactory(coreFactory, factories);
						} else {
							for (DslFactory f : factories) {
								if (f.getNamespace().equals(defaultFactoryNS)) {
									factory = f;
									break;
								}
							}
							if (factory == null) {
								System.out.println("Could not find DSL factory '" + defaultFactoryNS + "', can't load");
								throw new CoreException("Could not find DSL factory '" + defaultFactoryNS + "', can't load.");
							}
						}
					}
				}else{
					factory = factories.get(0);
				}
			} else {
				System.out.println("No DSL factory found on classpath. Using core factory as default factory.");
			}
		}
	}

	private static DslFactory createFacadeFactory(DslFactory coreFactory, List<DslFactory> factories) {
		List<DslFactoryState> imports = new ArrayList<DslFactoryState>();
		DslFactoryState facadeFactory = coreFactory.cloneState();
		ObjectEditor editor = facadeFactory.reflect().edit();
		InkObjectState superFactory = coreFactory.getState(CoreNotations.Ids.OBJECT_FACTORY);
		editor.setSuper(superFactory);
		DslLoaderState loader = ((InkClass) coreFactory.getObject(CoreNotations.Ids.EMPTY_DSL_LOADER)).newInstance();
		facadeFactory.setLoader(loader);
		facadeFactory.setJavaPath("");
		facadeFactory.setJavaMapping(JavaMapping.No_Java);
		facadeFactory.setDslPackage("");
		facadeFactory.setJavaPackage("");
		facadeFactory.setNamespace("ink.facade");
		for (int i = factories.size()-1; i >=0; i--) {
			boolean shouldAdd = true; 
			DslFactory imp = factories.get(i);
			for(int j=0;j<imports.size();j++){
				DslFactory existingImp = imports.get(j).getBehavior(); 
				if(existingImp.compareTo(imp)<0){
					shouldAdd = false;
				}
			}
			if(shouldAdd){
				imports.add((DslFactoryState) imp.reflect().edit().getEditedState());
			}
		}
		facadeFactory.setImports(imports);
		facadeFactory.reflect().edit().save();
		return facadeFactory.getBehavior();
	}

	private static List<DslFactory> collectFactories(String path, DslFactory coreFactory) {
		List<DslFactory> result = new ArrayList<DslFactory>();
		File inkFile = new File(path);
		if (!inkFile.exists()) {
			// TODO log warning
			return result;
		}
		if (inkFile.isFile()) {
			result.addAll(locateFactory(inkFile, coreFactory));
		} else {
			result.addAll(loadDirectory(inkFile, coreFactory));
		}
		return result;
	}

	private static List<DslFactory> loadDirectory(File dir, DslFactory coreFactory) {
		List<DslFactory> result = new ArrayList<DslFactory>();
		for (File f : FileUtils.listInkFiles(dir)) {
			result.addAll(locateFactory(f, coreFactory));
		}
		return result;
	}

	protected static InstanceFactory getInstanceFactory() {
		return instanceFactory;
	}

	private static List<DslFactory> locateFactory(File inkFile, DslFactory coreFactory) {
		try {
			List<DslFactory> result = new ArrayList<DslFactory>();
			Tag content = SdlParser.parse(inkFile);
			List<Tag> elements = content.getChildren();
			String classId;
			InkClass readerCls = coreFactory.getObject(CoreNotations.Ids.INK_READER.toString());
			InkReader<Tag> reader = readerCls.newInstance(coreFactory.getAppContext(), false, true).getBehavior();
			DslFactory factory;
			for (Tag elem : elements) {
				try {
					classId = (String) elem.getAttribute(InkNotations.Path_Syntax.CLASS_ATTRIBUTE);
					if (classId != null) {
						// TODO need to check also if subclass
						if (classId.equals(CoreNotations.Ids.DSL_FACTORY)) {
							long start = System.currentTimeMillis();
							DslFactoryState factoryState = (DslFactoryState) reader.read(elem, coreFactory.getAppContext(), new HashMap<String, InkObjectState>());
							if(factoryState==null || reader.containsErrors()){
								String errorMsg = "Could not loaf factory in file " + inkFile.getAbsolutePath() +".";
								if(reader.containsErrors()){
									errorMsg += "Errors:";
									for(ParseError err : reader.getErrors()){
										errorMsg +=err.getDescription()+",";
									}
								}
								throw new CoreException(errorMsg);
							}
							factory = factoryState.getBehavior();
							if (!allFactories.containsKey(factory.getNamespace())) {
								System.out.println("DSL factory '" + factory.getNamespace() + "' loaded in " + (System.currentTimeMillis() - start) + " millis.");
								factory.setConfigurationFile(inkFile);
								result.add(factory);
								coreFactory.register(factoryState);
							}
						}
					}
				} catch (ClassCastException e) {
					e.printStackTrace();
					// TODO log error. no need to throw an exception, this is not the place (InkReader should do that...
				}
			}
			return result;
		} catch (IOException e) {
			throw new InkBootException("Could not open the file '" + inkFile.getAbsolutePath() + "'.", e);
		} catch (SDLParseException e) {
			throw new InkBootException("Could not parse the file '" + inkFile.getAbsolutePath() + "'.", e);
		}
	}

	private static DslFactory loadCoreFactory() {
		long start = System.currentTimeMillis();
		DslFactoryImpl<DslFactoryState> factory = new DslFactoryImpl<DslFactoryState>();
		factory.boot();
		System.out.println("DSL factory '" + factory.getNamespace() + "' loaded in " + (System.currentTimeMillis() - start) + " millis.");
		return factory;
	}

	public static Collection<DslFactory> getAllFactories() {
		return Collections.unmodifiableList(new ArrayList<DslFactory>(allFactories.values()));
	}

	public static void main(String[] args) {
	}

	public static void introduceNewDsl(String path) throws InkException {
		if (paths != null) {
			boolean found = false;
			for (String p : paths) {
				if (p.equals(path)) {
					found = true;
					break;
				}
			}
			if (!found) {
				String[] newPaths = new String[paths.length + 1];
				System.arraycopy(paths, 0, newPaths, 0, paths.length);
				newPaths[newPaths.length - 1] = path;
			}
		}
		List<DslFactory> newFactories = collectFactories(path, coreFactory);
		ModelInfoWriteableRepository repo = ModelInfoFactory.getWriteableInstance();
		for (DslFactory f : newFactories) {
			allFactories.put(f.getNamespace(), f);
			namespaces.add(f.getNamespace());
			repo.introduceNewDsl(f.getNamespace());
		}

		for (DslFactory f : newFactories) {
			f.scan();
		}
		List<DslFactory> nonCoreFactories = new ArrayList<DslFactory>(allFactories.values());
		nonCoreFactories.remove(coreFactory);
		arrangeFactories(defaultNS, nonCoreFactories);
	}

}
