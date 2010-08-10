package org.ink.core.vm.lang;

import org.ink.core.vm.factory.Context;
import org.ink.core.vm.factory.DslFactory;

/**
 * @author Lior Schachter
 */
public interface InkClass extends InkType{
	public <T extends InkObjectState> T newInstance();
	public <T extends InkObjectState> T newInstance(Context context);
	public <T extends InkObjectState> T newInstance(boolean initObjectId, boolean initDefaults);
	public void initInstance(InkObjectState state, boolean initObjectId, boolean initDefaults);
	public <T extends InkObjectState> T  newInstance(Context context, boolean initObjectId, boolean initDefaults);
	public <T extends InkObjectState> T  newInstance(DslFactory factory, boolean initObjectId, boolean initDefaults);
}
