package org.ink.core.vm.factory.internal;

import org.ink.core.vm.factory.DslFactory;
import org.ink.core.vm.factory.InstantiationStrategy;
import org.ink.core.vm.lang.InkClassState;
import org.ink.core.vm.types.EnumTypeState;
import org.ink.core.vm.utils.CoreUtils;
import org.ink.core.vm.utils.InkNotations;

/**
 * @author Lior Schachter
 */
public class CoreInstantiationStrategy implements InstantiationStrategy {

	private static CoreInstantiationStrategy instance = new CoreInstantiationStrategy();

	public static InstantiationStrategy getInstance(){
		return instance;
	}
	
	public InkClassState findCoreClass(InkClassState cls){
		if(cls.reflect().isCoreObject()){
			return cls;
		}
		return findCoreClass((InkClassState) cls.reflect().getSuper().getTargetBehavior());
	}
	
	@Override
	public String getBehaviorClassName(InkClassState cls, DslFactory factory) {
		InkClassState coreClass = findCoreClass(cls);
		StringBuilder builder = new StringBuilder(100);
		String javaPack = coreClass.getJavaPath();
		if(javaPack==null || javaPack.equals("")){
			builder.append(factory.getJavaPackage()).append(".").append(CoreUtils.getShortId(coreClass.getId())).append(InkNotations.Names.BEHAVIOR_EXTENSION);
		}else{
			builder.append(factory.getJavaPackage()).append(".").append(javaPack).append(".").append(CoreUtils.getShortId(coreClass.getId())).append(InkNotations.Names.BEHAVIOR_EXTENSION);
		}
		return  builder.toString();
	}
	
	@Override
	public String getInterfaceClassName(InkClassState cls, DslFactory factory) {
		InkClassState coreClass = findCoreClass(cls);
		StringBuilder builder = new StringBuilder(100);
		String javaPack = coreClass.getJavaPath();
		if(javaPack==null || javaPack.equals("")){
			builder.append(factory.getJavaPackage()).append(".").append(CoreUtils.getShortId(coreClass.getId()));
		}else{
			builder.append(factory.getJavaPackage()).append(".").append(javaPack).append(".").append(CoreUtils.getShortId(coreClass.getId()));
		}
		return  builder.toString();
	}

	@Override
	public String getDataClassName(InkClassState cls, DslFactory factory) {
		InkClassState coreClass = findCoreClass(cls);
		StringBuilder builder = new StringBuilder(100);
		String javaPack = coreClass.getJavaPath();
		if(javaPack==null || javaPack.equals("")){
			builder.append(factory.getJavaPackage()).append(".").append(CoreUtils.getShortId(coreClass.getId())).append(InkNotations.Names.DATA_CLASS_EXTENSION);
		}else{
			builder.append(factory.getJavaPackage()).append(".").append(javaPack).append(".").append(CoreUtils.getShortId(coreClass.getId())).append(InkNotations.Names.DATA_CLASS_EXTENSION);
		}
		return  builder.toString();
	}
	
	@Override
	public String getStructDataClassName(InkClassState cls, DslFactory factory) {
		InkClassState coreClass = findCoreClass(cls);
		StringBuilder builder = new StringBuilder(100);
		String javaPack = coreClass.getJavaPath();
		if(javaPack==null || javaPack.equals("")){
			builder.append(factory.getJavaPackage()).append(".").append(CoreUtils.getShortId(coreClass.getId())).append(InkNotations.Names.STRUCT_CLASS_EXTENSION);
		}else{
			builder.append(factory.getJavaPackage()).append(".").append(cls.getJavaPath()).append(".").append(CoreUtils.getShortId(coreClass.getId())).append(InkNotations.Names.STRUCT_CLASS_EXTENSION);
		}
		return  builder.toString();
	}

	@Override
	public String getEnumClassName(EnumTypeState enumState, DslFactory factory) {
		StringBuilder builder = new StringBuilder(100);
		String javaPack = enumState.getJavaPath();
		if(javaPack==null || javaPack.equals("")){
			builder.append(factory.getJavaPackage()).append(".").append(CoreUtils.getShortId(enumState.getId()));
		}else{
			builder.append(factory.getJavaPackage()).append(".").append(javaPack).append(".").append(CoreUtils.getShortId(enumState.getId()));
		}
		return  builder.toString();
	}
	
	
	
}