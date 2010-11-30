package org.ink.core.vm.factory;

import java.io.File;

import org.ink.core.vm.lang.InkClassState;
import org.ink.core.vm.types.EnumTypeState;

/**
 * @author Lior Schachter
 */
public interface InstantiationStrategy {

	public String getBehaviorClassName(InkClassState cls, DslFactory factory);

	public String getInterfaceClassName(InkClassState cls, DslFactory factory);

	public String getDataClassName(InkClassState cls, DslFactory factory);

	public String getEnumClassName(EnumTypeState enumState, DslFactory factory);

	public String getStructDataClassName(InkClassState cls, DslFactory factory);

	public boolean enableEagerFetch();

	public File getDslResourcesLocation(DslFactory factory);
}