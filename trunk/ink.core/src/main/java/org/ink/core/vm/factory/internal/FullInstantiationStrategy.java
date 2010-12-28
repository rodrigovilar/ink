package org.ink.core.vm.factory.internal;

import java.io.File;
import java.net.URL;

import org.ink.core.vm.exceptions.CoreException;
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


	@Override
	public String getBehaviorClassName(InkClassState cls, DslFactory factory) {
		StringBuilder builder = new StringBuilder(100);
		String javaPack = cls.getJavaPath();
		if(javaPack==null || javaPack.equals("")){
			builder.append(factory.getJavaPackage()).append(".").append(getBehaviorClassName(cls));
		}else{
			builder.append(factory.getJavaPackage()).append(".").append(javaPack).append(".").append(getBehaviorClassName(cls));
		}
		return  builder.toString();
	}

	private String getBehaviorClassName(InkClassState cls){
		return cls.reflect().getShortId() + InkNotations.Names.BEHAVIOR_EXTENSION;
	}

	@Override
	public String getInterfaceClassName(InkClassState cls, DslFactory factory) {
		StringBuilder builder = new StringBuilder(100);
		String javaPack = cls.getJavaPath();
		if(javaPack==null || javaPack.equals("")){
			builder.append(factory.getJavaPackage()).append(".").append(getInterfaceClassName(cls));
		}else{
			builder.append(factory.getJavaPackage()).append(".").append(javaPack).append(".").append(getInterfaceClassName(cls));
		}
		return  builder.toString();
	}

	private String getInterfaceClassName(InkClassState cls){
		return cls.reflect().getShortId();
	}


	@Override
	public String getDataClassName(InkClassState cls, DslFactory factory) {
		StringBuilder builder = new StringBuilder(100);
		String javaPack = cls.getJavaPath();
		if(javaPack==null || javaPack.equals("")){
			builder.append(factory.getJavaPackage()).append(".").append(getDataClassName(cls));
		}else{
			builder.append(factory.getJavaPackage()).append(".").append(javaPack).append(".").append(getDataClassName(cls));
		}
		return  builder.toString();
	}

	private String getDataClassName(InkClassState cls){
		return cls.reflect().getShortId() + InkNotations.Names.DATA_CLASS_EXTENSION;
	}


	@Override
	public String getStructDataClassName(InkClassState cls, DslFactory factory) {
		StringBuilder builder = new StringBuilder(100);
		String javaPack = cls.getJavaPath();
		if(javaPack==null || javaPack.equals("")){
			builder.append(factory.getJavaPackage()).append(".").append(getStructDataClassName(cls));
		}else{
			builder.append(factory.getJavaPackage()).append(".").append(cls.getJavaPath()).append(".").append(getStructDataClassName(cls));
		}
		return  builder.toString();
	}

	private String getStructDataClassName(InkClassState cls){
		return cls.reflect().getShortId() + InkNotations.Names.STRUCT_CLASS_EXTENSION;
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

}