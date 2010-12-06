package org.ink.core.vm.modelinfo;

import java.util.Collection;

import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.modelinfo.relations.ModelRelation;

public interface ModelInfoRepository {

	public Collection<InkObject> findReferrers(InkObject referent, ModelRelation relation, boolean isRecursive); // inner

	public Collection<InkObject> findReferrers(InkObject referent, ModelRelation relation, boolean isRecursive, String... namespaces);

	public void beginReadTransaction();

	public void endReadTransaction();

	public void reload();

}
