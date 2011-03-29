Class id="BaseOffer" class="ink.core:InkClass" super="ink.core:InkObject" abstract=true {
	java_path ""
	java_mapping "State_Behavior_Interface"
	properties{
		property class="ink.core:BooleanAttribute"{
			name "studentOnlyOffer"
			mandatory true
		}
		property class="ink.core:BooleanAttribute"{
			name "renewalOnlyOffer"
			mandatory true
		}
		property class="ink.core:LongAttribute"{
			name "conditionForPeriodsSigned"
			mandatory false
		}
		
		property class="ink.core:IntegerAttribute" {
			name "freeIssues"
			mandatory false
		}
		property class="ink.core:DateAttribute"{
			name "validUntil"
			mandatory true
		}
	}
}

Class id="FixedPercentageDiscountOffer" class="ink.core:InkClass" super="BaseOffer" abstract=false{
	java_path ""
	java_mapping "State_Behavior"
	properties{
		property class="ink.core:DoubleAttribute"{
			name "percentage"
			mandatory true
		}
	}
}


Class id="FixedPriceOffer" class="ink.core:InkClass" super="BaseOffer" abstract=false{
	java_path ""
	java_mapping "State_Behavior"
	properties{
		property class="ink.core:DoubleAttribute"{
			name "amount"
			mandatory true
		}
	}
}

Class id="ActiveOffers" class="ink.core:InkClass" super="ink.core:InkObject" {
	java_path ""
	java_mapping "State_Behavior_Interface"
	properties {
		property class="ink.core:ListProperty"{
			type ref="ink.core:List"
			name "offers"
			mandatory true
			inheritance_constraints "Instance_Can_Refine_Inherited_Value"
			list_item class="ink.core:Reference"{
				type ref="ink.tutorial1:BaseOffer"
				name "offer"
			}
		}

	}	
}