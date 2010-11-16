package org.ink.core.vm.modelinfo;

import java.util.Collection;

import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.modelinfo.relations.ModelRelation;

public interface ModelInfoRepository {

	public Collection<InkObject> findReferrers(InkObject referent, ModelRelation relation); // recursive, inner, dsl factories

	public Collection<InkObject> findReferrers(InkObject referent, ModelRelation relation, String... namespaces);

	public void beginReadTransaction();

	public void endReadTransaction();

}
