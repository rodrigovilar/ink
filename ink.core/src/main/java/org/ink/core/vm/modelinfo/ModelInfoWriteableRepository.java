package org.ink.core.vm.modelinfo;

import org.ink.core.vm.lang.InkObject;

public interface ModelInfoWriteableRepository extends ModelInfoRepository {

	public void register(InkObject referent);

	public void unregister(InkObject referent);

	public void beginWriteTransaction();

	public void commit();

	public void rollback();

}
