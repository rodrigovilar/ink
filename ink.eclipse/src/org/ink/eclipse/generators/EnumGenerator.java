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
					result.append(vals.get(i));
					if (i < (vals.size() - 1)) {
						result.append(',');
					}
				}
				result.append(';');
			}

			result.append("}");
			writeFile(result.toString(), fullJavaPack, name, true);
		}
	}

}
