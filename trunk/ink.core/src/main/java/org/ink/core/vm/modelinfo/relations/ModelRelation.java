package org.ink.core.vm.modelinfo.relations;

import java.util.Set;

import org.ink.core.vm.mirror.Mirror;

public interface ModelRelation {

	Set<Mirror> findReferents(Mirror obj);

}
