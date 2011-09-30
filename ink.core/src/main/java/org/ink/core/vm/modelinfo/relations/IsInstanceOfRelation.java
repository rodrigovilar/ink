package org.ink.core.vm.modelinfo.relations;

import java.util.HashSet;
import java.util.Set;

import org.ink.core.vm.mirror.Mirror;

public class IsInstanceOfRelation implements ModelRelation {

	private static IsInstanceOfRelation instance = new IsInstanceOfRelation();

	private IsInstanceOfRelation() {
	}

	public static ModelRelation getInstance() {
		return instance;
	}

	@Override
	public Set<Mirror> findReferents(Mirror obj) {
		HashSet<Mirror> result = new HashSet<Mirror>(1);
		result.add(obj.getClassMirror());
		return result;
	}
}
