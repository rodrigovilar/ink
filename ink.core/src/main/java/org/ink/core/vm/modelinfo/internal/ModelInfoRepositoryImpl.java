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
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.modelinfo.ModelInfoRepository;
import org.ink.core.vm.modelinfo.ModelInfoWriteableRepository;
import org.ink.core.vm.modelinfo.relations.ExtendsRelation;
import org.ink.core.vm.modelinfo.relations.ModelRelation;

public class ModelInfoRepositoryImpl implements ModelInfoRepository {

	private static final String[] EMPTY_STRING_ARRAY = {};

	protected Map<String, ModelIndex> indices;

	public void init() {
		indices = new HashMap<String, ModelIndex>();
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
	public Collection<InkObject> findReferrers(InkObject referent, ModelRelation relation, boolean isRecursive) {
		return findReferrers(referent, relation, isRecursive, getAllNamespaces());
	}

	@Override
	public Collection<InkObject> findReferrers(InkObject referent, ModelRelation relation, boolean isRecursive, String... namespaces) {
		Collection<InkObject> result;
		if (namespaces != null && namespaces.length > 0) {
			result = new HashSet<InkObject>();
			if (isRecursive) {
				Stack<InkObject> processingStack = new Stack<InkObject>();
				Set<InkObject> processed = new HashSet<InkObject>();
				processingStack.push(referent);
				String[] allNamespaces = getAllNamespaces();
				ModelRelation extendsInstance = ExtendsRelation.getInstance();
				while (!processingStack.isEmpty()) {
					InkObject currentReferent = processingStack.pop();
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
						Set<InkObject> referrers = index.findReferrers(referent, relation);
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

	ModelInfoWriteableRepository createWriteableInstance() {
		return new ModelInfoWriteableRepositoryImpl(indices);
	}

	private String[] getAllNamespaces() {
		return InkVM.instance().getFactory().getScope().toArray(EMPTY_STRING_ARRAY);
	}
}
