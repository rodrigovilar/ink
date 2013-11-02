package org.ink.eclipse.generators;

import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.ink.core.vm.mirror.EnumTypeMirror;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.types.EnumType;
import org.ink.core.vm.types.ObjectTypeMarker;

public class EnumGenerator extends BaseGenerator {

	public EnumGenerator(IFolder outputFolder) {
		super(outputFolder);
	}

	@Override
	public void generate(Mirror mirror) {
		if (mirror.getObjectTypeMarker() == ObjectTypeMarker.Enumeration) {
			EnumType enumType = mirror.getTargetBehavior();
			String fullJavaPack = ((EnumTypeMirror) mirror).getFullJavaPackage();
			String name = mirror.getShortId();
			StringBuilder result = new StringBuilder(1000);
			result.append("package ").append(fullJavaPack).append(";").append(LINE_SEPARATOR);
			result.append("public enum ").append(name).append("{");
			List<String> vals = enumType.getValues();
			if (!vals.isEmpty()) {
				for (int i = 0; i < vals.size(); i++) {
					String val = vals.get(i);
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
								builder.append(c);
							}
							break;
						}
					}
					result.append(builder.toString().toUpperCase()).append("(\"").append(val).append("\")");
					if (i < (vals.size() - 1)) {
						result.append(',');
					}
				}
				result.append(';');
			}
			result.append("public final String key;");
			result.append("private " + name +"(String key){this.key=key;}");
			result.append("@Override public String toString(){return key;}");
			result.append("public static final "+name +" enumValue(String val){return "+name+".valueOf(org.ink.core.vm.utils.CoreUtils.getJavaEnum(val));}");
			result.append("}");
			writeFile(result.toString(), fullJavaPack, name, true);
		}
	}

}
