Class class="ink.core:TraitClass" id="TestTrait" super="ink.core:Trait"{
	java_path "traits"
	java_mapping "No_Java"
	role "stam"
	kind "Detachable"
	properties{
		property class="ink.core:StringAttribute"{
			name "kuku"
			mandatory true
		}
	}
	injected_properties{
		property class="ink.core:StringAttribute"{
			name "shmupu"
			mandatory false
		}
	}
}

Object class="example.customer:TestTrait" id="TestTriatObject"{
	kuku "haha"
	target_locator class="ink.core:ClassHierarchyLocator"{
		root_class ref="example.customer:Customer"
	}
}