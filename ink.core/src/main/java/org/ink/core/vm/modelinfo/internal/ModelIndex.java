package org.ink.core.vm.modelinfo.internal;

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
import org.ink.core.vm.proxy.Proxiable;
import org.ink.core.vm.types.CollectionTypeMarker;
import org.ink.core.vm.utils.property.mirror.ListPropertyMirror;
import org.ink.core.vm.utils.property.mirror.MapPropertyMirror;

class ModelIndex {

	private static final ModelRelation[] relations;

	/**
	 * The values of this data structure can be inner objects.
	 */
	private final Map<Mirror, Map<ModelRelation, Set<Mirror>>> referentToReferrers;

	/**
	 * The values of this data structure are top-level objects only.
	 */
	private final Map<Mirror, Map<ModelRelation, Set<Mirror>>> referrerToReferents;

	static {
		relations = findRelations();
	}

	private ModelIndex() {
		referentToReferrers = new HashMap<Mirror, Map<ModelRelation, Set<Mirror>>>();
		referrerToReferents = new HashMap<Mirror, Map<ModelRelation, Set<Mirror>>>();
	}

	static ModelIndex initIndex(String namespace, ModelInfoRepositoryImpl repository) {
		ModelIndex result = new ModelIndex();
		return result;
	}

	void insert(Mirror referrer) {
		for (ModelRelation relation : relations) {
			recursiveFindReferents(relation, referrer);
		}
	}

	@SuppressWarnings("unchecked")
	protected void recursiveFindReferents(ModelRelation relation, Mirror referrer) {
		if (!isRef(referrer)) {
			addToIndex(relation, referrer);
			PropertyMirror[] propertiesMirrors = referrer.getPropertiesMirrors();
			for (PropertyMirror propertyMirror : propertiesMirrors) {
				Object value = referrer.getPropertyValue(propertyMirror.getIndex());
				if (value != null && !propertyMirror.isComputed()) {
					switch (propertyMirror.getTypeMarker()) {
					case Class:
						if (!isRef((Proxiable) value)) {
							recursiveFindReferents(relation, ((Proxiable) value).reflect());
						}
						break;
					case Collection:
						CollectionTypeMarker collectionTypeMarker = ((CollectionPropertyMirror) propertyMirror).getCollectionTypeMarker();
						switch (collectionTypeMarker) {
						case List:
							PropertyMirror innerPropertyMirror = ((ListPropertyMirror) propertyMirror).getItemMirror();
							switch (innerPropertyMirror.getTypeMarker()) {
							case Class:
								for (Proxiable listItem : (List<Proxiable>) value) {
									recursiveFindReferents(relation, listItem.reflect());
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
										recursiveFindReferents(relation, ((Proxiable) entry.getKey()).reflect());
									}
									if (shouldIndexValue) {
										recursiveFindReferents(relation, ((Proxiable) entry.getValue()).reflect());
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
		}
	}

	private void addToIndex(ModelRelation relation, Mirror referrer) {
		Set<Mirror> referentsForReferrerAndRelation = relation.findReferents(referrer);

		// Add the root referrer to referrerToReferents.
		Mirror rootReferrer = referrer.getRootOwner();
		Map<ModelRelation, Set<Mirror>> referentsForReferrer = referrerToReferents.get(rootReferrer);
		if (referentsForReferrer == null) {
			referentsForReferrer = new HashMap<ModelRelation, Set<Mirror>>();
			referrerToReferents.put(rootReferrer, referentsForReferrer);
		}
		referentsForReferrer.put(relation, referentsForReferrerAndRelation);

		// Add the current referrer (which might be an inner object) to referentToReferrers.
		for (Mirror referent : referentsForReferrerAndRelation) {
			Map<ModelRelation, Set<Mirror>> referrersForReferent = referentToReferrers.get(referent);
			if (referrersForReferent == null) {
				referrersForReferent = new HashMap<ModelRelation, Set<Mirror>>();
				referentToReferrers.put(referent, referrersForReferent);
			}
			Set<Mirror> referrersForReferentAndRelation = referrersForReferent.get(relation);
			if (referrersForReferentAndRelation == null) {
				referrersForReferentAndRelation = new HashSet<Mirror>();
				referrersForReferent.put(relation, referrersForReferentAndRelation);
			}
			referrersForReferentAndRelation.add(referrer);
		}
	}

	private boolean isRef(Proxiable obj) {
		boolean result = false;
		if (obj.isProxied()) {
			Kind proxyKind = ((Proxiability) obj).getProxyKind();
			if (proxyKind == Kind.BEHAVIOR_OWNER || proxyKind == Kind.BEHAVIOR_BOTH) {
				result = true;
			}
		}
		return result;
	}

	void update(Mirror referrer) {
		delete(referrer);
		insert(referrer);
	}

	void insertOrUpdate(Mirror referrer) {
		if (referrerToReferents.containsKey(referrer)) {
			update(referrer);
		} else {
			insert(referrer);
		}
	}

	void delete(Mirror referrer) {
		Map<ModelRelation, Set<Mirror>> referentsForReferrer = referrerToReferents.get(referrer);
		for (Entry<ModelRelation, Set<Mirror>> entry : referentsForReferrer.entrySet()) {
			ModelRelation relation = entry.getKey();
			Set<Mirror> referentsForReferrerAndRelation = entry.getValue();
			for (Mirror referent : referentsForReferrerAndRelation) {
				Map<ModelRelation, Set<Mirror>> referrersForReferent = referentToReferrers.get(referent);
				Set<Mirror> referrersForReferentAndRelation = referrersForReferent.get(relation);
				for (Mirror referrerForReferentAndRelation : new HashSet<Mirror>(referrersForReferentAndRelation)) {
					if (referrerForReferentAndRelation.getRootOwner().equals(referrer)) {
						referrersForReferentAndRelation.remove(referrerForReferentAndRelation);
					}
				}
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

	Set<Mirror> findReferrers(InkObject referent, ModelRelation relation) {
		Set<Mirror> result = null;
		Map<ModelRelation, Set<Mirror>> referrersForObject = referentToReferrers.get(referent);
		if (referrersForObject != null) {
			result = referrersForObject.get(relation);
		}
		return result;
	}

	private static ModelRelation[] findRelations() {
		return new ModelRelation[] { ExtendsRelation.getInstance(), IsInstanceOfRelation.getInstance() };
	}
}