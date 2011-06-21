package org.ink.core.vm.serialization;

import static org.ink.core.vm.utils.InkNotations.Path_Syntax.ABSTRACT_ATTRIBUTE;
import static org.ink.core.vm.utils.InkNotations.Path_Syntax.CLASS_ATTRIBUTE;
import static org.ink.core.vm.utils.InkNotations.Path_Syntax.ID_ATTRIBUTE;
import static org.ink.core.vm.utils.InkNotations.Path_Syntax.REF_ATTRIBUTE;
import static org.ink.core.vm.utils.InkNotations.Path_Syntax.SUPER_ATTRIBUTE;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ikayzo.sdl.SDLParseException;
import org.ikayzo.sdl.Tag;
import org.ink.core.utils.sdl.SdlParser;
import org.ink.core.vm.exceptions.CoreException;
import org.ink.core.vm.factory.Context;
import org.ink.core.vm.factory.DslFactory;
import org.ink.core.vm.factory.ElementDescriptor;
import org.ink.core.vm.factory.InkVM;
import org.ink.core.vm.factory.SdlElementDescriptor;
import org.ink.core.vm.lang.InkClass;
import org.ink.core.vm.lang.InkClassState;
import org.ink.core.vm.lang.InkObjectImpl;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.Property;
import org.ink.core.vm.lang.internal.MirrorAPI;
import org.ink.core.vm.lang.property.mirror.CollectionPropertyMirror;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.proxy.Proxiable;
import org.ink.core.vm.types.EnumType;
import org.ink.core.vm.utils.InkNotations;
import org.ink.core.vm.utils.property.Dictionary;
import org.ink.core.vm.utils.property.ElementsDictionary;
import org.ink.core.vm.utils.property.mirror.ListPropertyMirror;
import org.ink.core.vm.utils.property.mirror.MapPropertyMirror;
import org.ink.core.vm.utils.property.mirror.PrimitiveAttributeMirror;

/**
 * @author Lior Schachter
 */
public class InkReaderImpl<S extends InkReaderState> extends InkObjectImpl<S>
implements InkReader<Tag>{


	private List<ParseError> errors = new ArrayList<ParseError>();
	private URL url = null;
	private DslFactory serializationContext = null;
	private Map<String, MirrorAPI> objects = new HashMap<String, MirrorAPI>();
	private boolean containsError = false;

	@Override
	public void reset() {
		errors = new ArrayList<ParseError>();
		url = null;
		objects = new HashMap<String, MirrorAPI>();
	}

	protected void addError(Tag t, String description) {
		addError(t.getLineNumber(), t.getPosition(), description);
	}

	protected void addError(int lineNumber, int position, String description) {
		ParseError err = new ParseError(lineNumber, position, description, url);
		containsError = true;
		errors.add(err);
	}

	@Override
	public List<ParseError> getErrors() {
		return errors;
	}

	@Override
	public InkObjectState[] read(File f) throws IOException {
		return read(f, InkVM.instance().getContext());
	}

	@Override
	public InkObjectState[] read(File f, Context context) throws IOException {
		this.serializationContext = context.getFactory();
		return read(f.toURI().toURL());
	}

	@Override
	public InkObjectState[] read(URL url) throws IOException {
		return read(url, InkVM.instance().getContext());
	}

	@Override
	public InkObjectState[] read(URL url, Context context) throws IOException {
		this.serializationContext = context.getFactory();
		try {
			this.url = url;
			Tag tag = SdlParser.parse(url);
			return transform(tag);
		} catch (SDLParseException e) {
			addError(e.getLine(), e.getPosition(), e.getMessage());
		}
		return null;

	}

	@Override
	public InkObjectState[] read(String data) {
		return read(data, InkVM.instance().getContext());
	}

	@Override
	public InkObjectState[] read(String data, Context context) {
		this.serializationContext = context.getFactory();
		try {
			Tag tag = SdlParser.parse(data);
			return transform(tag);
		} catch (SDLParseException e) {
			addError(e.getLine(), e.getPosition(), e.getMessage());
		}
		return null;
	}

	@Override
	public List<ElementDescriptor<Tag>> extractRawData(File f, Context context) throws IOException{
		try {
			Tag tag = SdlParser.parse(f);
			List<Tag> tags = tag.getChildren();
			List<ElementDescriptor<Tag>> result = new ArrayList<ElementDescriptor<Tag>>(tags.size());
			for(Tag t : tags){
				result.add(new SdlElementDescriptor(context.getNamespace(),t, f));
			}
			return result;
		} catch (SDLParseException e) {
			addError(e.getLine(), e.getPosition(), e.getMessage());
		}
		return null;
	}

	@Override
	public InkObjectState read(Tag data) {
		return read(data, InkVM.instance().getContext());
	}

	@Override
	public InkObjectState read(Tag data, Context context) {
		this.serializationContext = context.getFactory();
		return createInkObject(data, false);
	}

	protected InkObjectState[] transform(Tag t) {
		List<Tag> objects = t.getChildren();
		InkObjectState[] result = new InkObjectState[objects.size()];
		for (int i = 0; i < objects.size(); i++) {
			result[i] = createInkObject(objects.get(i), false);
		}
		return result;
	}

	protected InkObjectState createInkObject(Tag tag, boolean isInnerObject) {
		String id = null;
		String classId = null;
		String ref = null;
		String superId = null;
		Boolean isAbstract = null;
		Map<String, Object> attributes = tag.getAttributes();
		String attName;
		for (Map.Entry<String, Object> en : attributes.entrySet()) {
			attName = en.getKey();
			if (attName.equals(ID_ATTRIBUTE)) {
				if (isInnerObject) {
					addError(tag, "The attribute '" + attName
							+ "' is invalid in this context.");
				} else if (id != null) {
					addError(tag, "Duplicate attribute '" + attName
							+ "' was found.");
					return null;
				} else {
					try {
						id = (String) en.getValue();
						if(id==null || id.trim().length()==0){
							addError(tag, "Attribute '" + attName
									+ "' should not be empty.");
						}
						id = serializationContext.getNamespace() +InkNotations.Path_Syntax.NAMESPACE_DELIMITER_C+id;
					} catch (ClassCastException e) {
						addError(tag, "Attribute '" + attName
								+ "' should be of a string type.");
						return null;
					}
				}
			} else if (attName.equals(CLASS_ATTRIBUTE)) {
				if (classId != null) {
					addError(tag, "Duplicate attribute '" + attName
							+ "' was found.");
					return null;
				} else {
					try {
						classId = (String) en.getValue();
						if(classId==null || classId.trim().length()==0){
							addError(tag, "Attribute '" + attName
									+ "' should not be empty.");
						}
						if(classId.indexOf(InkNotations.Path_Syntax.NAMESPACE_DELIMITER_C)<0){
							classId = serializationContext.getNamespace() +InkNotations.Path_Syntax.NAMESPACE_DELIMITER_C+classId;
						}
					} catch (ClassCastException e) {
						addError(tag, "Attribute '" + attName
								+ "' should be of a string type.");
						return null;
					}
				}
			} else if (attName.equals(REF_ATTRIBUTE)) {
				if (ref != null) {
					addError(tag, "Duplicate attribute '" + attName
							+ "' was found.");
					return null;
				} else {
					try {
						ref = (String) en.getValue();
						if(ref==null || ref.trim().length()==0){
							addError(tag, "Attribute '" + attName
									+ "' should not be empty.");
						}
						if(ref.indexOf(InkNotations.Path_Syntax.NAMESPACE_DELIMITER_C)<0){
							ref = serializationContext.getNamespace() +InkNotations.Path_Syntax.NAMESPACE_DELIMITER_C+ref;
						}
					} catch (ClassCastException e) {
						addError(tag, "Attribute '" + attName
								+ "' should be of a string type.");
						return null;
					}
				}
			} else if (attName.equals(SUPER_ATTRIBUTE)) {
				if (superId != null) {
					addError(tag, "Duplicate attribute '" + attName
							+ "' was found.");
				} else {
					try {
						superId = (String) en.getValue();
						if(superId==null || superId.trim().length()==0){
							addError(tag, "Attribute '" + attName
									+ "' should not be empty.");
							return null;
						}
						if(superId.indexOf(InkNotations.Path_Syntax.NAMESPACE_DELIMITER_C)<0){
							superId = serializationContext.getNamespace() +InkNotations.Path_Syntax.NAMESPACE_DELIMITER_C+superId;
						}
					} catch (ClassCastException e) {
						addError(tag, "Attribute '" + attName
								+ "' should be of a string type.");
						return null;
					}
				}
			} else if (attName.equals(ABSTRACT_ATTRIBUTE)) {
				if (isAbstract != null) {
					addError(tag, "Duplicate attribute '" + attName
							+ "' was found.");
				} else {
					try {
						isAbstract = (Boolean) en.getValue();
					} catch (ClassCastException e) {
						addError(tag, "Attribute '" + attName
								+ "' should be of a boolean type.");
					}
				}
			} else {
				addError(tag, "The attribute '" + attName
						+ "' is invalid in this context.");
			}

		}
		// todo should support no classId if superId exists
		if (ref != null) {
			if (classId != null) {
				addError(tag,
				"The attribute 'class' is invalid in this context.");
				return null;
			} else {
				InkObjectState result = objects.get(ref);
				if(result==null){
					result = serializationContext.getState(ref, false);
					if(result==null){
						ElementDescriptor<?> desc = serializationContext.getDescriptor(ref);
						if(desc!=null && !desc.isValid()){
							return null;
						}
					}
				}
				if (result == null) {
					addError(tag, "Could not find Ink object with id '" + ref
							+ "'.");
				}
				return result;
			}
		}
		if (classId == null) {
			addError(tag, "Attribute 'classId' must be assigned a value.");
			return null;
		}
		return createInkObject(tag, id, classId, superId,
				isAbstract == null ? false : isAbstract, isInnerObject);
	}

	protected InkObjectState createInkObject(Tag tag, String id,
			String classId, String superId, boolean isAbstract, boolean isInnerObject) {
		InkClassState clsState = serializationContext.getState(classId, false);
		MirrorAPI result = null;
		if (clsState == null) {
			containsError = true;
			if(serializationContext.getDescriptor(classId)==null){
				addError(tag, "Could not resolve class id '" + classId + "'.");
			}
			return null;
		}else if(!clsState.reflect().isValid()){
			containsError = true;
			return null;
		}else{
			InkClass cls = clsState.getBehavior();
			result = cls.newInstance(serializationContext, false, false);
			try{
				ClassMirror cMirror = cls.reflect();
				Map<String, PropertyMirror> propertiesMap = cMirror
				.getClassPropertiesMap();
				//no defaults here - defaults are resolved in compilation
				result.setId(id);
				result.setRoot(!isInnerObject);
				result.setAbstract(isAbstract);
				result.setSuperId(superId);
				if(id!=null){
					objects.put(id, result);
				}
				List<Tag> fields = tag.getChildren();
				PropertyMirror pm;
				String propertyName;
				Set<String> visited = new HashSet<String>(fields.size());
				for (Tag field : fields) {
					propertyName = field.getName();
					if(visited.contains(propertyName)){
						addError(field, "The property with name '" + propertyName +"' appears more than once.");
						continue;
					}
					visited.add(propertyName);
					pm = propertiesMap.get(propertyName);
					if (pm == null) {
						addError(field, "The property with name '" + propertyName
								+ "' does not exist for class '" + classId + "'.");
					} else {
						result.setRawValue(pm.getIndex(), transformPropertyValue(
								field, pm));
					}
				}
				if(!containsErrors()){
					if(!isInnerObject){
						try{
							result.reflect().edit().compile();
						}catch(Exception e){
							addError(tag, e.getMessage());
						}
					}
				}
			}catch(Throwable e){
				addError(tag, "Could not read object '"+id +"'.");
			}
		}
		return result;
	}

	private Object convertPrimitiveTypeValue(Tag t,Object val, PrimitiveAttributeMirror pm){
		Object result = val;
		if(result!=null){
			try{
				switch((pm).getPrimitiveTypeMarker()){
				case Byte:
					result = ((Number)val).byteValue();
					break;
				case Double:
					result = ((Number)val).doubleValue();
					break;
				case Float:
					result = ((Number)val).floatValue();
					break;
				case Integer:
					result = ((Number)val).intValue();
					break;
				case Long:
					result = ((Number)val).longValue();
					break;
				case Short:
					result = ((Number)val).shortValue();
					break;
				case Date:
					result = ((Calendar)val).getTime();
					break;
				}
			}catch(ClassCastException e){
				//				addError(t, "Property '" + pm.getName() +"' is of '" +pm.getPropertyType().reflect().getId()
				//						+" type, while the value is of '" + val.getClass() +"'");
			}
			//no need to add an error here - validation framework should report the error
			//			if(!pm.getPropertyType().getTypeClass().equals(result.getClass())){
			//				addError(t, "Property '" + pm.getName() +"' is of '" +pm.getPropertyType().reflect().getId()
			//						+" type, while the value is of '" + result.getClass() +"'");
			//				result = null;
			//			}
		}
		return result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Object transformPropertyValue(Tag tag, PropertyMirror pm) {
		Object result = null;
		try{
			switch (pm.getTypeMarker()) {
			case Primitive:
				result = convertPrimitiveTypeValue(tag, tag.getValue(), (PrimitiveAttributeMirror) pm);
				break;
			case Collection:
				switch (((CollectionPropertyMirror) pm).getCollectionTypeMarker()) {
				case List:
					PropertyMirror itemMirror = ((ListPropertyMirror) pm).getItemMirror();
					switch (itemMirror.getTypeMarker()) {
					case Primitive:
						List<?> values = tag.getValues();
						result = new ArrayList<Object>(values.size());
						for(Object o : values){
							((List)result).add(convertPrimitiveTypeValue(tag, o, (PrimitiveAttributeMirror) itemMirror));
						}
						break;
					case Enum:
						// result = new ArrayList<Object>(pt.getValues());
						break;
					default:
						List<Tag> tags = tag.getChildren();
						result = new ArrayList<Object>(tags.size());
						PropertyMirror innerPM = ((ListPropertyMirror) pm)
						.getItemMirror();
						for (Tag t : tags) {
							if (!t.getName().equals(innerPM.getName())) {
								addError(t, "Invalid list item name '"
										+ t.getName() + "'. Expected name '"
										+ pm.getName() + "'.");
							} else {
								Object o = transformPropertyValue(t, innerPM);
								if(o!=null){
									((List) result).add(o);
								}
							}
						}
						break;
					}
					break;
				case Map:
					result = loadMap(tag, pm);
					break;
				}
				break;
			case Class:
				result = createInkObject(tag, true);
				break;
			case Enum:
				try {
					result = ((EnumType) ((Property) pm.getTargetBehavior())
							.getType()).getEnumObject(tag.getValue().toString());
				} catch (CoreException e) {
					addError(tag, "Invalid enumeration value : '" + tag.getValue()
							+ "'");
				}
				break;
			}
		}catch(CoreException e){
			addError(tag, "Invalid property value '" + pm.getName() +"':"+e.getMessage());
		}catch(Throwable e){
			e.printStackTrace();
			addError(tag, "Invalid property value '" + pm.getName() +"'.");
		}
		return result;
	}

	private Object loadMap(Tag tag, PropertyMirror pm) {
		Object result;
		MapPropertyMirror mapPM = (MapPropertyMirror) pm;
		PropertyMirror keyM = ((MapPropertyMirror) pm).getKeyMirror();
		PropertyMirror valueM = ((MapPropertyMirror) pm).getValueMirror();
		List<Tag> tags = tag.getChildren();
		List<Tag> entries;
		result = mapPM.getNewInstance();
		Dictionary spec = mapPM.getSpecifictation();
		boolean isKeyValue = true;
		if(spec instanceof ElementsDictionary){
			isKeyValue = false;
		}
		for (Tag t : tags) {
			boolean keyFound = false;
			boolean valueFound = false;
			entries = t.getChildren();
			Object mapKey = null;
			Object mapvalue = null;
			if(isKeyValue){
				for (Tag mapEn : entries) {
					if (mapEn.getName().equals(keyM.getName())) {
						keyFound = true;
						mapKey = transformPropertyValue(mapEn, keyM);
					} else if (mapEn.getName().equals(valueM.getName())) {
						valueFound = true;
						mapvalue = transformPropertyValue(mapEn, valueM);
					} else {
						addError(mapEn,
								"Unexpected field found inside map item. Expected fields are '"
								+ keyM.getName() + "','"
								+ valueM.getName() + "'.");
					}
				}
			}else{
				mapvalue = transformPropertyValue(t, valueM);
				mapKey = ((Proxiable)mapvalue).reflect().getPropertyValue(keyM.getName());
				keyFound = true;
				valueFound = true;
			}

			if (!keyFound) {
				addError(t, "could not find map key '" + keyM.getName()
						+ "'.");
			}
			if (!valueFound) {
				addError(t, "could not find map value '"
						+ valueM.getName() + "'.");
			}
			if (mapKey != null && mapvalue != null) {
				((Map) result).put(mapKey, mapvalue);
			}
		}
		return result;
	}

	@Override
	public boolean containsErrors() {
		return containsError;
	}

}
