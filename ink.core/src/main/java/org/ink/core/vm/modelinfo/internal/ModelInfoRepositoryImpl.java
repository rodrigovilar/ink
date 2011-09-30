package org.ink.core.vm.modelinfo.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.ink.core.vm.factory.InkVM;
import org.ink.core.vm.factory.VMMain;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.modelinfo.ModelInfoRepository;
import org.ink.core.vm.modelinfo.ModelInfoWriteableRepository;
import org.ink.core.vm.modelinfo.relations.ExtendsRelation;
import org.ink.core.vm.modelinfo.relations.ModelRelation;

public class ModelInfoRepositoryImpl implements ModelInfoRepository {

	private static final String[] EMPTY_STRING_ARRAY = {};

	protected Map<String, ModelIndex> indices = new HashMap<String, ModelIndex>();

	private void init() {
		indices.clear();
		Set<String> scope = VMMain.getDsls();
		for (String namespace : scope) {
			ModelIndex newIndex = ModelIndex.initIndex(namespace, this);
			indices.put(namespace, newIndex);
		}
	}

	ModelInfoRepositoryImpl() {
		init();
	}

	@Override
	public void beginReadTransaction() {
		// TODO Auto-generated method stub

	}

	@Override
	public void endReadTransaction() {
		// TODO Auto-generated method stub

	}

	@Override
	public Collection<Mirror> findReferrers(Mirror referent, ModelRelation relation, boolean isRecursive) {
		return findReferrers(referent, relation, isRecursive, getAllNamespaces());
	}

	@Override
	public Collection<Mirror> findReferrers(Mirror referent, ModelRelation relation, boolean isRecursive, String... namespaces) {
		Collection<Mirror> result;
		if (namespaces != null && namespaces.length > 0) {
			result = new HashSet<Mirror>();
			if (isRecursive) {
				Stack<Mirror> processingStack = new Stack<Mirror>();
				Set<Mirror> processed = new HashSet<Mirror>();
				processingStack.push(referent);
				String[] allNamespaces = getAllNamespaces();
				ModelRelation extendsInstance = ExtendsRelation.getInstance();
				while (!processingStack.isEmpty()) {
					Mirror currentReferent = processingStack.pop();
					if (!processed.contains(currentReferent)) {
						processingStack.addAll(findReferrers(currentReferent, extendsInstance, false, allNamespaces));
						result.addAll(findReferrers(currentReferent, relation, false, namespaces));
						processed.add(currentReferent);
					}
				}
			} else {
				for (String namespace : namespaces) {
					ModelIndex index = indices.get(namespace);
					if (index != null) {
						Set<Mirror> referrers = index.findReferrers(referent, relation);
						if (referrers != null) {
							result.addAll(referrers);
						}
					}
					// TODO Eli else?
				}
			}
		} else {
			result = Collections.emptySet();
		}
		return result;
	}

	@Override
	public void reload() {
		init();
	}

	@Override
	public void destroy() {
		indices.clear();
	}

	ModelInfoWriteableRepository createWriteableInstance() {
		return new ModelInfoWriteableRepositoryImpl(indices);
	}

	private String[] getAllNamespaces() {
		return InkVM.instance().getFactory().getScope().toArray(EMPTY_STRING_ARRAY);
	}
}
