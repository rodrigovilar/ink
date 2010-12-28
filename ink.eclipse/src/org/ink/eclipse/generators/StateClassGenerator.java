package org.ink.eclipse.generators;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.ink.core.vm.lang.property.mirror.CollectionPropertyMirror;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.mirror.EnumTypeMirror;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.utils.property.mirror.ListPropertyMirror;
import org.ink.core.vm.utils.property.mirror.MapPropertyMirror;
import org.ink.eclipse.utils.EclipseUtils;
import org.ink.eclipse.utils.InkUtils;

public class StateClassGenerator implements Generator {

	public final static String LINE_SEPARATOR = System.getProperty("line.separator");

//	private final String outputFolderPath;
	private final IFolder outputFolder;

	public StateClassGenerator(IFolder outputFolder) {
		this.outputFolder = outputFolder.getFolder("gen");
		if(!this.outputFolder.exists()){
			try {
				this.outputFolder.create(IResource.FORCE | IResource.DERIVED, true, null);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void generate(Mirror mirror) {
		StringBuilder result = new StringBuilder(1000);
		if(mirror.isClass() && ((ClassMirror)mirror).getJavaMapping().hasState()){
			ClassMirror classMirror = (ClassMirror)mirror;
			ClassMirror superClass = mirror.getSuper();
			StringBuilder interfaceClass = new StringBuilder(500);
			StringBuilder innerClass = new StringBuilder(500);
			String fullJavaPackage = classMirror.getFullJavaPackage();
			interfaceClass.append("package ").append(fullJavaPackage).append(';').append(LINE_SEPARATOR);
			String className = classMirror.getShortId();
			String superClassName = superClass.getFullJavaPackage() +"." + superClass.getShortId();
			if(!classMirror.isStruct()){
				className +="State";
				superClassName +="State";
			}
			interfaceClass.append("public interface ").append(className).append(" extends ").append(superClassName)
			.append("{").append(LINE_SEPARATOR);
			innerClass.append("public class ").append("Data").append(" extends ")
			.append(superClassName + "." + "Data")
			.append(" implements ").append(className).append("{").append(LINE_SEPARATOR);
			PropertyMirror lastPM = null;
			for(PropertyMirror pm : classMirror.getOriginalProperties()){
				if(!pm.isInherited()){
					String staticName = "p_"+ pm.getName().toLowerCase();
					interfaceClass.append("public static final byte ").append(staticName)
					.append(lastPM==null?" = 0": "= p_" +lastPM.getName().toLowerCase()+"+1").append(";");
					String fielName = convertPropertyName(pm.getName());
					String[] types = resolveGetterType(pm);
					String getterSig = "public " + types[0] +" get" + fielName + "()";
					String setterSig = "public void set" + fielName + "("+types[1]+" value)";
					interfaceClass.append(getterSig).append(";");
					interfaceClass.append(setterSig).append(";");
					innerClass.append(getterSig).append("{return (").append(types[0]).append(")getValue(")
					.append(staticName).append(");}");
					innerClass.append(setterSig).append("{").append("setValue(")
					.append(staticName).append(",value);}");//setValue(p_namespace, value);

				}
				lastPM = pm;
			}
			result.append(interfaceClass).append(innerClass).append("}").append("}");
			writeFile(result.toString(), fullJavaPackage, className);
		}
	}

	protected IContainer createFolder(IFolder folder) throws CoreException {
		if (!folder.exists()) {
			createFolder((IFolder) folder.getParent());
			folder.create(IResource.FORCE | IResource.DERIVED, true, null);
		}
		return folder;
	}

	private void writeFile(String data, String fullJavaPackage, String className) {
		try{
			String relativeFolderPath = fullJavaPackage.replace(".", File.separator);
			IFolder folder = outputFolder.getFolder(relativeFolderPath);
			createFolder(folder);
			IFile f = folder.getFile(className +".java");
			boolean shouldWrite = true;
			String newData = EclipseUtils.format(data);
			if(f.exists()){
//				QualifiedName qn = new QualifiedName(null, "hash");
//				f.setPersistentProperty(key, value);
				InputStream str = f.getContents();
				String oldData = InkUtils.readString(str, Charset.defaultCharset().name());
				if(!oldData.equals(newData)){
					f.delete(true, null);
				}else{
					shouldWrite = false;
				}
			}
			if(shouldWrite){
				byte[] bytes = newData.getBytes();
				f.create(new ByteArrayInputStream(bytes, 0, bytes.length),true, null);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private String resolveRealStateClassName(ClassMirror m){
		String result = null;
		if(m.getJavaMapping().hasState()){
			result = m.getFullJavaPackage() +"."+m.getShortId();
			if(!m.isStruct()){
				result +="State";
			}
		}else{
			return resolveRealStateClassName((ClassMirror) m.getSuper());
		}
		return result;
	}

	private String resolveEnumClassName(EnumTypeMirror m){
		String result = m.getFullJavaPackage() + "." +m.getShortId();
		return result;
	}

	private String resolveRealBehaviorClassName(ClassMirror m){
		String result = null;
		if(m.getJavaMapping().hasBeahvior()){
			result = m.getFullJavaPackage() + "." + m.getShortId() +"Impl";
		}else{
			return resolveRealStateClassName((ClassMirror) m.getSuper());
		}
		return result;
	}

	private String[] resolveGetterType(PropertyMirror pm) {
		String[] typesName=new String[2];
		switch(pm.getTypeMarker()){
		case Collection:
			CollectionPropertyMirror cpm = (CollectionPropertyMirror)pm;
			switch(cpm.getCollectionTypeMarker()){
			case List:
				String[] itemTypes = resolveGetterType(((ListPropertyMirror)pm).getItemMirror());
				typesName[0] = cpm.getTypeClass().getName() +"<" +itemTypes[0]+">";
				typesName[1] = cpm.getTypeClass().getName() +"<" +itemTypes[1]+">";
				break;
			case Map:
				String[] keyTypes = resolveGetterType(((MapPropertyMirror)pm).getKeyMirror());
				String[] valTypes = resolveGetterType(((MapPropertyMirror)pm).getValueMirror());
				typesName[0] = cpm.getTypeClass().getName() +"<" +keyTypes[0]+","+valTypes[0]+">";
				typesName[1] = cpm.getTypeClass().getName() +"<" +keyTypes[1]+","+valTypes[1]+">";
				break;
			}
			break;
		case Class:
			ClassMirror propertyType = pm.getPropertyType().reflect();
			typesName[1] = resolveRealStateClassName(propertyType);
			if(propertyType.isStruct()){
				typesName[0] = typesName[1];
			}else{
				typesName[0] = resolveRealBehaviorClassName(propertyType);
			}
			break;
		case Enum:
			String enumType = resolveEnumClassName((EnumTypeMirror) pm.getPropertyType().reflect());
			typesName[0] = enumType;
			typesName[1] = enumType;
			break;
		default:
			String primitiveType = pm.getPropertyType().getTypeClass().getName();
			typesName[0] = primitiveType;
			typesName[1] = primitiveType;
			break;
		}
		return typesName;
	}

	private String convertPropertyName(String name) {
		StringBuilder result = new StringBuilder(name.length());
		result.append(Character.toUpperCase(name.charAt(0)));
		for (int i = 1; i < name.length(); i++) {
			char c = name.charAt(i);
			if (c == '_') {
				if (i < name.length() - 1) {
					i++;
					result.append(Character.toUpperCase(name.charAt(i)));
				}
			} else {
				result.append(c);
			}
		}
		return result.toString();
	}

}
