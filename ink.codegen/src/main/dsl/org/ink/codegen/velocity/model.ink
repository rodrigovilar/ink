Class id="VelocityExecutor" class="ink.core:InkClass" super="ink.core:InkObject"{
	java_mapping "State_Behavior_Interface"
	can_cache_behavior_instance true
	properties{
		property class="ink.core:MapProperty"{
			name "configurations"
			specifications class="ink.core:KeyValueDictionary"{
				entry_name "item"
				key class="ink.core:StringAttribute"{
					name "key"
				}
				value class="ink.core:StringAttribute"{
					name "value"
				}
			}
			mandatory true
		}
	}
}

Class id="VelocityGeneratorMeta" class="ink.core:InkClass" super="ink.core:InkClass"{
	java_mapping "State_Behavior_Interface"
	properties{
		property class="ink.core:Reference"{
			name "configurator"
			type ref="VelocityExecutor"
		} 
	}
}

Class id="VelocityGenerator" class="VelocityGeneratorMeta" super="ink.core:InkObject"{
	java_mapping "State_Behavior_Interface"
	configurator ref="defaultVelocityExecutor"
	properties{
		property class="ink.core:StringAttribute"{
			name "template_relative_path"
			mandatory true
		}
	}
}

Object id="defaultVelocityExecutor" class="VelocityExecutor"{
	configurations{
		item{
			key "file.resource.loader.class"
			value "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader"
		}
	}
}