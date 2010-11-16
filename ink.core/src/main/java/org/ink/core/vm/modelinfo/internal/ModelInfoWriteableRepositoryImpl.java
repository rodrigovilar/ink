package org.ink.core.vm.modelinfo.internal;

import java.util.Map;

import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.modelinfo.ModelInfoWriteableRepository;

public class ModelInfoWriteableRepositoryImpl extends ModelInfoRepositoryImpl implements ModelInfoWriteableRepository {

	public ModelInfoWriteableRepositoryImpl(Map<String, ModelIndex> indices) {
		this.indices = indices;
	}

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
		ModelIndex index = indices.get(referent.reflect().getNamespace());
		if (index != null) {
			index.insert(referent);
		}
		// TODO Eli else?
	}

	@Override
	public void unregister(InkObject referent) {
		ModelIndex index = indices.get(referent.reflect().getNamespace());
		if (index != null) {
			index.delete(referent);
		}
		// TODO Eli else?
	}
}
