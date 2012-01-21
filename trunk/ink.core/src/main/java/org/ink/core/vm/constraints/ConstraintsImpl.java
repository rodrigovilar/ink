package org.ink.core.vm.constraints;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ink.core.vm.lang.DataTypeMarker;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.Property;
import org.ink.core.vm.lang.property.mirror.CollectionPropertyMirror;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.proxy.Proxiable;
import org.ink.core.vm.traits.TraitImpl;
import org.ink.core.vm.types.CollectionTypeMarker;
import org.ink.core.vm.utils.property.mirror.ListPropertyMirror;
import org.ink.core.vm.utils.property.mirror.MapPropertyMirror;

/**
 * @author Lior Schachter
 */
public class ConstraintsImpl<S extends ConstraintsState> extends TraitImpl<S> implements Constraints {

	@Override
	public boolean validateTarget(Mirror stateSuper, ValidationContext context, SystemState systemState) {
		InkObjectState target = getTargetState();
		Mirror targetMirror = target.reflect();
		PropertyMirror[] props = target.reflect().getPropertiesMirrors();
		PropertyConstraints propConstraints;
		Object value;
		// first, validate generic constraints on top current object
		boolean toContinue = validateGenericLanguageConstraints(stateSuper, context, systemState);
		if (toContinue) {
			// validate inner objects
			PropertyMirror p;
			for (int i = 0; i < props.length; i++) {
				boolean innerContinue = true;
				// validate each inner object separatly, so failure in 1 object will not stop the process
				p = props[i];
				value = targetMirror.getPropertyValue(p.getIndex());
				if (value != null) {
					switch (p.getTypeMarker()) {
					case Class:
						innerContinue = validateClass(stateSuper, context, systemState, value, p);
						break;
					case Collection:
						innerContinue = validateCollection(stateSuper, context, systemState, value, p);
						break;
					}
				}
				if (innerContinue) {
					// validate the object against the property constraints
					propConstraints = p.getTargetBehavior().asTrait(InkObjectState.t_constraints);
					innerContinue = propConstraints.validatePropertyValue((Property) p.getTargetBehavior(), value, target, context, systemState);
				}
				toContinue &= innerContinue;
			}
			if (toContinue) {
				// finally, validate current object specific constraints
				Map<String, InstanceValidator> validatorsMap = getState().getValidators();
				if (validatorsMap != null) {
					Collection<InstanceValidator> validators = getState().getValidators().values();
					for (InstanceValidator v : validators) {
						v.validate(target, stateSuper, context, systemState);
						if (context.aborted()) {
							toContinue = false;
							break;
						}
					}
				}
			}
		}
		return toContinue ? !context.containsError() : false;
	}

	private boolean validateCollection(Mirror stateSuper, ValidationContext context, SystemState systemState, Object value, PropertyMirror p) {
		boolean toContinue = true;
		PropertyMirror innerP;
		CollectionTypeMarker collectionTypeMarker = ((CollectionPropertyMirror) p).getCollectionTypeMarker();
		switch (collectionTypeMarker) {
		case List:
			if (!((List<?>) value).isEmpty()) {
				innerP = ((ListPropertyMirror) p).getItemMirror();
				if (innerP.isValueDrillable()) {
					toContinue = validateInnerCollection(null, context, systemState, (Collection<?>) value, innerP);
				}
			}
			break;
		case Map:
			if (!((Map<?, ?>) value).isEmpty()) {
				innerP = ((MapPropertyMirror) p).getKeyMirror();
				if (innerP.isValueDrillable()) {
					for(Object innerCol : ((Map<?, ?>) value).values()){
						toContinue = validateInnerCollection(null, context, systemState, (Collection<?>)innerCol, innerP);
					}
				}
				if (toContinue) {
					innerP = ((MapPropertyMirror) p).getValueMirror();
					if (innerP.isValueDrillable()) {
						byte index = p.getIndex();
						if (innerP.getTypeMarker() == DataTypeMarker.Class) {
							Object superValue = null;
							if(stateSuper != null && index < stateSuper.getPropertiesCount()){
								superValue = stateSuper.getPropertyValue(index);
							}
							Object temp;
							Map.Entry<?, ?> en;
							Iterator<?> enIter = ((Map<?, ?>) value).entrySet().iterator();
							Mirror m;
							while (enIter.hasNext() && toContinue) {
								en = (Entry<?, ?>) enIter.next();
								m = ((Proxiable) en.getValue()).reflect();
								if (!m.isRoot()) {
									temp = null;
									if (superValue != null) {
										temp = ((Map<?, ?>) superValue).get(en.getKey());
									}
									toContinue = validateInnerObject(temp, context, systemState, en.getValue(), innerP);
								}
							}
						} else {
							for(Object innerCol : ((Map<?, ?>) value).values()){
								toContinue = validateInnerCollection(null, context, systemState, (Collection<?>)innerCol, innerP);
							}
						}
					}
				}
			}
			break;
		}
		return toContinue;
	}

	private boolean validateClass(Mirror stateSuper, ValidationContext context, SystemState systemState, Object value, PropertyMirror p) {
		boolean toContinue = true;
		Mirror m = ((Proxiable) value).reflect();
		if (!m.isRoot()) {
			Object superValue;
			byte index = p.getIndex();
			if (stateSuper != null && index < stateSuper.getPropertiesCount()) {
				superValue = stateSuper.getPropertyValue(index);
			} else {
				superValue = null;
			}
			toContinue = validateInnerObject(superValue, context, systemState, value, p);
		}
		return toContinue;
	}

	private boolean validateInnerCollection(Object superValue, ValidationContext context, SystemState systemState, Collection<?> value, PropertyMirror p) {
		Mirror m;
		for (Object o : value) {
			switch (p.getTypeMarker()) {
			case Class:
				m = ((Proxiable) o).reflect();
				if (!m.isRoot()) {
					if (!validateInnerObject(superValue, context, systemState, o, p)) {
						return false;
					}
				}
				break;
			case Collection:
				if (!validateCollection(null, context, systemState, value, p)) {
					return false;
				}
				break;
			}
		}
		return true;
	}

	private boolean validateInnerObject(Object superValue, ValidationContext context, SystemState systemState, Object value, PropertyMirror p) {
		Constraints innerConstraints;
		Mirror superValueMirror = null;
		innerConstraints = ((Proxiable) value).asTrait(InkObjectState.t_constraints);
		if (superValue != null) {
			superValueMirror = ((Proxiable) superValue).reflect();
		}
		if (innerConstraints.validateTarget(superValueMirror, context, systemState)) {
			return true;
		}
		return !context.aborted();
	}

	private boolean validateGenericLanguageConstraints(Mirror stateSuper, ValidationContext context, SystemState systemState) {
		getState().getGenericConstraints().validate(getTargetState(), stateSuper, context, systemState);
		return !context.containsError();
	}

}
