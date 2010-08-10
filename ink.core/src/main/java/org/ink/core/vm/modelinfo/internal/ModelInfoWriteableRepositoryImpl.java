package org.ink.core.vm.modelinfo.internal;

import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.modelinfo.ModelInfoWriteableRepository;

public class ModelInfoWriteableRepositoryImpl extends ModelInfoRepositoryImpl implements ModelInfoWriteableRepository {

	@Override
	public void beginWriteTransaction() {
		// TODO Auto-generated method stub
	}

	@Override
	public void commit() {
		// TODO Auto-generated method stub
	}

	@Override
	public void rollback() {
		// TODO Auto-generated method stub
	}

	@Override
	public void register(InkObject referent) {
		index.insert(referent);
	}

	@Override
	public void unregister(InkObject referent) {
		index.delete(referent);
	}
}
