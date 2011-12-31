Metaclass id="Movie" class="ink.core:InkClass" super="ink.core:InkClass" {
	java_path ""
	java_mapping "State_Behavior_Interface"
	component_type "Root"
	properties {
		property class="ink.core:StringAttribute" {
			name "title"
			mandatory true
		}
		property class="ink.core:StringAttribute" {
			name "rating"
			mandatory true
		}
	}
}


Class id="Videotape" class="Movie" super="ink.core:InkObject" abstract=true {
	java_path ""
	java_mapping "State_Behavior_Interface"
	component_type "Root"
	properties {
		property class="ink.core:BooleanAttribute" {
			name "isRented"
			default_value false
		}
		property class="ink.core:Reference" {
			type ref="Customer"
			name "renter"
			mandatory false
		}
	}
}

Object id="sad" class="KillBill" super="KillBillTape1"{
	isRented true
}

Class id="Customer" class="ink.core:InkClass" super="ink.core:InkObject" {
	java_path ""
	java_mapping "Only_State"
	component_type "Root"
	properties {
		property class="ink.core:StringAttribute" {
			name "name"
			mandatory true
		}
		property class="ink.core:IntegerAttribute" {
			name "age"
			mandatory true
		}
	}
}

Metaclass id="RestrictedMovie" class="ink.core:InkClass" super="Movie" {
	java_path ""
	java_mapping "State_Behavior_Interface"
	component_type "Root"
	properties {
		property class="ink.core:IntegerAttribute" {
			name "minimumAge"
			mandatory true
			min_value 10
			max_value 100
		}
		property class="ink.core:StringAttribute" {
			name "rating"
			final_value "R"
		}
	}
}

Class id="RestrictedVideotape" class="RestrictedMovie" super="Videotape" abstract=true {
	java_path ""
	java_mapping "State_Behavior"
	component_type "Root"
}
