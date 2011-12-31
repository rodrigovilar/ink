Class id="Tutorial1Factory" class="ink.core:DslFactory" super="ink.core:ObjectFactory"{
	java_path "factory"
	java_mapping "No_Java"
	imports{
		import ref="ink.core:ObjectFactory"
	}
	namespace "ink.tutorial1"
	loader class="ink.core:DslLoader"
	repository class="ink.core:DslRepository"
	dsl_package "org.ink.tutorial1"
	java_package "org.ink.tutorial1"
}


Class id="Tutorial2Factory" class="ink.core:DslFactory" super="ink.core:ObjectFactory"{
	java_path "factory"
	java_mapping "No_Java"
	imports{
		import ref="ink.core:ObjectFactory"

	}
	description "Ink tutorial 2"
	loader class="ink.core:DslLoader"
	repository class="ink.core:DslRepository"
	namespace "ink.tutorial2"
	dsl_package "org.ink.tutorial2"
	java_package "org.ink.tutorial2"
}
Class id="Tutorila3Factory" class="ink.core:DslFactory" super="ink.core:ObjectFactory"{
	java_path "factory"
	java_mapping "No_Java"
	imports{
		import ref="ink.core:ObjectFactory"

	}
	description "Ink tutorial 3 - Videostore example"
	loader class="ink.core:DslLoader"
	repository class="ink.core:DslRepository"
	namespace "ink.tutorial3"
	dsl_package "org.ink.tutorial3"
	java_package "org.ink.tutorial3"
}

