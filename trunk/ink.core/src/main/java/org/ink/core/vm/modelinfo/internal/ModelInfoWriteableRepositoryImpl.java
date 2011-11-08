package org.ink.core.vm.modelinfo.internal;

import java.util.Map;

import org.ink.core.vm.mirror.Mirror;
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
	public void register(Mirror referent) {
		ModelIndex index = indices.get(referent.getNamespace());
		if (index != null) {
			index.insert(referent);
		}
		// TODO Eli else?
	}

	@Override
	public void reset(String namespace) {
		ModelIndex newIndex = ModelIndex.initIndex(namespace, this);
		indices.put(namespace, newIndex);
	}

	@Override
	public void introduceNewDsl(String namespace) {
		ModelIndex newIndex = ModelIndex.initIndex(namespace, this);
		indices.put(namespace, newIndex);
	}

	@Override
	public void unregister(Mirror referent) {
		ModelIndex index = indices.get(referent.getNamespace());
		if (index != null) {
			index.delete(referent);
		}
		// TODO Eli else?
	}
}
