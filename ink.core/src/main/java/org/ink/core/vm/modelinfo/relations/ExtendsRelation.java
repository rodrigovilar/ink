package org.ink.core.vm.modelinfo.relations;

import java.util.HashSet;
import java.util.Set;

import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.mirror.Mirror;

public class ExtendsRelation implements ModelRelation {

	private static ExtendsRelation instance = new ExtendsRelation();

	private ExtendsRelation() {
	}

	public static ModelRelation getInstance() {
		return instance;
	}

	@Override
	public Set<InkObject> findReferents(InkObject obj) {
		HashSet<InkObject> result = new HashSet<InkObject>(1);
		Mirror superMirror = obj.reflect().getSuper();
		if (superMirror != null) {
			result.add(superMirror.getTargetBehavior());
		}
		return result;
	}
}
