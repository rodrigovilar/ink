// Meta-class
Class id="F3_MetaCache" class="ink.core:InkClass" super="ink.core:InkClass"{
	java_path ""
	java_mapping "No_Java"				// This model class doesn't have a java counterpart (yet).
	component_type "Root"		// Can not have an owner.
	properties{
		property class="ink.core:Reference"{
			type ref="example.http:F3_CacheManager"
			name "cache"
			mandatory true
		}
	}
}

Class id="F3_CacheManager" class="ink.core:InkClass" super="ink.core:InkObject" abstract=true{
	java_path ""
	java_mapping "No_Java"				// This model class doesn't have a java counterpart (yet).
	component_type "Root_or_Pure_Component"		// Can have an owner.
}

Class id="F3_StandardCache" class="ink.core:InkClass" super="example.http:F3_CacheManager" {
	java_path ""
	java_mapping "No_Java"				// This model class doesn't have a java counterpart (yet).
	component_type "Root_or_Pure_Component"		// Can have an owner.
	properties{
		property class="ink.core:LongAttribute"{
			name "maxElementsInMemory"
			mandatory true
		}
		property class="ink.core:LongAttribute"{
			name "timeToLive"
			mandatory true
		}
	}
}


// HTTP_Client class is an instance of MetaCache meta-class.
Class id="F3_HTTP_Client" class="example.http:F3_MetaCache" super="ink.core:InkObject" {
	java_path ""
	java_mapping "No_Java"				// This model class doesn't have a java counterpart (yet).
	component_type "Root_or_Pure_Component"		// Can have an owner.
	properties{
		property class="ink.core:LongAttribute"{
			name "numberOfRetries"
			mandatory true
		}
		property class="ink.core:LongAttribute"{
			name "timeout"
			mandatory true
		}
		property class="ink.core:StringAttribute" {
			name "URL"
		}
	}
	cache class="example.http:F3_StandardCache" {
		maxElementsInMemory 500
		timeToLive 60
	}
}

Object id="F3_FastHTTP_Client" class="example.http:F3_HTTP_Client" {
	numberOfRetries 2
	timeout 2
}

Object id="F3_RobustHTTP_Client" class="example.http:F3_HTTP_Client" {
	numberOfRetries 8
	timeout 15
}

Object id="F3_PontisLogoRetriever" class="example.http:F3_HTTP_Client" super="example.http:F3_FastHTTP_Client" {
	URL "http://www.pontis.com/logo.bmp"
	timeout 2
}

