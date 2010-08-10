package org.ink.core.vm.modelinfo.internal;

import java.util.Collection;

import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.modelinfo.ModelInfoRepository;
import org.ink.core.vm.modelinfo.ModelInfoWriteableRepository;
import org.ink.core.vm.modelinfo.relations.ModelRelation;

public class ModelInfoRepositoryImpl implements ModelInfoRepository {

	protected ModelIndex index;

	public void init() {
		index = ModelIndex.initIndex();
	}

	ModelInfoRepositoryImpl() {
		init();
	}

	@Override
	public void beginReadTransaction() {
		// TODO Auto-generated method stub

	}

	@Override
	public void endReadTransaction() {
		// TODO Auto-generated method stub

	}

	@Override
	public Collection<InkObject> findReferrers(InkObject referent, ModelRelation relation) {
		return index.findReferrers(referent, relation);
	}

	ModelInfoWriteableRepository createWriteableInstance() {
		return new ModelInfoWriteableRepositoryImpl();
	}

}
