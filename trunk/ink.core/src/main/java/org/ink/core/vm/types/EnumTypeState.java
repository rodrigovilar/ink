package org.ink.core.vm.types;

import java.util.List;

import org.ink.core.vm.lang.InkTypeState;
import org.ink.core.vm.lang.JavaMapping;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;
import org.ink.core.vm.lang.internal.annotations.CoreListField;
import org.ink.core.vm.mirror.EnumTypeMirrorState;

/**
 * @author Lior Schachter
 */
@CoreClassSpec(mirrorClass = EnumTypeMirrorState.class, metaclass = EnumTypeClassState.class, javaMapping = JavaMapping.State_Behavior_Interface)
public interface EnumTypeState extends InkTypeState {

	public static final byte p_java_path = 0;
	@CoreListField(itemName = "value")
	public static final byte p_values = 1;

	public String getJavaPath();

	public void setJavaPath(String value);

	public List<String> getValues();

	public void setValues(List<String> value);

	public class Data extends InkTypeState.Data implements EnumTypeState {

		@Override
		public String getJavaPath() {
			return (String) getValue(p_java_path);
		}

		@SuppressWarnings("unchecked")
		@Override
		public List<String> getValues() {
			return (List<String>) getValue(p_values);
		}

		@Override
		public void setJavaPath(String value) {
			setValue(p_java_path, value);
		}

		@Override
		public void setValues(List<String> value) {
			setValue(p_values, value);
		}

		@Override
		public ObjectTypeMarker getObjectTypeMarker() {
			return ObjectTypeMarker.Enumeration;
		}

	}
}
