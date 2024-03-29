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
	namespace "example.shapes"	// DSL name
	loader class="ink.core:DslLoader"
	repository class="ink.core:DslRepository"
	dsl_package "org.ink.language.example.shapes"		// DSL source folder
	java_package "org.ink.language.example.shapes"		// java source folder
}

Class id="HTTPFactory" class="ink.core:DslFactory" super="ink.core:ObjectFactory"{
	java_path "factory"
	java_mapping "No_Java"
	imports{
		import ref="ink.core:ObjectFactory"
	}
	namespace "example.http"	// DSL name
	loader class="ink.core:DslLoader"
	repository class="ink.core:DslRepository"
	dsl_package "org.ink.language.example.http"		// DSL source folder
	java_package "org.ink.language.example.http"		// java source folder
}

Class id="VideoStoreFactory" class="ink.core:DslFactory" super="ink.core:ObjectFactory" {
	java_path "factory"
	java_mapping "No_Java"
	imports{
		import ref="ink.core:ObjectFactory"
	}
	namespace "example.videostore"	// DSL name
	loader class="ink.core:DslLoader"
	repository class="ink.core:DslRepository"
	dsl_package "org.ink.language.example.videostore"		// DSL source folder
	java_package "org.ink.language.example.videostore"		// java source folder
}
