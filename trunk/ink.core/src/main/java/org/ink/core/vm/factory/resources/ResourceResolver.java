package org.ink.core.vm.factory.resources;

import java.io.File;

import org.ink.core.vm.factory.DslFactory;
import org.ink.core.vm.lang.InkClassState;
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

	public abstract ClassStructure getClassDetails(InkClassState cls);

	protected String getBehaviorShortClassName(InkClassState cls){
		return cls.reflect().getShortId() + InkNotations.Names.BEHAVIOR_EXTENSION;
	}

	protected String getInterfaceClassShortName(InkClassState cls){
		return cls.reflect().getShortId();
	}

	protected String getDataClassShortName(InkClassState cls){
		return cls.reflect().getShortId() + InkNotations.Names.DATA_CLASS_EXTENSION;
	}

	protected String getStructDataClassShortName(InkClassState cls){
		return cls.reflect().getShortId() + InkNotations.Names.STRUCT_CLASS_EXTENSION;
	}
}