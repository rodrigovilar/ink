package org.ink.core.vm.modelinfo.relations;

import java.util.HashSet;
import java.util.Set;

import org.ink.core.vm.lang.InkObject;

public class IsInstanceOfRelation implements ModelRelation {

	private static IsInstanceOfRelation instance = new IsInstanceOfRelation();

	private IsInstanceOfRelation() {
	}

	public static ModelRelation getInstance() {
		return instance;
	}

	@Override
	public Set<InkObject> findReferents(InkObject obj) {
		HashSet<InkObject> result = new HashSet<InkObject>(1);
		result.add(obj.reflect().getClassMirror().getTargetBehavior());
		return result;
	}
}
