package org.ink.core.vm.factory.resources;

import java.io.File;

import org.ink.core.vm.factory.DslFactory;
import org.ink.core.vm.lang.InkClassState;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.types.EnumTypeState;
import org.ink.core.vm.utils.InkNotations;

/**
 * @author Lior Schachter
 */
public abstract class ResourceResolver {

	public abstract String getBehaviorClassName(InkClassState cls);

	public abstract String getInterfaceClassName(InkClassState cls);

	public abstract String getDataClassName(InkClassState cls);

	public abstract String getEnumClassName(EnumTypeState enumState);

	public abstract String getStructDataClassName(InkClassState cls);

	public abstract boolean enableEagerFetch();

	public abstract File getDslResourcesLocation(DslFactory factory);

	public abstract JavaClassDescription getBehaviorClassDescription(ClassMirror cm);

	public abstract JavaClassDescription getInterfaceDescription(ClassMirror cm);

	public String getBehaviorShortClassName(ClassMirror cm) {
		return cm.getShortId() + InkNotations.Names.BEHAVIOR_EXTENSION;
	}

	public String getInterfaceClassShortName(ClassMirror cm) {
		return cm.getShortId();
	}

	public String getDataClassShortName(ClassMirror cm) {
		return cm.getShortId() + InkNotations.Names.DATA_CLASS_EXTENSION;
	}

	public String getStateClassShortName(ClassMirror cm) {
		return cm.getShortId() + InkNotations.Names.STATE_CLASS_EXTENSION;
	}

	public String getStructDataClassShortName(ClassMirror cm) {
		return cm.getShortId() + InkNotations.Names.STRUCT_CLASS_EXTENSION;
	}
}