package org.ink.core.vm.factory.resources;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.ink.core.vm.exceptions.CoreException;
import org.ink.core.vm.factory.DslFactory;
import org.ink.core.vm.lang.InkClassState;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.types.EnumTypeState;
import org.ink.core.vm.utils.CoreUtils;

/**
 * @author Lior Schachter
 */
public class DefaultResourceResolver extends ResourceResolver{


	private DslFactory getFactory(InkObjectState o){
		return o.reflect().getTragetOwnerFactory();
	}

	@Override
	public String getBehaviorClassName(InkClassState cls) {
		ClassMirror cm = cls.reflect();
		String result = cm.getFullJavaPackage() + "." + getBehaviorShortClassName(cls);
		return result;
	}


	@Override
	public String getInterfaceClassName(InkClassState cls) {
		ClassMirror cm = cls.reflect();
		String result = cm.getFullJavaPackage() + "." +getInterfaceClassShortName(cls);
		return result;
	}


	@Override
	public String getDataClassName(InkClassState cls) {
		ClassMirror cm = cls.reflect();
		String result = cm.getFullJavaPackage() + "." +getDataClassShortName(cls);
		return result;
	}


	@Override
	public String getStructDataClassName(InkClassState cls) {
		ClassMirror cm = cls.reflect();
		String result = cm.getFullJavaPackage() + "."+ getStructDataClassShortName(cls);
		return result;
	}


	@Override
	public String getEnumClassName(EnumTypeState enumState) {
		StringBuilder builder = new StringBuilder(100);
		String javaPack = enumState.getJavaPath();
		if(javaPack==null || javaPack.equals("")){
			builder.append(getFactory(enumState).getJavaPackage()).append(".").append(CoreUtils.getShortId(enumState.getId()));
		}else{
			builder.append(getFactory(enumState).getJavaPackage()).append(".").append(javaPack).append(".").append(CoreUtils.getShortId(enumState.getId()));
		}
		return  builder.toString();
	}

	@Override
	public boolean enableEagerFetch() {
		return false;
	}

	@Override
	public File getDslResourcesLocation(DslFactory factory) {
		URL dir  = Thread.currentThread().getContextClassLoader().getResource(factory.getDslPackage().replace('.', '/'));
		if(dir==null){
			//TODO log error
			throw new CoreException("Could not locate dsl reources location for DSL factory :" + factory.getNamespace());
		}
		File folder = new File(dir.getPath());
		return folder;
	}

	@Override
	public ClassStructure getClassDetails(InkClassState cls) {
		try {
			Class<?> javaCls = Thread.currentThread().getContextClassLoader().loadClass(getBehaviorShortClassName(cls));
			ClassStructure result = new ClassStructure();
			Class<?> sClass = javaCls.getSuperclass();
			if(sClass!=null){
				result.setSuperClass(sClass.getName());
			}
			Class<?>[] interfaces = javaCls.getInterfaces();
			if(interfaces!=null && interfaces.length>0){
				Set<String> s = new HashSet<String>(interfaces.length);
				for(Class<?> c : interfaces){
					s.add(c.getName());
				}
				result.setInterfaces(s);
			}
			Method[] methods = javaCls.getMethods();
			if(methods!=null && methods.length>0){
				Set<String> s = new HashSet<String>(interfaces.length);
				for(Method m : methods){
					s.add(m.getName());
				}
				result.setMethods(s);
			}
			return result;
		} catch (ClassNotFoundException e) {}
		return null;
	}

}