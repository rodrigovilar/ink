package org.ink.core.vm.traits;

import java.util.List;

import org.ink.core.vm.lang.InkClassState;
import org.ink.core.vm.lang.Property;
import org.ink.core.vm.lang.PropertyState;
import org.ink.core.vm.lang.internal.annotations.CoreField;
import org.ink.core.vm.lang.internal.annotations.CoreListField;

/**
 * @author Lior Schachter
 */
public interface TraitClassState extends InkClassState{
	
	@CoreListField(itemName="property")
	public static final byte p_injected_properties = p_personality+1;
	@CoreField(defaultValue="Structural_or_Detachable")
	public static final byte p_kind = p_injected_properties + 1;
	
	//TODO - should be mandatory true with value calculator
	@CoreField(mandatory=false)
	public static final byte p_role = p_kind + 1;
	
	public List<? extends Property> getInjectedProperties();
	public void setInjectedProperties(List<? extends PropertyState> value);

	public TraitKind getKind();
	public void setKind(TraitKind value);

	public String getRole();
	public void setRole(String value);

	
	public class Data extends InkClassState.Data implements TraitClassState{
		
		@SuppressWarnings("unchecked")
		@Override
		public List<? extends Property> getInjectedProperties() {
			return (List<? extends Property>) getValue(p_injected_properties);
		}
		
		@Override
		public void setInjectedProperties(List<? extends PropertyState> value) {
			setValue(p_injected_properties, value);
		}
		
		@Override
		public TraitKind getKind() {
			return (TraitKind) getValue(p_kind);
		}

		@Override
		public void setKind(TraitKind value) {
			setValue(p_kind, value);
		}

		@Override
		public String getRole() {
			return (String)getValue(p_role);
		}

		@Override
		public void setRole(String value) {
			setValue(p_role, value);
		}
	}

}
