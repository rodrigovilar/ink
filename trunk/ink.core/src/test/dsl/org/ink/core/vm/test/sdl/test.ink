Object id="ComponentType" class="ink.core:EnumType"{
	java_path "lang"
	values "Root" "Pure_Component" "Root_or_Pure_Component"
}

Class id="StringAttribute" class="ink.core:InkClass" super="ink.core:PrimitiveAttribute"{
	factory class="ink.core:ObjectFactory"
	java_path "utils.property"
	java_mapping "State_Behavior_Interface"
	can_cache_behavior_instance true
	component_type "Root_or_Pure_Component"
	properties{
		property class="ink.core:Reference"{
			type ref="ink.core:InkType"
			name "type"
			mandatory true
			inheritance_constraints "Instance_Can_Refine_Inherited_Value"
			final_value ref="ink.core:String"
			kind "Association_or_Composition"
		}
		property class="ink.core:StringAttribute"{
			name "name"
			max_length 100
			mandatory "true"
			inheritance_constraints "Instance_Can_Refine_Inherited_Value"
		}
		property class="ink.core:BooleanAttribute"{
			name "mandatory"
			mandatory true
			inheritance_constraints "Instance_Can_Refine_Inherited_Value"
			default_value false
		}
		property class="ink.core:StringAttribute"{
			name "display_name"
			mandatory false
			inheritance_constraints "Instance_Can_Refine_Inherited_Value"
		}
		property class="ink.core:StringAttribute"{
			name "description"
			mandatory false
			inheritance_constraints "Instance_Must_Override_Inherited_Value"
		}
		property class="ink.core:EnumAttribute"{
			type ref="ink.core:InheritanceConstraints"
			name "inheritance_constraints"
			mandatory true
			inheritance_constraints "Instance_Can_Refine_Inherited_Value"
			default_value "Instance_Can_Refine_Inherited_Value"
		}
		property class="ink.core:Reference"{
			type ref="ink.core:PropertyValueCalculator"
			name "value_calculator"
			mandatory false
			inheritance_constraints "Instance_Can_Refine_Inherited_Value"
			kind "Association_or_Composition"
		}
		property class="ink.core:StringAttribute"{
			name "default_value"
			mandatory false
			inheritance_constraints "Instance_Can_Refine_Inherited_Value"
		}
		property class="ink.core:StringAttribute"{
			name "final_value"
			mandatory false
			inheritance_constraints "Instance_Can_Refine_Inherited_Value"
		}
		property class="ink.core:IntegerAttribute"{
			name "min_length"
			mandatory false
			inheritance_constraints "Instance_Can_Refine_Inherited_Value"
		}
		property class="ink.core:IntegerAttribute"{
			name "max_length"
			mandatory false
			inheritance_constraints "Instance_Can_Refine_Inherited_Value"
		}
		property class="ink.core:StringAttribute"{
			name "reg_exp"
			mandatory false
			inheritance_constraints "Instance_Can_Refine_Inherited_Value"
		}
	}
	personality class="ink.core:Personality"{
		reflection class="ink.core:PrimitiveAttributeMirror"{
			editor class="ink.core:ObjectEditor"
		}
		constraints class="ink.core:PropertyConstraints"{
			generic_constraints class="ink.core:GenericInstanceValidator"
			validators{
				item{
					name "string_attribute_validator"
					validator class="ink.core:StringAttributeValidator"
				}
			}
			generic_property_value_constraints class="ink.core:GenericPropertyValueValidator"
			property_value_validators{
				item{
					key "string_value_validator"
				 	validator class="ink.core:StringAttributeValueValidator"
				}
			}
		}
	}
}