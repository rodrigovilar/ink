package org.ink.core.vm.lang;

import org.ink.core.vm.constraints.SystemState;
import org.ink.core.vm.constraints.ValidationContext;
import org.ink.core.vm.proxy.Proxiable;

/**
 * @author Lior chachter
 */
public interface InkObject extends Proxiable{
	
	public void afterStateSet();
	public <T extends InkObjectState> T cloneState();
	public boolean validate(ValidationContext context, SystemState systemState);
	public boolean validate(ValidationContext context);
	
}
