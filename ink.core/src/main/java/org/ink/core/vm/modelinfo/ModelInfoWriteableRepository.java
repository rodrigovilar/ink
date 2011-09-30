package org.ink.core.vm.modelinfo;

import org.ink.core.vm.mirror.Mirror;


public interface ModelInfoWriteableRepository extends ModelInfoRepository {

	public void register(Mirror referent);

	public void unregister(Mirror referent);

	public void beginWriteTransaction();

	public void commit();

	public void rollback();

}
