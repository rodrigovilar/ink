package org.ink.eclipse.generators;

import org.eclipse.core.resources.IFolder;
import org.ink.core.vm.lang.property.mirror.CollectionPropertyMirror;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.mirror.EnumTypeMirror;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.utils.property.mirror.ListPropertyMirror;
import org.ink.core.vm.utils.property.mirror.MapPropertyMirror;

public class StateClassGenerator extends BaseGenerator {

	public StateClassGenerator(IFolder outputFolder) {
		super(outputFolder);
	}

	@Override
	public void generate(Mirror mirror) {
		if (mirror.isClass() && ((ClassMirror) mirror).getJavaMapping().hasState()) {
			StringBuilder result = new StringBuilder(1000);
			ClassMirror classMirror = (ClassMirror) mirror;
			ClassMirror superClass = mirror.getSuper();
			StringBuilder interfaceClass = new StringBuilder(500);
			StringBuilder innerClass = new StringBuilder(500);
			String fullJavaPackage = classMirror.getFullJavaPackage();
			interfaceClass.append("package ").append(fullJavaPackage).append(';').append(LINE_SEPARATOR);
			String className = classMirror.getShortId();
			while (!superClass.getJavaMapping().hasState()) {
				superClass = superClass.getSuper();
			}
			String superClassName = superClass.getFullJavaPackage() + "." + superClass.getShortId();

			if (!classMirror.isStruct()) {
				className += "State";
				superClassName += "State";
			}
			interfaceClass.append("public interface ").append(className).append(" extends ").append(superClassName).append("{").append(LINE_SEPARATOR);
			innerClass.append("public class ").append("Data").append(" extends ").append(superClassName + "." + "Data").append(" implements ").append(className).append("{").append(LINE_SEPARATOR);

			Mirror m = classMirror.getPersonality().reflect();
			PropertyMirror lastPM = null;
			for (PropertyMirror pm : m.getPropertiesMirrors()) {
				if (!pm.isInherited()) {
					String staticName = "t_" + pm.getName().toLowerCase();
					interfaceClass.append("public static final byte ").append(staticName).append(lastPM == null ? " = 0" : "= t_" + lastPM.getName().toLowerCase() + "+1").append(";");
				}
				lastPM = pm;
			}
			lastPM = null;

			for (PropertyMirror pm : classMirror.getOriginalProperties()) {
				if (!pm.isInherited()) {
					String staticName = "p_" + pm.getName().toLowerCase();
					interfaceClass.append("public static final byte ").append(staticName).append(lastPM == null ? " = 0" : "= p_" + lastPM.getName().toLowerCase() + "+1").append(";");
					String fielName = convertPropertyName(pm.getName());
					String[] types = resolveGetterType(pm);
					if (types != null) {
						String getterSig = "public " + types[0] + " get" + fielName + "()";
						String setterSig = "public void set" + fielName + "(" + types[1] + " value)";
						interfaceClass.append(getterSig).append(";");
						interfaceClass.append(setterSig).append(";");
						innerClass.append(getterSig).append("{return (").append(types[0]).append(")getValue(").append(staticName).append(");}");
						innerClass.append(setterSig).append("{").append("setValue(").append(staticName).append(",value);}");// setValue(p_namespace, value);
					}

				}
				lastPM = pm;
			}
			result.append(interfaceClass).append(innerClass).append("}").append("}");
			writeFile(result.toString(), fullJavaPackage, className, true);
		}
	}

	private String resolveRealStateClassName(ClassMirror m) {
		String result = null;
		if (m.getJavaMapping().hasState()) {
			result = m.getFullJavaPackage() + "." + m.getShortId();
			if (!m.isStruct()) {
				result += "State";
			}
		} else {
			return resolveRealStateClassName((ClassMirror) m.getSuper());
		}
		return result;
	}

	private String resolveEnumClassName(EnumTypeMirror m) {
		String result = m.getFullJavaPackage() + "." + m.getShortId();
		return result;
	}

	private String resolveRealBehaviorClassName(ClassMirror m) {
		String result = null;
		if (m.getJavaMapping().hasInterface()) {
			result = m.getFullJavaPackage() + "." + m.getShortId();
		} else {
			return resolveRealBehaviorClassName((ClassMirror) m.getSuper());
		}
		return result;
	}

	private String[] resolveGetterType(PropertyMirror pm) {
		if (pm == null || pm.getTypeMarker() == null) {
			return null;
		}
		String[] typesName = new String[2];
		switch (pm.getTypeMarker()) {
		case COLLECTION:
			CollectionPropertyMirror cpm = (CollectionPropertyMirror) pm;
			switch (cpm.getCollectionTypeMarker()) {
			case LIST:
				String[] itemTypes = resolveGetterType(((ListPropertyMirror) pm).getItemMirror());
				if (itemTypes == null) {
					return null;
				}
				typesName[0] = cpm.getTypeClass().getName() + "<" + itemTypes[0] + ">";
				typesName[1] = cpm.getTypeClass().getName() + "<" + itemTypes[1] + ">";
				break;
			case MAP:
				String[] keyTypes = resolveGetterType(((MapPropertyMirror) pm).getKeyMirror());
				String[] valTypes = resolveGetterType(((MapPropertyMirror) pm).getValueMirror());
				if (keyTypes == null || valTypes == null) {
					return null;
				}
				typesName[0] = cpm.getTypeClass().getName() + "<" + keyTypes[0] + "," + valTypes[0] + ">";
				typesName[1] = cpm.getTypeClass().getName() + "<" + keyTypes[1] + "," + valTypes[1] + ">";
				break;
			}
			break;
		case CLASS:
			ClassMirror propertyType = pm.getPropertyType().reflect();
			typesName[1] = resolveRealStateClassName(propertyType);
			if (propertyType.isStruct()) {
				typesName[0] = typesName[1];
			} else {
				typesName[0] = resolveRealBehaviorClassName(propertyType);
			}
			break;
		case ENUM:
			String enumType = resolveEnumClassName((EnumTypeMirror) pm.getPropertyType().reflect());
			typesName[0] = enumType;
			typesName[1] = enumType;
			break;
		default:
			String primitiveType = pm.getPropertyType().getJavaClass().getName();
			typesName[0] = primitiveType;
			typesName[1] = primitiveType;
			break;
		}
		return typesName;
	}

}
