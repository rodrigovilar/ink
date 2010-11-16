package org.ink.core.vm.modelinfo.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.ink.core.vm.lang.DataTypeMarker;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.property.mirror.CollectionPropertyMirror;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.modelinfo.relations.ExtendsRelation;
import org.ink.core.vm.modelinfo.relations.IsInstanceOfRelation;
import org.ink.core.vm.modelinfo.relations.ModelRelation;
import org.ink.core.vm.proxy.Proxiability;
import org.ink.core.vm.proxy.Proxiability.Kind;
import org.ink.core.vm.types.CollectionTypeMarker;
import org.ink.core.vm.utils.property.mirror.ListPropertyMirror;
import org.ink.core.vm.utils.property.mirror.MapPropertyMirror;

class ModelIndex {

	private static final ModelRelation[] relations;

	private final Map<InkObject, Map<ModelRelation, Set<InkObject>>> referentToReferrers;

	private final Map<InkObject, Map<ModelRelation, Set<InkObject>>> referrerToReferents;

	static {
		relations = findRelations();
	}

	private ModelIndex() {
		referentToReferrers = new HashMap<InkObject, Map<ModelRelation, Set<InkObject>>>();
		referrerToReferents = new HashMap<InkObject, Map<ModelRelation, Set<InkObject>>>();
	}

	static ModelIndex initIndex(String namespace, ModelInfoRepositoryImpl repository) {
		ModelIndex result = new ModelIndex();
		return result;
	}

	void insert(InkObject referrer) {
		for (ModelRelation relation : relations) {
			Set<InkObject> referentsForReferrerAndRelation = recursiveFindReferents(relation, referrer);
			Map<ModelRelation, Set<InkObject>> referentsForReferrer = referrerToReferents.get(referrer);
			if (referentsForReferrer == null) {
				referentsForReferrer = new HashMap<ModelRelation, Set<InkObject>>();
				referrerToReferents.put(referrer, referentsForReferrer);
			}
			referentsForReferrer.put(relation, referentsForReferrerAndRelation);
			for (InkObject referent : referentsForReferrerAndRelation) {
				Map<ModelRelation, Set<InkObject>> referrersForReferent = referentToReferrers.get(referent);
				if (referrersForReferent == null) {
					referrersForReferent = new HashMap<ModelRelation, Set<InkObject>>();
					referentToReferrers.put(referent, referrersForReferent);
				}
				Set<InkObject> referrersForReferentAndRelation = referrersForReferent.get(relation);
				if (referrersForReferentAndRelation == null) {
					referrersForReferentAndRelation = new HashSet<InkObject>();
					referrersForReferent.put(relation, referrersForReferentAndRelation);
				}
				referrersForReferentAndRelation.add(referrer);
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected Set<InkObject> recursiveFindReferents(ModelRelation relation, InkObject referrer) {
		if (referrer.isProxied()) {
			Kind proxyKind = ((Proxiability) referrer).getProxyKind();
			if (proxyKind == Kind.BEHAVIOR_OWNER || proxyKind == Kind.BEHAVIOR_BOTH) {
				return Collections.emptySet();
			}
		}
		Set<InkObject> result = relation.findReferents(referrer);
		Mirror referrerMirror = referrer.reflect();
		PropertyMirror[] propertiesMirrors = referrerMirror.getPropertiesMirrors();
		for (PropertyMirror propertyMirror : propertiesMirrors) {
			Object value = referrerMirror.getPropertyValue(propertyMirror.getIndex());
			if (value != null && !propertyMirror.isComputed()) {
				switch (propertyMirror.getTypeMarker()) {
				case Class:
					//TODO Eli - handle structs
					if (value instanceof InkObject) {
						result.addAll(recursiveFindReferents(relation, (InkObject) value));
					}
					break;
				case Collection:
					CollectionTypeMarker collectionTypeMarker = ((CollectionPropertyMirror) propertyMirror).getCollectionTypeMarker();
					switch (collectionTypeMarker) {
					case List:
						PropertyMirror innerPropertyMirror = ((ListPropertyMirror) propertyMirror).getItemMirror();
						switch (innerPropertyMirror.getTypeMarker()) {
						case Class:
							for (InkObject listItem : (List<InkObject>) value) {
								result.addAll(recursiveFindReferents(relation, listItem));
							}
							break;
						case Enum:
							// ???
							break;
						}
						break;
					case Map:
						boolean shouldIndexKey = ((MapPropertyMirror) propertyMirror).getKeyMirror().getTypeMarker() == DataTypeMarker.Class;
						boolean shouldIndexValue = ((MapPropertyMirror) propertyMirror).getValueMirror().getTypeMarker() == DataTypeMarker.Class;
						// TODO Eli Enums???
						if (shouldIndexKey || shouldIndexValue) {
							for (Map.Entry<Object, Object> entry : ((Map<Object, Object>) value).entrySet()) {
								if (shouldIndexKey) {
									result.addAll(recursiveFindReferents(relation, (InkObject) entry.getKey()));
								}
								if (shouldIndexValue) {
									result.addAll(recursiveFindReferents(relation, (InkObject) entry.getValue()));
								}
							}
						}
						break;
					}
					break;
				case Enum:
					// ???
					break;
				}
			}
		}
		return result;
	}

	void update(InkObject referrer) {
		delete(referrer);
		insert(referrer);
	}

	void insertOrUpdate(InkObject referrer) {
		if (referrerToReferents.containsKey(referrer)) {
			update(referrer);
		} else {
			insert(referrer);
		}
	}

	void delete(InkObject referrer) {
		Map<ModelRelation, Set<InkObject>> referentsForReferrer = referrerToReferents.get(referrer);
		for (Entry<ModelRelation, Set<InkObject>> entry : referentsForReferrer.entrySet()) {
			ModelRelation relation = entry.getKey();
			Set<InkObject> referentsForReferrerAndRelation = entry.getValue();
			for (InkObject referent : referentsForReferrerAndRelation) {
				Map<ModelRelation, Set<InkObject>> referrersForReferent = referentToReferrers.get(referent);
				Set<InkObject> referrersForReferentAndRelation = referrersForReferent.get(relation);
				referrersForReferentAndRelation.remove(referrer);
				if (referrersForReferentAndRelation.isEmpty()) {
					referrersForReferent.remove(relation);
				}
				if (referrersForReferent.isEmpty()) {
					referentToReferrers.remove(referent);
				}
			}
		}
		referrerToReferents.remove(referrer);
	}

	Set<InkObject> findReferrers(InkObject referent, ModelRelation relation) {
		Set<InkObject> result = null;
		Map<ModelRelation, Set<InkObject>> referrersForObject = referentToReferrers.get(referent);
		if (referrersForObject != null) {
			result = referrersForObject.get(relation);
		}
		return result;
	}

	private static ModelRelation[] findRelations() {
		return new ModelRelation[] { ExtendsRelation.getInstance(), IsInstanceOfRelation.getInstance() };
	}
}