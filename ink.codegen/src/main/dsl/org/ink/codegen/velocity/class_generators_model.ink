Class id="ClassGenerator" class="VelocityGeneratorMeta" super="VelocityGenerator"{
	java_path "class_gen"
	java_mapping "State_Behavior"
	properties{
		property class="ink.core:Reference"{
			name "target"
			type ref="ink.core:InkClass"
			value_calculator class="ink.core:RootObjectRetriever"{
				has_static_value true
			}
		}
	}
} 

