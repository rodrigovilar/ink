package org.ink.core.vm.modelinfo.internal;

import org.ink.core.vm.modelinfo.ModelInfoFactory;
import org.ink.core.vm.modelinfo.ModelInfoRepository;
import org.ink.core.vm.modelinfo.ModelInfoWriteableRepository;

public class ModelInfoFactoryImpl extends ModelInfoFactory {

	private static final ModelInfoRepositoryImpl mainRepository = new ModelInfoRepositoryImpl();

	// TODO Eli until there are transactions...
	private static final ModelInfoWriteableRepository mainWriteableRepository = mainRepository.createWriteableInstance();

	public static ModelInfoRepository getInstanceImpl() {
		return mainRepository;
	}

	public static ModelInfoWriteableRepository getWriteableInstanceImpl() {
		// TODO Eli until there are transactions...
		return mainWriteableRepository;
	}

}
