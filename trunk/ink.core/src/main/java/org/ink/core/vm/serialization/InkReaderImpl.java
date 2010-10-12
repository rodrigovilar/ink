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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.ink.core.vm.lang.property.mirror.CollectionPropertyMirror;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.mirror.editor.ObjectEditor;
import org.ink.core.vm.types.EnumType;
import org.ink.core.vm.utils.InkNotations;
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
	
	@Override
	public void reset() {
		errors = new ArrayList<ParseError>();
		url = null;
	}

	protected void addError(Tag t, String description) {
		addError(t.getLineNumber(), t.getPosition(), description);
	}

	protected void addError(int lineNumber, int position, String description) {
		ParseError err = new ParseError(lineNumber, position, description, url);
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
	public List<ElementDescriptor<Tag>> extractRawData(File f) throws IOException{
		try {
			Tag tag = SdlParser.parse(f);
			List<Tag> tags = tag.getChildren();
			List<ElementDescriptor<Tag>> result = new ArrayList<ElementDescriptor<Tag>>(tags.size());
			for(Tag t : tags){
				result.add(new SdlElementDescriptor(t, f));
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
				} else {
					try {
						id = (String) en.getValue();
						id = serializationContext.getNamespace() +InkNotations.Path_Syntax.NAMESPACE_DELIMITER_C+id;
					} catch (ClassCastException e) {
						addError(tag, "Attribute '" + attName
								+ "' should be of a string type.");
					}
				}
			} else if (attName.equals(CLASS_ATTRIBUTE)) {
				if (classId != null) {
					addError(tag, "Duplicate attribute '" + attName
							+ "' was found.");
				} else {
					try {
						classId = (String) en.getValue();
					} catch (ClassCastException e) {
						addError(tag, "Attribute '" + attName
								+ "' should be of a string type.");
					}
				}
			} else if (attName.equals(REF_ATTRIBUTE)) {
				if (ref != null) {
					addError(tag, "Duplicate attribute '" + attName
							+ "' was found.");
				} else {
					try {
						ref = (String) en.getValue();
					} catch (ClassCastException e) {
						addError(tag, "Attribute '" + attName
								+ "' should be of a string type.");
					}
				}
			} else if (attName.equals(SUPER_ATTRIBUTE)) {
				if (superId != null) {
					addError(tag, "Duplicate attribute '" + attName
							+ "' was found.");
				} else {
					try {
						superId = (String) en.getValue();
					} catch (ClassCastException e) {
						addError(tag, "Attribute '" + attName
								+ "' should be of a string type.");
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
				InkObjectState result = serializationContext.getState(ref, false);
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
		InkObjectState result = null;
		if (clsState == null) {
			addError(tag, "Could not resolve class id '" + classId + "'.");
			return null;
		}else{
			InkClass cls = clsState.getBehavior();
			ClassMirror cMirror = cls.reflect();
			Map<String, PropertyMirror> propertiesMap = cMirror
					.getClassPropertiesMap();
			//no defaults here - defaults are resolved in compilation
			result = cls.newInstance(serializationContext, false, false);
			ObjectEditor editor = result.reflect().edit();
			editor.setId(id);
			editor.setRoot(!isInnerObject);
			editor.setAbstract(isAbstract);
			editor.setSuperId(superId);
			List<Tag> fields = tag.getChildren();
			PropertyMirror pm;
			String propertyName;
			for (Tag field : fields) {
				propertyName = field.getName();
				pm = propertiesMap.get(propertyName);
				if (pm == null) {
					addError(field, "The property with name '" + propertyName
							+ "' does not exist for class '" + classId + "'.");
				} else {
					editor.setPropertyValue(propertyName, transformPropertyValue(
							field, pm));
				}
			}
			if(!containsErrors()){
				if(!isInnerObject){
					try{
						editor.compile();
					}catch(Exception e){
						addError(tag, e.getMessage());
					}
				}
				editor.save();
			}
		}
		return result;
	}
	
	private Object convertPrimitiveTypeValue(Tag t,Object val, PrimitiveAttributeMirror pm){
		Object result = val;
		if(result!=null){
			try{
				switch(((PrimitiveAttributeMirror)pm).getPrimitiveTypeMarker()){
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
							((List) result).add(transformPropertyValue(t,
									innerPM));
						}
					}
					break;
				}
				break;
			case Map:
				PropertyMirror keyM = ((MapPropertyMirror) pm).getKeyMirror();
				PropertyMirror valueM = ((MapPropertyMirror) pm)
						.getValueMirror();
				List<Tag> tags = tag.getChildren();
				List<Tag> keyValue;
				result = new HashMap(tags.size());
				for (Tag t : tags) {
					boolean keyFound = false;
					boolean valueFound = false;
					keyValue = t.getChildren();
					Object mapKey = null;
					Object mapvalue = null;
					for (Tag mapEn : keyValue) {
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
		return result;
	}
	
	@Override
	public boolean containsErrors() {
		return !errors.isEmpty();
	}

}