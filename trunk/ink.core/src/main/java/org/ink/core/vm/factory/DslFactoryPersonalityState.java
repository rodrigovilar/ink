package org.ink.core.vm.factory;

import org.ink.core.vm.lang.JavaMapping;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;
import org.ink.core.vm.traits.PersonalityState;

/**
 * @author Lior Schachter
 */
@CoreClassSpec(javaMapping=JavaMapping.Only_State)
public interface DslFactoryPersonalityState extends PersonalityState{
	
	public static final byte p_app_context = 2;
	public static final byte p_event_dispatcher = 3;
	
	public Context getContext();
	public void setAppContext(ContextState value);
	
	public DslFactoryEventDispatcherState getEventDispatcher();
	public void setEventDispatcher(DslFactoryEventDispatcherState value);
	
	public class Data extends PersonalityState.Data implements DslFactoryPersonalityState{

		@Override
		public Context getContext() {
			return (Context)getValue(p_app_context);
		}

		@Override
		public void setAppContext(ContextState value) {
			setValue(p_app_context, value);
		}

		@Override
		public DslFactoryEventDispatcherState getEventDispatcher() {
			return (DslFactoryEventDispatcherState)getValue(p_event_dispatcher);
		}

		@Override
		public void setEventDispatcher(DslFactoryEventDispatcherState value) {
			setValue(p_event_dispatcher, value);
		}
		
	}

}
