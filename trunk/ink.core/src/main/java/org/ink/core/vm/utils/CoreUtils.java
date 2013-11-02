package org.ink.core.vm.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
import org.ink.core.vm.lang.exceptions.InvalidPathException;
import org.ink.core.vm.lang.internal.MirrorAPI;
import org.ink.core.vm.lang.property.mirror.CollectionPropertyMirror;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.proxy.Proxiability;
import org.ink.core.vm.proxy.Proxiable;
import org.ink.core.vm.proxy.Proxiable.Kind;
import org.ink.core.vm.types.CollectionTypeMarker;
import org.ink.core.vm.types.PrimitiveTypeMarker;
import org.ink.core.vm.utils.property.Dictionary;
import org.ink.core.vm.utils.property.ElementsDictionary;
import org.ink.core.vm.utils.property.mirror.ListPropertyMirror;
import org.ink.core.vm.utils.property.mirror.MapPropertyMirror;
import org.ink.core.vm.utils.property.mirror.PrimitiveAttributeMirror;

/**
 * @author Lior Schachter
 */
public class CoreUtils {
	
	public static final String DATE_FORMAT = "yyyy/MM/dd";
	public static final String TIME_FORMAT = "HH:mm:ss.SSS-z";
	public static final String DATE_TIME_FORMAT = DATE_FORMAT + " " +TIME_FORMAT;	

	public final static String TAB = "\t";
	private static final char PATH_SEPERATOR = '.';
	private static final char LIST_INDEX_START = '[';
	private static final char MAP_KEY_START = '<';
	private static final char MAP_KEY_END = '>';
	private static final char LIST_INDEX_END = ']';

	public static String newUUID() {
		return UUID.randomUUID().toString();
	}
	
	public static String getJavaEnum(String val){
		StringBuilder builder = new StringBuilder(val.length());
		char[] cs = val.toCharArray();
		for(int j=0;j<cs.length;j++){
			char c= cs[j];
			switch (c) {
			case '-':
				builder.append('_');
				break;
			default:
				if(Character.isWhitespace(c)){
					builder.append('_');
				}else if(Character.isJavaIdentifierPart(c)){
					builder.append(Character.toUpperCase(c));
				}
				break;
			}
		}
		return builder.toString();
	}


	
	public static void toString(Mirror object, ClassMirror classMirror, StringBuilder builder) {
		if (object.isRoot()) {
			builder.append("Ink").append(" ");
			builder.append(InkNotations.Path_Syntax.ID_ATTRIBUTE).append("=\"").append(object.getShortId()).append("\"").append(" ");
			if (object.isAbstract()) {
				builder.append(InkNotations.Path_Syntax.ABSTRACT_ATTRIBUTE).append("=").append("true").append(" ");
			}
		} else if (object.getDefiningProperty() != null) {
			Property prop = object.getDefiningProperty().getTargetBehavior();
			builder.append(prop.getDisplayName()).append(" ");
		} else {
			builder.append(object.getObjectTypeMarker()).append(" ");
		}
		builder.append(InkNotations.Path_Syntax.CLASS_ATTRIBUTE).append("=\"").append(classMirror.getId()).append("\"");
		if (object.getSuper() != null) {
			builder.append(" ").append(InkNotations.Path_Syntax.SUPER_ATTRIBUTE).append("=\"").append(object.getSuper().getId()).append("\"");
			;
		}
		if (object.getInkScope() != Scope.ALL) {
			builder.append(" ").append(InkNotations.Path_Syntax.SCOPE_ATTRIBUTE).append("=\"").append(object.getInkScope()).append("\"");
		}
		boolean hasValues=false;
		Object o;
		for (PropertyMirror propMirror : object.getPropertiesMirrors()) {
			o = object.getPropertyValue(propMirror.getIndex());
			if (o != null && !propMirror.isFinal()) {
				if(!hasValues){
					hasValues=true;
					builder.append("{");
				}
				builder.append(StringUtils.LINE_SEPARATOR);
				toString(propMirror, propMirror.getIndex(), builder, o, false, false);
			}
		}
		if(hasValues){
			builder.append(StringUtils.LINE_SEPARATOR);
			builder.append("}");
		}
	}

	private static void toString(PropertyMirror definingProperty, byte definingPropertyIndex, StringBuilder builder, Object value, boolean isItem, boolean isListItem) {
		Property prop = definingProperty.getTargetBehavior();
		String displayString = "";
		String lineBreak = "";
		DataTypeMarker marker = definingProperty.getTypeMarker(); 
		if(!isItem || !isListItem || (marker!=DataTypeMarker.ENUM && marker!=DataTypeMarker.PRIMITIVE)){
			displayString = prop.getDisplayName();
			lineBreak = StringUtils.LINE_SEPARATOR;
		}
		switch (marker) {
		case COLLECTION:
			CollectionTypeMarker collectionMarker = ((CollectionPropertyMirror) definingProperty).getCollectionTypeMarker();
			switch (collectionMarker) {
			case LIST:
				PropertyMirror itemMirror = ((ListPropertyMirror) definingProperty).getItemMirror();
				builder.append(displayString);
				if(itemMirror.getTypeMarker()!=DataTypeMarker.PRIMITIVE && itemMirror.getTypeMarker()!=DataTypeMarker.ENUM){
					builder.append("{");
				}
				
				
				serializeCollection(definingPropertyIndex, builder, (Collection<?>) value, itemMirror);
				if(itemMirror.getTypeMarker()!=DataTypeMarker.PRIMITIVE && itemMirror.getTypeMarker()!=DataTypeMarker.ENUM){
					builder.append("}");
				}
				break;
			case MAP:
				builder.append(displayString).append("{");
				Dictionary dic = ((MapPropertyMirror) definingProperty).getSpecifictation();
				PropertyMirror keyMirror = ((MapPropertyMirror) definingProperty).getKeyMirror();
				PropertyMirror valueMirror = ((MapPropertyMirror) definingProperty).getValueMirror();
				Map<?, ?> mapValue = (Map<?, ?>) value;
				if (dic instanceof ElementsDictionary) {
					serializeCollection(definingPropertyIndex, builder, mapValue.values(), valueMirror);
				} else {

					if (!mapValue.isEmpty()) {
						builder.append(lineBreak);
					}
					Iterator<?> iter = mapValue.entrySet().iterator();
					Map.Entry<?, ?> en;
					while (iter.hasNext()) {
						en = (Entry<?, ?>) iter.next();
						builder.append("item{");
						builder.append(StringUtils.LINE_SEPARATOR);
						toString(keyMirror, definingPropertyIndex, builder, en.getKey(), true, false);
						builder.append(StringUtils.LINE_SEPARATOR);
						toString(valueMirror, definingPropertyIndex, builder, en.getValue(), true, false);
						builder.append(StringUtils.LINE_SEPARATOR);
						builder.append("}");
						builder.append(StringUtils.LINE_SEPARATOR);
					}
				}
				builder.append("}");
				break;
			default:
				throw new UnsupportedOperationException(collectionMarker.name());
			}
			break;
		case CLASS:
			Mirror m = ((Proxiable) value).reflect();
			if (m.getOwner() == null) {
				if(!definingProperty.isComputed()){
					builder.append(value);
						builder.append(lineBreak);
				}
			} else {
				if (((Proxiable) value).isProxied()) {
					builder.append(displayString).append(" ").append("ref=\"").append(m.getId()).append("\"");
				} else {
					toString(((Proxiable)value).reflect(), m.getClassMirror(), builder);
				}
					
			}
			break;
		case ENUM:
			builder.append(displayString).append(" ").append("\"" + value + "\"");
			break;
		case PRIMITIVE:
			switch(((PrimitiveAttributeMirror) definingProperty).getPrimitiveTypeMarker()){
			case STRING:
				builder.append(displayString).append(" ").append("\"" + value + "\"");
				break;
			case DATE:
				Date d = (Date)value;
				SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
				builder.append(displayString).append(" ").append(sdf.format(d));
				break;
			default:
				builder.append(displayString).append(" ").append(value);
			}
			break;
		default:
			if (isItem) {
				builder.append(value);
			} else {
				builder.append(displayString).append(" ").append("\"" + value + "\"").append(lineBreak);
			}
			break;
		}
	}

	private static void serializeCollection(byte definingPropertyIndex, StringBuilder builder, Collection<?> value, PropertyMirror itemMirror) {
		boolean isClass = itemMirror.getTypeMarker() == DataTypeMarker.CLASS;
		if (!value.isEmpty() && isClass) {
			builder.append(StringUtils.LINE_SEPARATOR);
		}
		
		Collection<?> col = value;
		for (Object o : col) {
			toString(itemMirror, definingPropertyIndex, builder, o, true, true);
			if (isClass) {
				builder.append(StringUtils.LINE_SEPARATOR);
			}
		}
//		if (listBuilder.length() > 0 && isClass) {
//			builder.append(CoreUtils.TAB);
//			char c;
//			for (int i = 0; i < listBuilder.length(); i++) {
//				c = listBuilder.charAt(i);
//				builder.append(c);
////				if (c == '\n' && i < listBuilder.length() - 1) {
////					builder.append(CoreUtils.TAB);
////				}
//			}
//		}
	}

	public static Class<?>[] getBehaviorProxyInterfaces(Class<?> behaviorClass) {
		Class<?>[] interfaces = behaviorClass.getInterfaces();
		if (interfaces.length == 0) {
			Class<?> s = behaviorClass.getSuperclass();
			while (interfaces.length == 0 && s != null) {
				interfaces = s.getInterfaces();
				s = s.getSuperclass();
			}
		}
		Class<?>[] behaviorProxyInterfaces = new Class<?>[interfaces.length + 1];
		System.arraycopy(interfaces, 0, behaviorProxyInterfaces, 0, interfaces.length);
		behaviorProxyInterfaces[behaviorProxyInterfaces.length - 1] = Proxiability.class;
		return behaviorProxyInterfaces;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object cloneOneValue(PropertyMirror mirror, Object value, boolean identicalTwin) {
		Object result = null;
		DataTypeMarker typeMarker = mirror.getTypeMarker();
		switch (typeMarker) {
		case CLASS:
			if (((Proxiable) value).isProxied()) {
				value = ((Proxiability) value).getVanillaState();
				if (((MirrorAPI) value).isRoot()) {
					result = value;
				} else {
					result = ((MirrorAPI) value).cloneState(identicalTwin);
				}
			} else if (((Proxiable) value).getObjectKind() == Kind.BEHAVIOR) {
				Mirror m = ((InkObject) value).reflect();
				// can't be root since then it would have had proxy
				result = m.cloneTargetState(identicalTwin);
			} else if (((MirrorAPI) value).isRoot()) {
				result = value;
			} else {
				result = ((MirrorAPI) value).cloneState(identicalTwin);
			}
			break;
		case COLLECTION:
			CollectionTypeMarker cMarker = ((CollectionPropertyMirror) mirror).getCollectionTypeMarker();
			switch (cMarker) {
			case LIST:
				List<?> col = (List<?>) value;
				result = new ArrayList(col.size());
				PropertyMirror innerType = ((ListPropertyMirror) mirror).getItemMirror();
				for (Object o : col) {
					((List) result).add(cloneOneValue(innerType, o, identicalTwin));
				}
				break;
			case MAP:
				Map<?, ?> map = (Map) value;
				result = ((MapPropertyMirror) mirror).getNewInstance();
				PropertyMirror innerKeyType = ((MapPropertyMirror) mirror).getKeyMirror();
				innerType = ((MapPropertyMirror) mirror).getValueMirror();
				for (Map.Entry<?, ?> en : map.entrySet()) {
					((Map) result).put(cloneOneValue(innerKeyType, en.getKey(), identicalTwin), cloneOneValue(innerType, en.getValue(), identicalTwin));
				}
				break;
			}
			break;
		case ENUM:
			result = value;
			break;
		case PRIMITIVE:
			PrimitiveTypeMarker simpleTypeMarker = ((PrimitiveAttributeMirror) mirror).getPrimitiveTypeMarker();
			switch (simpleTypeMarker) {
			case DATE:
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

	public static String getShortId(String id) {
		return id.substring(id.indexOf(InkNotations.Path_Syntax.NAMESPACE_DELIMITER_C) + 1, id.length());
	}
	
	public static void validatePath(ClassMirror cm, String path) throws InvalidPathException {
		if (path != null && path.length() > 0) {
			try {
				String[] segments = splitPath(path);
				PropertyMirror propertyMirror = cm.getClassPropertyMirror(segments[0]);
				if (propertyMirror == null) {
					throw new InvalidPathException("Invalid path : The class '" + cm.getId() +"' does not define the property '" + segments[0] +"'.");
				}
				if (segments[1] != null) {
					switch (propertyMirror.getTypeMarker()) {
					case COLLECTION:
						CollectionPropertyMirror colMirror = (CollectionPropertyMirror) propertyMirror;
						switch (colMirror.getCollectionTypeMarker()) {
						case LIST:
							int indexLoc = getListEndIndexLocation(propertyMirror.getName(), segments[1], LIST_INDEX_START, LIST_INDEX_END);
							String indexStr = segments[1].substring(1, indexLoc);
							try{
								int index = Integer.valueOf(indexStr);
								if(index <0){
									throw new InvalidPathException("Invalid path : list '" + propertyMirror.getName() +"' index should be a positive integer and not '" + indexStr +"'.");
								}
								if(segments[1].length()-1 > indexLoc){
									PropertyMirror valueMirror = ((ListPropertyMirror)propertyMirror).getItemMirror();
									if(valueMirror.getTypeMarker()!=DataTypeMarker.CLASS){
										throw new InvalidPathException("Invalid path : list '" + propertyMirror.getName() +"' is not of user-defined class.");
									}
									
									validatePath((ClassMirror)valueMirror.getPropertyType().reflect(), segments[1].substring(indexLoc+2, segments[1].length())); 
								}
							}catch(NumberFormatException e){
								throw new InvalidPathException("Invalid path : list '" + propertyMirror.getName() +"' index should be a positive integer and not '" + indexStr +"'.");
							}
							
							break;
						case MAP:
							PropertyMirror keyMirror = ((MapPropertyMirror)propertyMirror).getKeyMirror();
							if(keyMirror.getTypeMarker()!=DataTypeMarker.PRIMITIVE){
								throw new InvalidPathException("Invalid path : map '" + propertyMirror.getName() +"' key should be of a simple type.");
							}
							int keyLoc = getMapKeyEndLocation(propertyMirror.getName(), segments[1], MAP_KEY_START, MAP_KEY_END);
							String keyStr = segments[1].substring(1, keyLoc);
							Object key = convertPrimitiveTypeValue(propertyMirror.getName(),keyStr, (PrimitiveAttributeMirror)keyMirror);	
							if(segments[1].length()-1 > keyLoc){
								PropertyMirror valueMirror = ((MapPropertyMirror)propertyMirror).getValueMirror();
								if(valueMirror.getTypeMarker()!=DataTypeMarker.CLASS){
									throw new InvalidPathException("Invalid path : map '" + propertyMirror.getName() +"' value is not of user-defined class.");
								}
								validatePath((ClassMirror) valueMirror.getPropertyType().reflect(), segments[1].substring(keyLoc+2, segments[1].length())); 
							}
							
							break;
						}
						break;
					case CLASS:
						validatePath((ClassMirror) propertyMirror.getPropertyType().reflect(), segments[1].substring(1, segments[1].length()));
						break;
					default:
						break;
					}
				}
			}catch(InvalidPathException e){
				throw e;
			}
			catch (Exception e) {
				throw new InvalidPathException("Could not parse path :'" + path + "'.", e);
			}
			
		}
	}
	
	private static String[] splitPath(String path){
		int firstSegmentLocation = getFirstSegment(path);
		String[] segments = new String[2];
		if (firstSegmentLocation > 0) {
			segments[0] = path.substring(0, firstSegmentLocation);
			segments[1] = path.substring(firstSegmentLocation, path.length());
		} else if (firstSegmentLocation < 0) {
			segments[0] = path;
		} else if (firstSegmentLocation == 0) {
			throw new InvalidPathException("path can't start with '" + PATH_SEPERATOR +"'.");
		}
		return segments;
	}

	public static Object getValue(Mirror mirror, String path) throws InvalidPathException {
		Object result = null;
		try {
			if (path == null || path.length() == 0) {
				result = mirror.getTargetBehavior();
			} else {
				String[] segments = splitPath(path);
				PropertyMirror propertyMirror = getPropertyMirror(mirror, segments[0]);
				if (propertyMirror == null) {
					throw new InvalidPathException("Invalid path : The class '" + mirror.getClassMirror().getId() +"' does not define the property '" + segments[0] +"'.");
				}
				result = mirror.getPropertyValue(propertyMirror.getIndex());
				if(result==null){
					return null;
				}
//				if(result==null && segments[1]!=null){
//					throw new InvalidPathException("Invalid path : property value '"+segments[0] +"', should not be NULL.");
//				}
				if (segments[1] != null) {
					switch (propertyMirror.getTypeMarker()) {
					case COLLECTION:
						CollectionPropertyMirror colMirror = (CollectionPropertyMirror) propertyMirror;
						switch (colMirror.getCollectionTypeMarker()) {
						case LIST:
							int indexLoc = getListEndIndexLocation(propertyMirror.getName(),segments[1], LIST_INDEX_START, LIST_INDEX_END);
							String indexStr = segments[1].substring(1, indexLoc);
							try{
								int index = Integer.valueOf(indexStr);
								if(index <0 || index >= ((List)result).size()){
									throw new InvalidPathException("Invalid path : list '" + propertyMirror.getName() +"' index should be between 0 to " + (((List)result).size()-1)+" and not '" + indexStr +"'.");
								}
								result=((List)result).get(index);
								if(result==null){
									throw new InvalidPathException("Invalid path : list value '"+segments[0] + LIST_INDEX_START +indexStr +LIST_INDEX_END +"', should not be NULL.");
								}
								if(segments[1].length()-1 > indexLoc){
									PropertyMirror valueMirror = ((ListPropertyMirror)propertyMirror).getItemMirror();
									if(valueMirror.getTypeMarker()!=DataTypeMarker.CLASS){
										throw new InvalidPathException("Invalid path : list '" + propertyMirror.getName() +"' is not of user-defined class.");
									}
									Mirror innerMirror = ((Proxiable) result).reflect();
									result = getValue(innerMirror, segments[1].substring(indexLoc+2, segments[1].length())); 
								}
							}catch(NumberFormatException e){
								throw new InvalidPathException("Invalid path : list '" + propertyMirror.getName() +"' index should be between 0 to " + (((List)result).size()-1)+" and not '" + indexStr +"'.");
							}
							
							break;
						case MAP:
							PropertyMirror keyMirror = ((MapPropertyMirror)propertyMirror).getKeyMirror();
							if(keyMirror.getTypeMarker()!=DataTypeMarker.PRIMITIVE){
								throw new InvalidPathException("Invalid path : map '" + propertyMirror.getName() +"' key should be of a simple type.");
							}
							int keyLoc = getMapKeyEndLocation(propertyMirror.getName(), segments[1], MAP_KEY_START, MAP_KEY_END);
							String keyStr = segments[1].substring(1, keyLoc);
							Object key = convertPrimitiveTypeValue(propertyMirror.getName(), keyStr, (PrimitiveAttributeMirror)keyMirror);	
							result=((Map)result).get(key);
							if(result==null){
								throw new InvalidPathException("Invalid path : map value '"+segments[0] + MAP_KEY_START +keyStr +MAP_KEY_END +"', should not be NULL.");
							}
							if(segments[1].length()-1 > keyLoc){
								PropertyMirror valueMirror = ((MapPropertyMirror)propertyMirror).getValueMirror();
								if(valueMirror.getTypeMarker()!=DataTypeMarker.CLASS){
									throw new InvalidPathException("Invalid path : map '" + propertyMirror.getName() +"' value is not of user-defined class.");
								}
								Mirror innerMirror = ((Proxiable) result).reflect();
								result = getValue(innerMirror, segments[1].substring(keyLoc+2, segments[1].length())); 
							}
							
							break;
						}
						break;
					case CLASS:
						Mirror innerMirror = ((Proxiable) result).reflect();
						result = getValue(innerMirror, segments[1].substring(1, segments[1].length()));
						break;
					default:
						throw new InvalidPathException("Invalid path : property '" + segments[0] +"' is of simple type. Please remove remaining path ('"+segments[1] +"')");
					}
				}
			}
		}catch(InvalidPathException e){
			throw e;
		}
		catch (Exception e) {
			throw new InvalidPathException("Could not parse path :'" + path + "'.", e);
		}

		return result;
	}

	private static int getMapKeyEndLocation(String mapName, String lastSegments, char mapIndexStart, char mapIndexEnd) {
		if(lastSegments.charAt(0)!='<'){
			throw new InvalidPathException("Invalid path : map '" +mapName +", expecting key definition <..>.");
		}
		int keyEndLocation = lastSegments.indexOf('>');
		if(keyEndLocation<0){
			throw new InvalidPathException("Invalid path : map '" +mapName +", expecting key definition <..>.");
		}
		return keyEndLocation;
	}
	
	private static int getListEndIndexLocation(String listName, String lastSegments, char listIndexStart, char listIndexEnd) {
		if(lastSegments.charAt(0)!='['){
			throw new InvalidPathException("Invalid path : list '" +listName +", expecting index definition <..>.");
		}
		int keyEndLocation = lastSegments.indexOf(']');
		if(keyEndLocation<0){
			throw new InvalidPathException("Invalid path : list '" +listName +", expecting index definition <..>.");
		}
		return keyEndLocation;
	}

	private static PropertyMirror getPropertyMirror(Mirror mirror, String firstSegment) {
		return mirror.getPropertyMirror(firstSegment);
	}

	private static int getFirstSegment(String path) {
		int result = path.indexOf(PATH_SEPERATOR);
		int tmp = path.indexOf(LIST_INDEX_START);
		result = result < 0 || (tmp > 0 && tmp < result)? tmp : result;
		tmp = path.indexOf(MAP_KEY_START);
		result = result < 0 || (tmp > 0 && tmp < result)? tmp : result;
		return result;
	}
	
	public static Object convertPrimitiveTypeValue(String mapName, String val, PrimitiveAttributeMirror pm) {
		Object result = val;
		if (result != null) {
			try {
				switch ((pm).getPrimitiveTypeMarker()) {
				case BOOLEAN:
					if(val.equals("true")){
						result = Boolean.TRUE;
					}else if(val.equals("false")){
						result = Boolean.FALSE;
					}else{
						throw new InvalidPathException("Invalid path : map '" +mapName +" key definition <"+val+">, expecting true/false.");
					}
					break;
				case BYTE:
					result = Byte.valueOf(val);
					break;
				case DOUBLE:
					result = Double.valueOf(val);
					break;
				case FLOAT:
					result = Float.valueOf(val);
					break;
				case INTEGER:
					result = Integer.valueOf(val);
					break;
				case LONG:
					result = Long.valueOf(val);
					break;
				case SHORT:
					result = Short.valueOf(val);
					break;
				case DATE:
					throw new InvalidPathException("Invalid path : map '" +mapName +". Date keys are not supported.");
				}
			} catch (NumberFormatException e) {
				throw new InvalidPathException("Invalid path : map '" + mapName +"' index should be a number and not '" + val +"'.");
			}
		}
		return result;
	}

}
