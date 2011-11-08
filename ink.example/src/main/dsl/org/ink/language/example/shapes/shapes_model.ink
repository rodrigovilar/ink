Class id="Drawing" class="ink.core:InkClass" super="ink.core:InkObject"{
	java_path ""
	java_mapping "Only_State"
	component_type "Root"
	properties{
		property class="ink.core:ListProperty"{
			name "elements"
			mandatory true
			inheritance_constraints "Instance_Can_Refine_Inherited_Value"
			list_item class="ink.core:Reference"{
				type ref="example.shapes:Shape"
				name "element"
			}
		}
	}
}

Class id="Shape" class="ink.core:InkClass" super="ink.core:InkObject" {
	java_path ""
	java_mapping "Only_State"
	component_type "Root"
	properties{
		property class="ink.core:LongAttribute"{
			name "x"
			mandatory true
		}
		property class="ink.core:LongAttribute"{
			name "y"
			mandatory true
		}
	}
}
