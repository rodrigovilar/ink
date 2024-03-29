Class id="MetaSegment" class="ink.core:InkClass" super="ink.core:InkClass"{
	java_path ""
	java_mapping "State_Behavior_Interface"
	properties{
		property class="ink.core:IntegerAttribute"{
			name "maxSize"
			max_value 3500
			mandatory true
		}
		property class="ink.core:StringAttribute"{
			name "table_name"
			mandatory true
			reg_exp "[A-Z].*"
		}
	}
}

Class id="MySegment" class="MetaSegment" super="ink.core:InkObject"{
	java_mapping "No_Java"
	java_path ""
	maxSize 3200
	table_name "Segment"
	personality class="CustomerPersonality"{
		fan class="SportFan"{
		}
	}
}


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

Class id="USAAddress" class="ink.core:StructClass" super="Address"{
	java_path ""
	properties{
		property class="ink.core:IntegerAttribute"{
			name "zip"
			mandatory true
			min_value 0
			max_value 99999
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
		property class="ink.core:StringAttribute"{
			name "favorite_player"
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
	values{
		 "Male" "Female"
	}
}

Object id="SportsKind" class="ink.core:EnumType"{
	java_path ""
	values{ 
		"BasketBall" "Tennis" "FootBall" "Rugby"
	}
}

Class id="SendLetterInterceptor" class="ink.core:InkClass" super="ink.core:OperationInterceptor"{
	java_path ""
	java_mapping "State_Behavior"
	properties{
		property class="ink.core:StringAttribute"{
			name "restrictedFirstName"
			mandatory true
		}	
	}
}

Class id="MyProperty" class="ink.core:InkClass" super="ink.core:Reference"{
	java_path ""
	java_mapping "Only_State"
	properties{
		property class="ink.core:DoubleAttribute"{
			name "percentage"
			mandatory false
		}
		property class="ink.core:Reference"{
			type ref="CustomerClass"
			name "type"
			final_value ref="Customer"
		}
	}
}


Class id="Customer" class="example.customer:CustomerClass" super="ink.core:InkObject"{
	java_path ""
	component_type "Root"
	java_mapping "State_Behavior_Interface"
	operations{
		operation class="ink.core:Operation"{
			name "sendLetter"
			interceptors{
				interceptor class="SendLetterInterceptor"{
					restrictedFirstName "Atzmon"
				}			
			}
		}
		operation class="ink.core:Operation"{
			name "isFriend"
			interceptors{
				interceptor class="ink.core:ValidationInterceptor"
			}
		}
	}
	properties{
		property class="ink.core:StringAttribute"{
			name "first_name"
			mandatory true
		}
		property class="MyProperty"{
			name "my_ref"
		}
		property class="ink.core:DateAttribute"{
			name "birthday"
			mandatory false
		}
		property class="ink.core:StringAttribute"{
			name "last_name"
			mandatory true
		}
		property class="ink.core:ModelPathAttribute"{
			name "path"
			mandatory false
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
		property class="ink.core:ListProperty"{
			name "friends"
			mandatory false
			list_item class="ink.core:Reference"{
				name "friend"
				type ref="Customer"
			}
		}
		property class="ink.core:ListProperty"{
			name "enum_list"
			mandatory false
			list_item class="ink.core:EnumAttribute"{
				name "item"
				type ref="Gender"
			}
		}
		property class="ink.core:ListProperty"{
			name "string_list"
			mandatory false
			list_item class="ink.core:StringAttribute"{
				name "item"
			}
		}
		property class="ink.core:MapProperty"{
			name "keyValueMap"
			mandatory false
			specifications class="ink.core:KeyValueDictionary"{
				entry_name "item"
				key class="ink.core:StringAttribute"{
					name "key"
				}
				value class="ink.core:IntegerAttribute"{
					name "value"
				}
			}
		}	
		property class="ink.core:MapProperty"{
			name "elementsMap"
			mandatory false
			specifications class="ink.core:ElementsDictionary"{
				key_property "first_name"
				item class="ink.core:Reference"{
					name "item"
					type ref="Customer"
				}
			}
		}
			

	}
	personality class="example.customer:CustomerPersonality"{
		fan class="example.customer:SportFan"{
		}
	}

}

Class id="SubCustomer" class="example.customer:CustomerClass" super="example.customer:Customer"{
	java_path ""
	component_type "Root"
	java_mapping "No_Java"
	properties{
		property class="ink.core:StringAttribute"{
			name "myprop"
		}	
	}
}

Ink id="TheFirstCustomer" class="example.customer:Customer"{
	first_name "Lior"
	last_name "Schachter"
	age 32
	birthday 1978/10/16
	fan.favorite_sport "BasketBall"
	path "keyValueMap<ads>"
	address class="example.customer:Address"{
		city "Tel-Aviv"
		street "Bar Kokva"
		number 10
	}
	elementsMap{
		item ref="TheSecondCustomer"
	}
	enum_list{
		"Male" "Female"
	}
	string_list{
		 "asd"  "asdasd"
		 "asdaf"  "dsafrwft" "ASD"
	}
	keyValueMap{
		item{
			key "ads"
			value 23
		}
	}
}

Object id="TheSecondCustomer" class="example.customer:Customer" super="TheFirstCustomer"{
	first_name "Also Lior"
	last_name "Also Schachter"
	age 33
	fan.favorite_sport "BasketBall"
	fan.favorite_player "sad"
	stam.shmupu "kuku"
	address class="USAAddress" {
		city "Tel-Aviv"
		street "Hovevey Zion"
		number 10
		zip 02468
	}
}


Object id="TheThirdCustomer" class="example.customer:SubCustomer" super="example.customer:TheFirstCustomer"{
	stam.shmupu "kuku2"
	my_ref ref="TheFirstCustomer"
	path "friends[0].address.city"
	friends{
		friend ref="TheFirstCustomer"
		friend ref="TheSecondCustomer"
	}	
}
