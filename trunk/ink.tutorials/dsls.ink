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