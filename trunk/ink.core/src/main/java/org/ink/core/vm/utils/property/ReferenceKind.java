package org.ink.core.vm.utils.property;

import org.ink.core.vm.lang.internal.annotations.CoreEnumField;

/**
 * @author Lior Schachter
 */
public enum ReferenceKind {
	Association_Only,
	Composition_Only,
	@CoreEnumField(isDefault=true)
	Association_or_Composition;
}
