
Class id="CodegenFactory" class="ink.core:DslFactory" super="ink.core:ObjectFactory"{
	java_path "factory"
	java_mapping "No_Java"
	imports{
		import ref="ink.core:ObjectFactory"

	}
	description "Ink code-generation DSL"
	loader class="ink.core:DslLoader"
	repository class="ink.core:DslRepository"
	namespace "ink.codegen.velocity"
	dsl_package "org.ink.codegen.velocity"
	java_package "org.ink.codegen.velocity"
}

