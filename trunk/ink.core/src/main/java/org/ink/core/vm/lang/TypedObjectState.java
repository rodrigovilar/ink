package org.ink.core.vm.lang;

import org.ink.core.vm.lang.internal.annotations.CoreField;

/**
 * @author Lior Schachter
 */
public interface TypedObjectState extends InkObjectState{

	@CoreField(mandatory=true)
	public static final byte p_type = 0;
	
	public InkType getType();
	public void setType(InkTypeState value);
	
	public class Data extends InkObjectState.Data implements TypedObjectState{

		@Override
		public InkType getType() {
			return (InkType) getValue(p_type);
		}

		@Override
		public void setType(InkTypeState value) {
			setValue(p_type, value);
		}
		
	}
	
}
