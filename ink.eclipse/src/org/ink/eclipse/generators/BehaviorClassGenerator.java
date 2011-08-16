package org.ink.eclipse.generators;

import org.eclipse.core.resources.IFolder;
import org.ink.core.vm.factory.resources.DefaultResourceResolver;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.mirror.Mirror;

public class BehaviorClassGenerator extends BaseGenerator{

	boolean hasInterface;

	public BehaviorClassGenerator(IFolder outputFolder, boolean hasInterface) {
		super(outputFolder);
		this.hasInterface = hasInterface;
	}

	private DefaultResourceResolver dr = new DefaultResourceResolver();

	@Override
	public void generate(Mirror mirror) {
		ClassMirror cm = (ClassMirror)mirror;
		String pack = "package " + cm.getFullJavaPackage() +";\n";
		String imports = "";
		String clsName = dr.getBehaviorShortClassName(cm);
		String sig = "public class " + clsName +"<S extends "+dr.getStateClassShortName(cm)+"> extends ";
		ClassMirror sm = cm.getSuper();
		while(!sm.getJavaMapping().hasBehavior()){
			sm = sm.getSuper();
		}
		String superClass = dr.getBehaviorClassName(sm);
		String superPackage = superClass.substring(0, superClass.lastIndexOf("."));
		if(!superPackage.equals(cm.getFullJavaPackage())) {
			imports+="import " + superClass+";\n";
		}
		sig+=dr.getBehaviorShortClassName(sm) + "<S> implements ";
		if(hasInterface){
			sig+=dr.getInterfaceClassShortName(cm);
		}else{
			sm = cm.getSuper();
			while(!sm.getJavaMapping().hasInterface()){
				sm = sm.getSuper();
			}
			superClass = dr.getInterfaceClassName(sm);
			String interfacePackage = superClass.substring(0, superClass.lastIndexOf("."));
			if(!interfacePackage.equals(cm.getFullJavaPackage())) {
				imports+="import " + superClass+";\n";
			}
			sig+=dr.getInterfaceClassShortName(sm);
		}
		imports+="\n\n";
		sig+="{\n\n\n}";
		String content = pack + imports + sig;
		writeFile(content, cm.getFullJavaPackage(), clsName);
	}

	@Override
	protected boolean sourceGenerator() {
		return true;
	}

}
