Class id="CustomerFactory" class="ink.core:DslFactory" super="ink.core:ObjectFactory"{
	java_path "factory"
	java_mapping "No_Java"
	imports{
		import ref="ink.core:ObjectFactory"
	}
	namespace "example.customer"
	loader class="ink.core:DslLoader"
	repository class="ink.core:DslRepository"
	dsl_package "org.ink.example.customer"
	java_package "org.ink.example.customer"
}


Class id="ShapesFactory" class="ink.core:DslFactory" super="ink.core:ObjectFactory"{
	java_path "factory"
	java_mapping "No_Java"
	imports{
		import ref="ink.core:ObjectFactory"
	}
	namespace "example.shape"	// DSL name
	loader class="ink.core:DslLoader"
	repository class="ink.core:DslRepository"
	dsl_package "org.ink.language.example.shapes"		// DSL source folder
	java_package "org.ink.language.example.shapes"		// java source folder
}
