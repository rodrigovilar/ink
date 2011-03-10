

Class id="AbstractOffer" class="ink.core:InkClass" super="ink.core:InkObject" abstract=true {
	java_path ""
	java_mapping "State_Behavior_Interface"
	properties{
		property class="ink.core:BooleanAttribute"{
			name "studentOnlyOffer"
			mandatory true
		}
		property class="ink.core:DateAttribute"{
			name "validUntil"
			mandatory true
		}
		property class="ink.core:LongAttribute" {
			name "periodsCondition"
			mandatory true
		}
	}
}

Class id="FixedPercentageOffer" class="ink.core:InkClass" super="ink.core:InkObject"{
	java_path ""
	java_mapping "State_Behavior_Interface"
	properties{
		property class="ink.core:DoubleAttribute"{
			name "percentage"
			mandatory true
		}
	}
}

Class id="FixedAmountOffer" class="ink.core:InkClass" super="ink.core:InkObject"{
	java_path ""
	java_mapping "State_Behavior_Interface"
	properties{
		property class="ink.core:DoubleAttribute"{
			name "amount"
			mandatory true
		}
	}
}

