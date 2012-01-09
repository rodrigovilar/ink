package org.ink.eclipse.generators;

import org.eclipse.core.resources.IFolder;
import org.ink.core.vm.factory.resources.DefaultResourceResolver;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.mirror.Mirror;

public class InterfaceClassGenerator extends BaseGenerator {

	public InterfaceClassGenerator(IFolder outputFolder) {
		super(outputFolder);
	}

	private DefaultResourceResolver dr = new DefaultResourceResolver();

	@Override
	public void generate(Mirror mirror) {
		ClassMirror cm = (ClassMirror) mirror;
		String pack = "package " + cm.getFullJavaPackage() + ";\n";
		String imports = "";
		String clsName = cm.getShortId();
		String sig = "public interface " + clsName + " extends ";
		ClassMirror sm = cm.getSuper();
		while (!sm.getJavaMapping().hasInterface()) {
			sm = sm.getSuper();
		}
		String superClass = dr.getInterfaceClassName(sm);
		String superPackage = superClass.substring(0, superClass.lastIndexOf("."));
		if (!superPackage.equals(cm.getFullJavaPackage())) {
			imports += "import " + superClass + ";\n";
		}
		sig += sm.getShortId();
		imports += "\n\n";
		sig += "{\n\n\n}";
		String content = pack + imports + sig;
		writeFile(content, cm.getFullJavaPackage(), clsName);
	}

	@Override
	protected boolean sourceGenerator() {
		return true;
	}

}
