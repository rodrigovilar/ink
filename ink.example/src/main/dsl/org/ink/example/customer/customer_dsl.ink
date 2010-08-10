Class id="Address" class="ink.core:StructClass" super="ink.core:Struct"{
	java_path ""
	properties{
		property class="ink.core:StringAttribute"{
			name "city"
			mandatory true
		}
		property class="ink.core:StringAttribute"{
			name "street"
			mandatory true
		}
		property class="ink.core:ShortAttribute"{
			name "number"
			mandatory true
			min_value 0
			max_value 1000
		}
	}
}

Class id="CustomerClass" class="ink.core:InkClass" super="ink.core:InkClass"{
	java_path ""
	java_mapping "No_Java"
}

Class id="SportFan" class="ink.core:TraitClass" super="ink.core:Trait"{
	java_path ""
	java_mapping "No_Java"
	injected_properties{
		property class="ink.core:EnumAttribute"{
			type ref="example.customer:SportsKind"
			name "favorite_sport"
			default_value "FootBall"
		}
	}
}

Class id="CustomerPersonality" class="ink.core:InkClass" super="ink.core:Personality"{
	java_path ""
	java_mapping "No_Java"
	properties{
		property class="ink.core:Reference"{
			type ref="example.customer:SportFan"
			name "fan"
			mandatory true
		}
	}
}

Object id="Gender" class="ink.core:EnumType"{
	java_path ""
	values "Male" "Female"
}

Object id="SportsKind" class="ink.core:EnumType"{
	java_path ""
	values "BasketBall" "Tennis" "FootBall" "Rugby"
}

Class id="Customer" class="example.customer:CustomerClass" super="ink.core:InkObject"{
	java_path ""
	component_type "Root"
	properties{
		property class="ink.core:StringAttribute"{
			name "first_name"
			mandatory true
		}
		property class="ink.core:StringAttribute"{
			name "last_name"
			mandatory true
		}
		property class="ink.core:ByteAttribute"{
			name "age"
			min_value 0
			max_value 120
			mandatory true
		}
		property class="ink.core:Reference"{
			type ref="example.customer:Address"
			name "address"
			mandatory true
		}
		property class="ink.core:EnumAttribute"{
			type ref="example.customer:Gender"
			name "gender"
			default_value "Male"
		}
	}
	personality class="example.customer:CustomerPersonality"{
		fan class="example.customer:SportFan"{
		}
	}

}

Object id="TheFirstCustomer" class="example.customer:Customer"{
	first_name "Lior"
	last_name "Schachter"
	age 32
	fan.favorite_sport "BasketBall"
	address class="example.customer:Address"{
		city "Tel-Aviv"
		street "Bar Kokva"
		number 10
	}
}