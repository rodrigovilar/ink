package org.ink.core.vm.factory;

import org.ink.core.vm.lang.internal.annotations.CoreField;
import org.ink.core.vm.traits.TraitState;

/**
 * @author Lior Schachter
 */
public interface DslFactoryEventListenerState extends TraitState{
	
	@CoreField(defaultValue="true")
	public static final byte p_bind_on_creation = 1;
	
	public Boolean getBindOnCreation();
	public void setBindOnCreation(Boolean value);
	
	public class Data extends TraitState.Data implements DslFactoryEventListenerState{

		@Override
		public Boolean getBindOnCreation() {
			return (Boolean)getValue(p_bind_on_creation);
		}

		@Override
		public void setBindOnCreation(Boolean value) {
			setValue(p_bind_on_creation, value);
		}
	}
}
