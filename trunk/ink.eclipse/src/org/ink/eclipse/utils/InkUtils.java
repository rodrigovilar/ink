package org.ink.eclipse.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.ink.core.vm.factory.Context;
import org.ink.core.vm.factory.DslFactory;
import org.ink.core.vm.factory.InkVM;
import org.ink.core.vm.factory.VM;
import org.ink.core.vm.factory.VMMain;
import org.ink.core.vm.factory.internal.CoreNotations;
import org.ink.core.vm.lang.DataTypeMarker;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.property.mirror.CollectionPropertyMirror;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.modelinfo.ModelInfoFactory;
import org.ink.core.vm.modelinfo.ModelInfoRepository;
import org.ink.core.vm.modelinfo.relations.ExtendsRelation;
import org.ink.core.vm.modelinfo.relations.IsInstanceOfRelation;
import org.ink.core.vm.utils.property.mirror.ListPropertyMirror;
import org.ink.core.vm.utils.property.mirror.MapPropertyMirror;
import org.ink.eclipse.InkPlugin;

public class InkUtils {

	private static String[] getScope(String ns) {
		return InkVM.instance().getFactory(ns).getScope().toArray(new String[] {});
	}

	public static final String getClassId(String objectId) {
		InkObjectState base = InkPlugin.getDefault().getInkContext().getState(objectId, false);
		if (base != null) {
			return base.getMeta().reflect().getId();
		}
		return null;
	}

	public static String readString(InputStream is, String encoding) {
		if (is == null) {
			return null;
		}
		BufferedReader reader = null;
		try {
			StringBuffer buffer = new StringBuffer();
			char[] part = new char[2048];
			int read = 0;
			reader = new BufferedReader(new InputStreamReader(is, encoding));

			while ((read = reader.read(part)) != -1) {
				buffer.append(part, 0, read);
			}

			return buffer.toString();

		} catch (IOException ex) {
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ex) {
				}
			}
		}
		return null;
	}

	public static Collection<Mirror> getAllClasses(String[] nss) {
		Collection<Mirror> result = new ArrayList<Mirror>();
		InkObject base = InkPlugin.getDefault().getInkContext().getObject(CoreNotations.Ids.INK_OBJECT);
		ModelInfoRepository repo = ModelInfoFactory.getInstance();
		Collection<Mirror> temp = repo.findReferrers(base.reflect(), ExtendsRelation.getInstance(), true, nss);
		if (temp != null) {
			result.addAll(temp);
		}
		return result;
	}

	public static String[] getProjectNamespaces(IProject p) {
		VM vm = InkVM.instance();
		Set<String> nss = VMMain.getDsls();
		IFile f = p.getFile("dsls.ink");
		List<String> nssList = new ArrayList<String>();
		if (f.exists()) {
			String path = f.getLocation().toFile().getAbsolutePath();
			for (String ns : nss) {
				DslFactory factory = vm.getFactory(ns);
				File factoryConfFile = factory.getConfigurationFile();
				if (factoryConfFile != null && factoryConfFile.getAbsolutePath().equals(path)) {
					nssList.add(ns);
				}
			}
		}
		return nssList.toArray(new String[] {});
	}

	public static String[] getProjectDSLs(IProject p) {
		Collection<DslFactory> factories = VMMain.getAllFactories();
		IFile f = p.getFile("dsls.ink");
		List<String> ids = new ArrayList<String>();
		if (f.exists()) {
			String path = f.getLocation().toFile().getAbsolutePath();
			for (DslFactory factory : factories) {
				File factoryConfFile = factory.getConfigurationFile();
				if (factoryConfFile != null && factoryConfFile.getAbsolutePath().equals(path)) {
					ids.add(factory.reflect().getId());
				}
			}
		}
		return ids.toArray(new String[] {});
	}

	public static Map<String, DslFactory> getProjectDSLFactories(IProject p) {
		Collection<DslFactory> factories = VMMain.getAllFactories();
		IFile f = p.getFile("dsls.ink");
		Map<String, DslFactory> result = new HashMap<String, DslFactory>();
		if (f.exists()) {
			String path = f.getLocation().toFile().getAbsolutePath();
			for (DslFactory factory : factories) {
				File factoryConfFile = factory.getConfigurationFile();
				if (factoryConfFile != null && factoryConfFile.getAbsolutePath().equals(path)) {
					result.put(factory.getNamespace(), factory);
				}
			}
		}
		return result;
	}

	public static List<String> getAllSupers(String classId) {
		Context context = InkPlugin.getDefault().getInkContext();
		List<String> result = new ArrayList<String>();
		try {
			InkObject o = context.getObject(classId);
			if (o != null) {
				Mirror m = o.reflect();
				while (m != null) {
					m = m.getSuper();
					if (m != null) {
						result.add(m.getId());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Collection<Mirror> getInstances(String classId, boolean recursive) {
		Collection<Mirror> referrers = new ArrayList<Mirror>();
		InkObject inkObject = InkPlugin.getDefault().getInkContext().getFactory().getObject(classId, false);
		if (inkObject != null) {
			ModelInfoRepository repo = ModelInfoFactory.getInstance();
			Collection<Mirror> temp = repo.findReferrers(inkObject.reflect(), IsInstanceOfRelation.getInstance(), recursive);
			if (temp != null) {
				referrers.addAll(temp);
			}
		}
		return referrers;
	}

	public static Collection<Mirror> getInstances(String[] nss, String classId, boolean recursive, boolean topLevelOnly) {
		Collection<Mirror> referrers = new ArrayList<Mirror>();
		InkObject inkObject = InkPlugin.getDefault().getInkContext().getFactory().getObject(classId, false);
		if (inkObject != null) {
			ModelInfoRepository repo = ModelInfoFactory.getInstance();
			Collection<Mirror> temp = repo.findReferrers(inkObject.reflect(), IsInstanceOfRelation.getInstance(), recursive, nss);
			if (temp != null) {
				for (Mirror m : temp) {
					if (m.isRoot() || !topLevelOnly) {
						referrers.add(m);
					}
				}
			}
		}
		return referrers;
	}

	public static List<String> getInstancesIds(String[] nss, String classId, boolean recursive) {
		return createResultList(getInstances(nss, classId, recursive, true), true);
	}

	public static List<String> getInstancesIds(String ns, String classId, boolean recursive) {
		return getInstancesIds(getScope(ns), classId, recursive);
	}

	private static List<String> createResultList(Collection<Mirror> referrers, boolean includingAbstract) {
		List<String> result = new ArrayList<String>();
		if (referrers != null) {
			for (Mirror m : referrers) {
				if (includingAbstract || !m.isAbstract()) {
					result.add(m.getId());
				}
			}
		}
		Comparator<String> compi = new Comparator<String>() {

			@Override
			public int compare(String arg0, String arg1) {
				if (arg0 == null) {
					return -1;
				} else if (arg1 == null) {
					return 1;
				}
				return arg0.compareTo(arg1);
			}

		};
		Collections.sort(result, compi);
		return result;
	}

	public static List<String> getInstances(String ns, List<String> classes, boolean rootOnly) {
		Collection<Mirror> referrers = new ArrayList<Mirror>();
		ModelInfoRepository repo = ModelInfoFactory.getInstance();
		for (String clsId : classes) {
			InkObject inkObject = InkPlugin.getDefault().getInkContext().getFactory().getObject(clsId, false);
			if (inkObject != null) {
				Collection<Mirror> temp = repo.findReferrers(inkObject.reflect(), IsInstanceOfRelation.getInstance(), false, getScope(ns));
				if (temp != null) {
					for (Mirror m : temp) {
						if (m.isRoot() || !rootOnly) {
							referrers.add(m);
						}
					}
				}
			}
		}
		return createResultList(referrers, true);
	}

	public static List<String> getSubClasses(String ns, String classId, boolean recursive, boolean includingAbstract) {
		Collection<Mirror> referrers = new ArrayList<Mirror>();
		ModelInfoRepository repo = ModelInfoFactory.getInstance();
		InkObject inkObject = InkPlugin.getDefault().getInkContext().getFactory().getObject(classId, false);
		if (inkObject != null) {
			referrers = repo.findReferrers(inkObject.reflect(), ExtendsRelation.getInstance(), recursive, getScope(ns));
		}
		return createResultList(referrers, includingAbstract);
	}

	public static Collection<PropertyMirror> getPropertiesMirrors(String classId, Collection<String> exclude) {
		Collection<PropertyMirror> result = new ArrayList<PropertyMirror>();
		InkObject inkObject = InkPlugin.getDefault().getInkContext().getFactory().getObject(classId, false);
		if (inkObject != null && inkObject.reflect().isClass()) {
			ClassMirror cm = inkObject.reflect();
			if (cm.isValid()) {
				Map<String, PropertyMirror> props = new HashMap<String, PropertyMirror>(cm.getClassPropertiesMap());
				for (String key : exclude) {
					props.remove(key);
				}
				return props.values();
			}
		}
		return result;
	}

	public static PropertyMirror getPropertyMirror(String classId, String propertyName, List<String> path) {
		PropertyMirror result = null;
		InkObject inkObject = InkPlugin.getDefault().getInkContext().getFactory().getObject(classId, false);
		if (inkObject != null && inkObject.reflect().isClass()) {
			ClassMirror cm = inkObject.reflect();
			Map<String, PropertyMirror> props = cm.getClassPropertiesMap();
			if (path != null && !path.isEmpty()) {
				// TODO should handle unlimited number of inner maps and lists
				PropertyMirror temp = null;
				temp = props.get(path.get(0));
				if (temp != null && temp.getTypeMarker() == DataTypeMarker.Collection) {
					switch (((CollectionPropertyMirror) temp).getCollectionTypeMarker()) {
					case List:
						result = ((ListPropertyMirror) temp).getItemMirror().getName().equals(propertyName) ? ((ListPropertyMirror) temp).getItemMirror() : null;
						break;
					case Map:
						result = ((MapPropertyMirror) temp).getKeyMirror().getName().equals(propertyName) ? ((MapPropertyMirror) temp).getKeyMirror() : null;
						if (result == null) {
							result = ((MapPropertyMirror) temp).getValueMirror().getName().equals(propertyName) ? ((MapPropertyMirror) temp).getValueMirror() : null;

						}
						break;

					}
				}

			} else {
				result = props.get(propertyName);
			}
		}
		return result;
	}

	public static List<IClasspathEntry> getJavaSrcPaths(IProject p) {
		List<IClasspathEntry> result = new ArrayList<IClasspathEntry>();
		try {
			IJavaProject jProject = JavaCore.create(p);
			IClasspathEntry[] paths = jProject.getResolvedClasspath(true);
			for (IClasspathEntry cpe : paths) {
				if (cpe.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
					result.add(cpe);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static List<IClasspathEntry> getJavaLibs(IProject p) {
		List<IClasspathEntry> result = new ArrayList<IClasspathEntry>();
		try {
			IJavaProject jProject = JavaCore.create(p);
			IClasspathEntry[] paths = jProject.getResolvedClasspath(true);
			for (IClasspathEntry cpe : paths) {
				if (cpe.getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
					result.add(cpe);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String resolveNamespace(File f) {
		DslFactory factory = InkVM.instance().getOwnerFactory(f);
		if (factory != null) {
			return factory.getNamespace();
		}
		return null;
	}

}
