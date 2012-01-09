package org.ink.core.vm.factory;

import org.ink.core.vm.lang.Struct;

/**
 * @author Lior Schachter
 */
public interface DslFactoryEvent extends Struct {

	public static final byte p_kind = 0;

	public DslFactoryEventKind getKind();

	public void setKind(DslFactoryEventKind value);

	public class Data extends Struct.Data implements DslFactoryEvent {

		@Override
		public DslFactoryEventKind getKind() {
			return (DslFactoryEventKind) getValue(p_kind);
		}

		@Override
		public void setKind(DslFactoryEventKind value) {
			setValue(p_kind, value);
		}
	}

}
