package org.ink.core.vm.factory.internal;

import java.io.File;

import org.ink.core.vm.factory.DslFactory;
import org.ink.core.vm.factory.InstantiationStrategy;
import org.ink.core.vm.lang.InkClassState;
import org.ink.core.vm.types.EnumTypeState;
import org.ink.core.vm.types.GenericEnum;
import org.ink.core.vm.utils.CoreUtils;
import org.ink.core.vm.utils.InkNotations;

/**
 * @author Lior Schachter
 */
public class CoreInstantiationStrategy implements InstantiationStrategy {


	public InkClassState findCoreClass(InkClassState cls){
		if(cls.reflect().isCoreObject()){
			return cls;
		}
		return findCoreClass((InkClassState) cls.reflect().getSuper().edit().getEditedState());
	}

	private String getCoreJavaPackage(InkClassState originalClass, InkClassState coreClass, DslFactory factory){
		if(originalClass.reflect().isCoreObject()){
			return factory.getJavaPackage();
		}
		return coreClass.getContext().getFactory().getJavaPackage();
	}

	@Override
	public String getBehaviorClassName(InkClassState cls, DslFactory factory) {
		InkClassState coreClass = findCoreClass(cls);
		String factoryPackage = getCoreJavaPackage(cls, coreClass, factory);
		StringBuilder builder = new StringBuilder(100);
		String javaPack = coreClass.getJavaPath();
		if(javaPack==null || javaPack.equals("")){
			builder.append(factoryPackage).append(".").append(CoreUtils.getShortId(coreClass.getId())).append(InkNotations.Names.BEHAVIOR_EXTENSION);
		}else{
			builder.append(factoryPackage).append(".").append(javaPack).append(".").append(CoreUtils.getShortId(coreClass.getId())).append(InkNotations.Names.BEHAVIOR_EXTENSION);
		}
		return  builder.toString();
	}

	@Override
	public String getInterfaceClassName(InkClassState cls, DslFactory factory) {
		InkClassState coreClass = findCoreClass(cls);
		String factoryPackage = getCoreJavaPackage(cls, coreClass, factory);
		StringBuilder builder = new StringBuilder(100);
		String javaPack = coreClass.getJavaPath();
		if(javaPack==null || javaPack.equals("")){
			builder.append(factoryPackage).append(".").append(CoreUtils.getShortId(coreClass.getId()));
		}else{
			builder.append(factoryPackage).append(".").append(javaPack).append(".").append(CoreUtils.getShortId(coreClass.getId()));
		}
		return  builder.toString();
	}

	@Override
	public String getDataClassName(InkClassState cls, DslFactory factory) {
		InkClassState coreClass = findCoreClass(cls);
		String factoryPackage = getCoreJavaPackage(cls, coreClass, factory);
		StringBuilder builder = new StringBuilder(100);
		String javaPack = coreClass.getJavaPath();
		if(javaPack==null || javaPack.equals("")){
			builder.append(factoryPackage).append(".").append(CoreUtils.getShortId(coreClass.getId())).append(InkNotations.Names.DATA_CLASS_EXTENSION);
		}else{
			builder.append(factoryPackage).append(".").append(javaPack).append(".").append(CoreUtils.getShortId(coreClass.getId())).append(InkNotations.Names.DATA_CLASS_EXTENSION);
		}
		return  builder.toString();
	}

	@Override
	public String getStructDataClassName(InkClassState cls, DslFactory factory) {
		InkClassState coreClass = findCoreClass(cls);
		String factoryPackage = getCoreJavaPackage(cls, coreClass, factory);
		StringBuilder builder = new StringBuilder(100);
		String javaPack = coreClass.getJavaPath();
		if(javaPack==null || javaPack.equals("")){
			builder.append(factoryPackage).append(".").append(CoreUtils.getShortId(coreClass.getId())).append(InkNotations.Names.STRUCT_CLASS_EXTENSION);
		}else{
			builder.append(factoryPackage).append(".").append(javaPack).append(".").append(CoreUtils.getShortId(coreClass.getId())).append(InkNotations.Names.STRUCT_CLASS_EXTENSION);
		}
		return  builder.toString();
	}

	@Override
	public String getEnumClassName(EnumTypeState enumState, DslFactory factory) {
		if(enumState.reflect().isCoreObject()){
			StringBuilder builder = new StringBuilder(100);
			String javaPack = enumState.getJavaPath();
			if(javaPack==null || javaPack.equals("")){
				builder.append(factory.getJavaPackage()).append(".").append(CoreUtils.getShortId(enumState.getId()));
			}else{
				builder.append(factory.getJavaPackage()).append(".").append(javaPack).append(".").append(CoreUtils.getShortId(enumState.getId()));
			}
			return  builder.toString();
		}else{
			return GenericEnum.class.getName();
		}
	}

	@Override
	public boolean enableEagerFetch() {
		return true;
	}

	@Override
	public File getDslResourcesLocation(DslFactory factory) {
		File dslConfFile = factory.getConfigurationFile();
		File result = new File(dslConfFile.getParentFile().getAbsolutePath() +
				File.separatorChar + "src" + File.separatorChar +"main" + File.separatorChar +"dsl"
				+ File.separatorChar +factory.getDslPackage().replace('.', File.separatorChar));
		return result;
	}

}