Class id="RootObjectRetriever" class="ink.core:InkClass" super="ink.core:PropertyValueCalculator"{
	java_path "class_gen"
	java_mapping "State_Behavior"	
}  



Class id="ClassGenerator" class="VelocityGeneratorMeta" super="VelocityGenerator"{
	java_path "class_gen"
	java_mapping "State_Behavior"
	properties{
		property class="ink.core:Reference"{
			name "target"
			type ref="ink.core:InkClass"
			value_calculator class="RootObjectRetriever"{
				has_static_value true
			}
		}
	}
} 

