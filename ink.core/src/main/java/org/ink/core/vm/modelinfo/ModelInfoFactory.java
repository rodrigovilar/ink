package org.ink.core.vm.modelinfo;

import org.ink.core.vm.modelinfo.internal.ModelInfoFactoryImpl;

public abstract class ModelInfoFactory {

	public static ModelInfoRepository getInstance() {
		return ModelInfoFactoryImpl.getInstanceImpl();
	}

	public static ModelInfoWriteableRepository getWriteableInstance() {
		return ModelInfoFactoryImpl.getWriteableInstanceImpl();
	}
}