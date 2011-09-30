package org.ink.core.vm.modelinfo;

import java.util.Collection;

import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.modelinfo.relations.ModelRelation;

public interface ModelInfoRepository {

	public Collection<Mirror> findReferrers(Mirror referent, ModelRelation relation, boolean isRecursive); // inner

	public Collection<Mirror> findReferrers(Mirror referent, ModelRelation relation, boolean isRecursive, String... namespaces);

	public void beginReadTransaction();

	public void endReadTransaction();

	public void reload();

	public void destroy();

}
