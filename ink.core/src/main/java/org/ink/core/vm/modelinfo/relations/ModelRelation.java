package org.ink.core.vm.modelinfo.relations;

import java.util.Set;

import org.ink.core.vm.lang.InkObject;

public interface ModelRelation {

	Set<InkObject> findReferents(InkObject obj);

}
