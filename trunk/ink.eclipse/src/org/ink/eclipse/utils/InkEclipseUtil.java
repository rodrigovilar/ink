package org.ink.eclipse.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.ink.core.vm.factory.DslFactory;
import org.ink.core.vm.factory.InkVM;
import org.ink.core.vm.lang.DataTypeMarker;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.property.mirror.CollectionPropertyMirror;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.modelinfo.ModelInfoFactory;
import org.ink.core.vm.modelinfo.ModelInfoRepository;
import org.ink.core.vm.modelinfo.relations.ExtendsRelation;
import org.ink.core.vm.modelinfo.relations.IsInstanceOfRelation;
import org.ink.core.vm.utils.property.mirror.ListPropertyMirror;
import org.ink.core.vm.utils.property.mirror.MapPropertyMirror;
import org.ink.eclipse.InkPlugin;

public class InkEclipseUtil {
	
	private static String[] getScope(String ns){
		return InkVM.instance().getFactory(ns).getScope().toArray(new String[]{});
	}
	
	public static List<String> getInstances(String ns, String classId){
		List<String> classes = getSubClasses(ns, classId);
		classes.add(classId);
		Collection<InkObject> referrers = new ArrayList<InkObject>();
		ModelInfoRepository repo = ModelInfoFactory.getInstance();
		for(String clsId : classes){
			InkObject inkObject = InkPlugin.getDefault().getInkContext().getFactory().getObject(clsId, false);
			if(inkObject!=null){
				Collection<InkObject> temp = repo.findReferrers(inkObject, IsInstanceOfRelation.getInstance(), getScope(ns));
				if(temp!=null){
					referrers.addAll(temp);
				}
			}
		}
		List<String> result = new ArrayList<String>();
		if(referrers!=null){
			for(InkObject o : referrers){
				String id = o.reflect().getId();
				result.add(id);
			}
		}
		Collections.sort(result);
		return result;
	}
	
	public static List<String> getSubClasses(String ns, String classId){
		Collection<InkObject> referrers = new ArrayList<InkObject>();
		ModelInfoRepository repo = ModelInfoFactory.getInstance();
		InkObject inkObject = InkPlugin.getDefault().getInkContext().getFactory().getObject(classId, false);
		if(inkObject!=null){
			referrers = repo.findReferrers(inkObject, ExtendsRelation.getInstance(), getScope(ns));
		}
		List<String> result = new ArrayList<String>();
		if(referrers!=null){
			for(InkObject o : referrers){
				String id = o.reflect().getId();
				result.add(id);
				result.addAll(getSubClasses(ns, id));
			}
		}
		Collections.sort(result);
		return result;
	}
	
	public static Collection<PropertyMirror> getPropertiesMirrors(String classId, Collection<String> exclude){
		Collection<PropertyMirror> result = new ArrayList<PropertyMirror>();
		InkObject inkObject = InkPlugin.getDefault().getInkContext().getFactory().getObject(classId, false);
		if(inkObject!=null && inkObject.reflect().isClass()){
			ClassMirror cm = inkObject.reflect();
			Map<String, PropertyMirror> props = new HashMap<String, PropertyMirror>(cm.getClassPropertiesMap());
			for(String key : exclude){
				props.remove(key);
			}
			return props.values();
		}
		return result;
	}
	
	public static PropertyMirror getPropertyMirror(String classId, String propertyName, List<String> path){
		PropertyMirror result = null;
		InkObject inkObject = InkPlugin.getDefault().getInkContext().getFactory().getObject(classId, false);
		if(inkObject!=null && inkObject.reflect().isClass()){
			ClassMirror cm = inkObject.reflect();
			Map<String, PropertyMirror> props = cm.getClassPropertiesMap();
			if(path!=null && !path.isEmpty()){
				//TODO should handle unlimited number of inner maps and lists
				PropertyMirror temp = null;
				temp = props.get(path.get(0));
				if(temp!=null && temp.getTypeMarker()==DataTypeMarker.Collection){
					switch(((CollectionPropertyMirror)temp).getCollectionTypeMarker()){
					case List:
						result = ((ListPropertyMirror)temp).getItemMirror().getName().equals(propertyName)?
								((ListPropertyMirror)temp).getItemMirror():null;
					break;
					case Map:
						result = ((MapPropertyMirror)temp).getKeyMirror().getName().equals(propertyName)?
								((MapPropertyMirror)temp).getKeyMirror():null;
						if(result==null){
							result = ((MapPropertyMirror)temp).getKeyMirror().getName().equals(propertyName)?
									((MapPropertyMirror)temp).getKeyMirror():null;
							
						}
						break;
								
					}
				}
				
			}else{
				result = props.get(propertyName);
			}
		}
		return result;
	}
	
	public static IFile getOutputFile(IProject p, IFile sourceFile){
		try {
			IJavaProject jProject = JavaCore.create(p);
			IPath outputPath = jProject.getOutputLocation().removeFirstSegments(1);
			IPath relativeFilepath = sourceFile.getFullPath().removeFirstSegments(4);
			if(relativeFilepath.isEmpty()){
				relativeFilepath = sourceFile.getFullPath().removeFirstSegments(1);
			}
			IFolder outputFolder = p.getFolder(outputPath);
			IFile result = outputFolder.getFile(relativeFilepath);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String resolveNamespace(File f) {
		DslFactory factory = InkVM.instance().getOwnerFactory(f);
		if(factory!=null){
			return factory.getNamespace();
		}
		return null;
	}

}
