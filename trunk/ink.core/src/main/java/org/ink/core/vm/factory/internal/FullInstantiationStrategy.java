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
public class FullInstantiationStrategy implements InstantiationStrategy {

	private static FullInstantiationStrategy instance = new FullInstantiationStrategy();

	public static InstantiationStrategy getInstance(){
		return instance;
	}
	
	@Override
	public String getBehaviorClassName(InkClassState cls, DslFactory factory) {
		StringBuilder builder = new StringBuilder(100);
		String javaPack = cls.getJavaPath();
		if(javaPack==null || javaPack.equals("")){
			builder.append(factory.getJavaPackage()).append(".").append(CoreUtils.getShortId(cls.getId())).append(InkNotations.Names.BEHAVIOR_EXTENSION);
		}else{
			builder.append(factory.getJavaPackage()).append(".").append(javaPack).append(".").append(CoreUtils.getShortId(cls.getId())).append(InkNotations.Names.BEHAVIOR_EXTENSION);
		}
		return  builder.toString();
	}
	
	@Override
	public String getInterfaceClassName(InkClassState cls, DslFactory factory) {
		StringBuilder builder = new StringBuilder(100);
		String javaPack = cls.getJavaPath();
		if(javaPack==null || javaPack.equals("")){
			builder.append(factory.getJavaPackage()).append(".").append(CoreUtils.getShortId(cls.getId()));
		}else{
			builder.append(factory.getJavaPackage()).append(".").append(javaPack).append(".").append(CoreUtils.getShortId(cls.getId()));
		}
		return  builder.toString();
	}

	@Override
	public String getDataClassName(InkClassState cls, DslFactory factory) {
		StringBuilder builder = new StringBuilder(100);
		String javaPack = cls.getJavaPath();
		if(javaPack==null || javaPack.equals("")){
			builder.append(factory.getJavaPackage()).append(".").append(CoreUtils.getShortId(cls.getId())).append(InkNotations.Names.DATA_CLASS_EXTENSION);
		}else{
			builder.append(factory.getJavaPackage()).append(".").append(javaPack).append(".").append(CoreUtils.getShortId(cls.getId())).append(InkNotations.Names.DATA_CLASS_EXTENSION);
		}
		return  builder.toString();
	}
	
	@Override
	public String getStructDataClassName(InkClassState cls, DslFactory factory) {
		StringBuilder builder = new StringBuilder(100);
		String javaPack = cls.getJavaPath();
		if(javaPack==null || javaPack.equals("")){
			builder.append(factory.getJavaPackage()).append(".").append(CoreUtils.getShortId(cls.getId())).append(InkNotations.Names.STRUCT_CLASS_EXTENSION);
		}else{
			builder.append(factory.getJavaPackage()).append(".").append(cls.getJavaPath()).append(".").append(CoreUtils.getShortId(cls.getId())).append(InkNotations.Names.STRUCT_CLASS_EXTENSION);
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