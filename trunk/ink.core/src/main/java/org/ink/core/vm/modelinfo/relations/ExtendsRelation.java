package org.ink.core.vm.modelinfo.relations;

import java.util.HashSet;
import java.util.Set;

import org.ink.core.vm.mirror.Mirror;

public class ExtendsRelation implements ModelRelation {

	private static ExtendsRelation instance = new ExtendsRelation();

	private ExtendsRelation() {
	}

	public static ModelRelation getInstance() {
		return instance;
	}

	@Override
	public Set<Mirror> findReferents(Mirror obj) {
		HashSet<Mirror> result = new HashSet<Mirror>(1);
		Mirror superMirror = obj.getSuper();
		if (superMirror != null) {
			result.add(superMirror);
		}
		return result;
	}
}
