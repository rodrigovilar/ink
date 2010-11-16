package ink.eclipse.editors.page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class InkContentAssistUtil {
	
	public static List<String> getInstances(String classId){
		List<String> classes = getSubClasses(classId);
		classes.add(classId);
		Collection<InkObject> referrers = new ArrayList<InkObject>();
		ModelInfoRepository repo = ModelInfoFactory.getInstance();
		for(String clsId : classes){
			InkObject inkObject = InkPlugin.getDefault().getInkContext().getFactory().getObject(clsId, false);
			if(inkObject!=null){
				Collection<InkObject> temp = repo.findReferrers(inkObject, IsInstanceOfRelation.getInstance());
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
	
	public static List<String> getSubClasses(String classId){
		Collection<InkObject> referrers = new ArrayList<InkObject>();
		ModelInfoRepository repo = ModelInfoFactory.getInstance();
		InkObject inkObject = InkPlugin.getDefault().getInkContext().getFactory().getObject(classId, false);
		if(inkObject!=null){
			referrers = repo.findReferrers(inkObject, ExtendsRelation.getInstance());
		}
		List<String> result = new ArrayList<String>();
		if(referrers!=null){
			for(InkObject o : referrers){
				String id = o.reflect().getId();
				result.add(id);
				result.addAll(getSubClasses(id));
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

}
