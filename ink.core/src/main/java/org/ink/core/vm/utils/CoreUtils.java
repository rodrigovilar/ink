package org.ink.core.vm.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.ink.core.utils.StringUtils;
import org.ink.core.vm.exceptions.CoreException;
import org.ink.core.vm.lang.DataTypeMarker;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.Property;
import org.ink.core.vm.lang.Scope;
import org.ink.core.vm.lang.internal.ClassMirrorAPI;
import org.ink.core.vm.lang.internal.MirrorAPI;
import org.ink.core.vm.lang.property.mirror.CollectionPropertyMirror;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.proxy.Proxiability;
import org.ink.core.vm.proxy.Proxiable;
import org.ink.core.vm.proxy.Proxiable.Kind;
import org.ink.core.vm.types.CollectionTypeMarker;
import org.ink.core.vm.types.PrimitiveTypeMarker;
import org.ink.core.vm.utils.property.mirror.ListPropertyMirror;
import org.ink.core.vm.utils.property.mirror.MapPropertyMirror;
import org.ink.core.vm.utils.property.mirror.PrimitiveAttributeMirror;

/**
 * @author Lior Schachter
 */
public class CoreUtils {

	public final static String TAB = "\t";
	
	public static String newUUID(){
		return UUID.randomUUID().toString();
	}
	
	public static void toString(MirrorAPI object, ClassMirrorAPI classMirror, StringBuilder builder){
		if(object.isRoot()){
			builder.append(object.getObjectTypeMarker()).append(" ");
			builder.append(InkNotations.Path_Syntax.ID_ATTRIBUTE).append("=\"").append(object.getShortId()).append("\"").append(" ");
			if(object.isAbstract()){
				builder.append(InkNotations.Path_Syntax.ABSTRACT_ATTRIBUTE).append("=").append("true").append(" ");
			}
		}else if(object.getDefiningProperty()!=null){
			Property prop = object.getDefiningProperty().getTargetBehavior();
			builder.append(prop.getDisplayName()).append(" ");
		}else{
			builder.append(object.getObjectTypeMarker()).append(" ");
		}
		builder.append(InkNotations.Path_Syntax.CLASS_ATTRIBUTE).append("=\"").append(classMirror.getId()).append("\"");
		if(object.getSuper()!=null){
			builder.append(" ").append(InkNotations.Path_Syntax.SUPER_ATTRIBUTE).append("=\"").append(object.getSuper().getId()).append("\"");;
		}if(object.getScope()!=Scope.all){
			builder.append(" ").append(InkNotations.Path_Syntax.SCOPE_ATTRIBUTE).append("=\"").append(object.getScope()).append("\"");;
		}
		builder.append("{");
		Object o;
		boolean firstValue=true;
		for(PropertyMirror propMirror:object.getPropertiesMirrors()){
			o = object.getPropertyValue(propMirror.getIndex());
			if(o!=null){
				if(firstValue){
					firstValue = false;
					builder.append(StringUtils.LINE_SEPARATOR);
				}
				toString(propMirror, propMirror.getIndex(), builder, o, false);
			}
		}
		builder.append("}");
	}
	
	private static void toString(PropertyMirror definingProperty, byte definingPropertyIndex, StringBuilder builder, Object value, boolean isItem){
		Property prop = definingProperty.getTargetBehavior();
		switch(definingProperty.getTypeMarker()){
		case Collection:
			CollectionTypeMarker collectionMarker = ((CollectionPropertyMirror)definingProperty).getCollectionTypeMarker();
			switch (collectionMarker){
			case List:
				builder.append(prop.getDisplayName()).append("{");
				PropertyMirror itemMirror = ((ListPropertyMirror)definingProperty).getItemMirror();
				boolean isClass = itemMirror.getTypeMarker()==DataTypeMarker.Class;
				if(!((List<?>)value).isEmpty() && isClass){
					builder.append(StringUtils.LINE_SEPARATOR);
				}
				StringBuilder listBuilder = null;
				if(isClass){
					listBuilder = new StringBuilder(1000);
				}else{
					listBuilder = builder;
				}
				List<?> l = (List<?>)value;
				Object o;
				for(int i=0;i<l.size();i++){
					o = l.get(i);
					toString(itemMirror, definingPropertyIndex, listBuilder, o, true);
					if(isClass){
						listBuilder.append(StringUtils.LINE_SEPARATOR);
					}
				}
				if(listBuilder.length()>0 && isClass){
					builder.append(CoreUtils.TAB);
					char c;
					for(int i=0;i<listBuilder.length();i++){
						c = listBuilder.charAt(i);
						builder.append(c);
						if(c=='\n' && i<listBuilder.length()-1){
							builder.append(CoreUtils.TAB);
						}
					}
				}
				builder.append("}").append(StringUtils.LINE_SEPARATOR);
				break;
			case Map:
				builder.append(prop.getDisplayName()).append("{");
				Map<?,?> mapValue = (Map<?, ?>)value;
				PropertyMirror keyMirror = ((MapPropertyMirror)definingProperty).getKeyMirror();
				PropertyMirror valueMirror = ((MapPropertyMirror)definingProperty).getValueMirror();
				if(!mapValue.isEmpty()){
					builder.append(StringUtils.LINE_SEPARATOR);
				}
				Iterator<?> iter = mapValue.entrySet().iterator();
				Map.Entry<?, ?> en;
				while(iter.hasNext()){
					en = (Entry<?, ?>) iter.next();
					builder.append(CoreUtils.TAB).append("item{");
					builder.append(StringUtils.LINE_SEPARATOR);
					builder.append(CoreUtils.TAB).append("     ");
					toString(keyMirror, definingPropertyIndex, builder, en.getKey(), true);
					builder.append(CoreUtils.TAB).append("     ");//.append(valueMirror.getName()).append("=");
					toString(valueMirror, definingPropertyIndex, builder, en.getValue(), true);
					builder.append(StringUtils.LINE_SEPARATOR);
					builder.append(CoreUtils.TAB).append("}");
					builder.append(StringUtils.LINE_SEPARATOR);
				}
				builder.append("}").append(StringUtils.LINE_SEPARATOR);
				break;
			default:
				throw new UnsupportedOperationException(collectionMarker.name());
			}
			break;
		case Class:
			Mirror m = ((Proxiable)value).reflect(); 
			if(m.getOwner()==null){
				builder.append(value);
				if(!isItem){
					builder.append(StringUtils.LINE_SEPARATOR);
				}
			}else{
				if(((Proxiable)value).isProxied()){
					builder.append(prop.getDisplayName()).append(" ").append("ref=\"").append(m.getId()).append("\"");
				}else{
					String stringValue = value.toString();
					builder.append(stringValue);
				}
				if(!isItem){
					builder.append(StringUtils.LINE_SEPARATOR);
				}
			}
			break;
		case Enum:
			builder.append(prop.getDisplayName()).append(" ").append("\"" + value +"\"").append(StringUtils.LINE_SEPARATOR);
			break;
		case Primitive:
			if(((PrimitiveAttributeMirror)definingProperty).getPrimitiveTypeMarker()==PrimitiveTypeMarker.String){
				builder.append(prop.getDisplayName()).append(" ").append("\"" + value +"\"").append(StringUtils.LINE_SEPARATOR);
			}else{
				builder.append(prop.getDisplayName()).append(" ").append(value).append(StringUtils.LINE_SEPARATOR);
			}
			break;
		default:
			if(isItem){
				builder.append(value);
			}else{
				builder.append(prop.getDisplayName()).append(" ").append("\"" + value +"\"").append(StringUtils.LINE_SEPARATOR);
			}
			break;
		}
	}
	
	public static Class<?>[] getBehaviorProxyInterfaces(Class<?> behaviorClass){
		Class<?>[] interfaces = behaviorClass.getInterfaces();
		while(interfaces.length==0){
			interfaces = behaviorClass.getSuperclass().getInterfaces();
		}
		Class<?>[] behaviorProxyInterfaces = new Class<?>[interfaces.length+1];
		System.arraycopy(interfaces, 0, behaviorProxyInterfaces, 0, interfaces.length);
		behaviorProxyInterfaces[behaviorProxyInterfaces.length-1] = Proxiability.class;
		return behaviorProxyInterfaces;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object cloneOneValue(PropertyMirror mirror, Object value, boolean identicalTwin) {
		Object result = null;
		DataTypeMarker typeMarker = mirror.getTypeMarker();
		switch (typeMarker) {
		case Class:
			if(((Proxiable)value).isProxied()){
				value = ((Proxiability)value).getVanillaState();
				if (((MirrorAPI) value).isRoot()) {
					result = value;
				} else {
					result = ((MirrorAPI) value).cloneState(identicalTwin);
				}
			}
			else if(((Proxiable)value).getObjectKind()==Kind.Behavior){
				Mirror m = ((InkObject) value).reflect();
				//can't be root since then it would have had proxy
				result = m.cloneTargetState(identicalTwin);
			}else if (((MirrorAPI) value).isRoot()) {
				result = value;
			} else {
				result = ((MirrorAPI) value).cloneState(identicalTwin);
			} 
			break;
		case Collection:
			CollectionTypeMarker cMarker = ((CollectionPropertyMirror)mirror).getCollectionTypeMarker();
			switch(cMarker){
			case List:
				List<?> col = (List<?>) value;
				result = new ArrayList(col.size());
				PropertyMirror innerType = ((ListPropertyMirror) mirror).getItemMirror();
				for (Object o : col) {
					((List) result).add(cloneOneValue(innerType, o, identicalTwin));
				}
				break;
			case Map:
				Map<?, ?> map = (Map) value;
				result = new HashMap(map.size());
				PropertyMirror innerKeyType = ((MapPropertyMirror) mirror).getKeyMirror();
				innerType = ((MapPropertyMirror) mirror).getValueMirror();
				for (Map.Entry<?,?> en : map.entrySet()) {
					((Map) result).put(cloneOneValue(innerKeyType, en.getKey(), identicalTwin), cloneOneValue(innerType, en.getValue(), identicalTwin));
				}
				break;
			}
			break;
		case Enum:
			result = value;
			break;
		case Primitive:
			PrimitiveTypeMarker simpleTypeMarker = ((PrimitiveAttributeMirror) mirror).getPrimitiveTypeMarker();
			switch (simpleTypeMarker) {
			case Date:
				result = ((Date) value).clone();
				break;
			default:// String, Double, Long, Boolean, Byte, Integer, Float, Short - are immutable
				result = value;
				break;
			}
			break;
		default:
			throw new CoreException("Type not supported:" + typeMarker);
		}
		return result;
	}
	
	public static String getShortId(String id){
		return id.substring(id.indexOf(InkNotations.Path_Syntax.NAMESPACE_DELIMITER_C)+1, id.length());
	}
	
	
	
}
