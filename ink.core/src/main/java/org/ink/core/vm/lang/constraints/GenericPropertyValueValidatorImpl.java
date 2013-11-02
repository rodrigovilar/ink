package org.ink.core.vm.lang.constraints;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ink.core.vm.constraints.PropertyValueValidator;
import org.ink.core.vm.constraints.SystemState;
import org.ink.core.vm.constraints.ValidationContext;
import org.ink.core.vm.constraints.ValidatorImpl;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.Property;
import org.ink.core.vm.lang.property.mirror.CollectionPropertyMirror;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.proxy.Proxiable;
import org.ink.core.vm.types.CollectionTypeMarker;
import org.ink.core.vm.utils.property.mirror.ListPropertyMirror;
import org.ink.core.vm.utils.property.mirror.MapPropertyMirror;

/**
 * @author Lior Schachter
 */
public class GenericPropertyValueValidatorImpl<S extends GenericPropertyValueValidatorState> extends ValidatorImpl<S> implements PropertyValueValidator {

	@Override
	public void validate(Property property, Object propertyValue, InkObjectState dataContainer, ValidationContext context, SystemState systemState) {
		if (propertyValue == null) {
			if (!dataContainer.reflect().getRootOwner().isAbstract() && property.isMandatory()) {
				context.addError(dataContainer, this, "field.required", property.getName());
			}
		} else {
			PropertyMirror pMirror = property.reflect();
			validatePropertyTypeConstraints(propertyValue, dataContainer, context, pMirror);

		}

	}

	protected void validatePropertyTypeConstraints(Object propertyValue, InkObjectState dataContainer, ValidationContext context, PropertyMirror pMirror) {
		Class<?> propertyTypeClass = pMirror.getTypeClass();
		switch (pMirror.getTypeMarker()) {
		case CLASS:
			validateClassValue(propertyValue, dataContainer, context, pMirror);
			break;
		case COLLECTION:
			validateCollectionValue(propertyValue, dataContainer, context, pMirror, propertyTypeClass);
			break;
		default:
			if (!propertyTypeClass.isAssignableFrom(propertyValue.getClass())) {
				context.addError(dataContainer, this, "wrong.value.type", pMirror.getName(), pMirror.getTypeClass().getName(), propertyValue.getClass().getName());
			}
			break;
		}
	}

	private void validateCollectionValue(Object propertyValue, InkObjectState dataContainer, ValidationContext context, PropertyMirror pMirror, Class<?> propertyTypeClass) {
		CollectionTypeMarker collectionMarker = ((CollectionPropertyMirror) pMirror).getCollectionTypeMarker();
		PropertyMirror itemMirror;
		switch (collectionMarker) {
		case LIST:
			try {
				List<?> col = (List<?>) propertyValue;
				itemMirror = ((ListPropertyMirror) pMirror).getItemMirror();
				for (Object o : col) {
					validatePropertyTypeConstraints(o, dataContainer, context, itemMirror);
				}
			} catch (ClassCastException e) {
				String expected = pMirror.getPropertyType().reflect().getId();
				String actual;
				if (propertyValue instanceof Proxiable) {
					actual = ((Proxiable) propertyValue).getMeta().reflect().getId();
				} else {
					actual = propertyValue.getClass().getName();
				}
				context.addError(dataContainer, this, "wrong.value.type", pMirror.getName(), expected, actual);
			}
			break;
		case MAP:
			try {
				Map<?, ?> mapValue = (Map<?, ?>) propertyValue;
				itemMirror = ((MapPropertyMirror) pMirror).getKeyMirror();
				Iterator<?> iter = mapValue.keySet().iterator();
				while (iter.hasNext()) {
					validatePropertyTypeConstraints(iter.next(), dataContainer, context, itemMirror);
				}
				itemMirror = ((MapPropertyMirror) pMirror).getValueMirror();
				iter = mapValue.values().iterator();
				while (iter.hasNext()) {
					validatePropertyTypeConstraints(iter.next(), dataContainer, context, itemMirror);
				}
			} catch (ClassCastException e) {
				String expected = pMirror.getPropertyType().reflect().getId();
				String actual;
				if (propertyValue instanceof Proxiable) {
					actual = ((Proxiable) propertyValue).getMeta().reflect().getId();
				} else {
					actual = propertyValue.getClass().getName();
				}
				context.addError(dataContainer, this, "wrong.value.type", pMirror.getName(), expected, actual);
			}
			break;
		default:
			throw new UnsupportedOperationException(collectionMarker.name());
		}
	}

	protected void validateClassValue(Object propertyValue, InkObjectState dataContainer, ValidationContext context, PropertyMirror pMirror) {
		try {
			Mirror vMirror = ((Proxiable) propertyValue).reflect();
			ClassMirror propertyType = pMirror.getPropertyType().reflect();
			if (!vMirror.getClassMirror().isSubClassOf(propertyType)) {
				context.addError(dataContainer, this, "wrong.value.type", pMirror.getName(), pMirror.getPropertyType().reflect().getId(), vMirror.getClassMirror().getId());
			}
		} catch (ClassCastException e) {
			context.addError(dataContainer, this, "wrong.value.type", pMirror.getName(), pMirror.getPropertyType().reflect().getId(), propertyValue.getClass().getName());
		}
	}

}
