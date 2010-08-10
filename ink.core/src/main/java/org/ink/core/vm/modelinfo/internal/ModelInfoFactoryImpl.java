package org.ink.core.vm.modelinfo.internal;

import org.ink.core.vm.modelinfo.ModelInfoFactory;
import org.ink.core.vm.modelinfo.ModelInfoRepository;
import org.ink.core.vm.modelinfo.ModelInfoWriteableRepository;

public class ModelInfoFactoryImpl extends ModelInfoFactory {

	private static final ModelInfoRepositoryImpl mainRepository = new ModelInfoRepositoryImpl();

	public static ModelInfoRepository getInstanceImpl() {
		return mainRepository;
	}

	public static ModelInfoWriteableRepository getWriteableInstanceImpl() {
		return mainRepository.createWriteableInstance();
	}

}
