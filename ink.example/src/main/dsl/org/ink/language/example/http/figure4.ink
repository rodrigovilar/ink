// Meta-class
Class id="F4_MetaCache" class="ink.core:InkClass" super="ink.core:InkClass"{
	java_path ""
	java_mapping "No_Java"				// This model class doesn't have a java counterpart (yet).
	component_type "Root"		// Can not have an owner.
	properties{
		property class="ink.core:Reference"{
			type ref="example.http:F4_CacheManager"
			name "cache"
			mandatory true
		}
	}
}

Class id="F4_CacheManager" class="ink.core:InkClass" super="ink.core:InkObject" abstract=true{
	java_path ""
	java_mapping "No_Java"				// This model class doesn't have a java counterpart (yet).
	component_type "Root_or_Pure_Component"		// Can have an owner.
}

Class id="F4_StandardCache" class="ink.core:InkClass" super="example.http:F4_CacheManager" {
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
Class id="F4_HTTP_Client" class="ink.core:InkClass" super="ink.core:InkObject" {
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
}

Class id="F4_PictureRetriever" class="F4_MetaCache" super="F4_HTTP_Client" {
	java_path ""
	java_mapping "No_Java"				// This model class doesn't have a java counterpart (yet).
	component_type "Root_or_Pure_Component"		// Can have an owner.
	cache class="F4_StandardCache" {
		timeToLive 600
		maxElementsInMemory 1000
	}
}

Class id="F4_NewsRetriever" class="F4_MetaCache" super="F4_HTTP_Client" {
	java_path ""
	java_mapping "No_Java"				// This model class doesn't have a java counterpart (yet).
	component_type "Root_or_Pure_Component"		// Can have an owner.
	cache class="F4_StandardCache" {
		timeToLive 10
		maxElementsInMemory 10000
	}
}

Class id="F4_StockQuoteRetriever" class="F4_MetaCache" super="F4_HTTP_Client" {
	java_path ""
	java_mapping "No_Java"				// This model class doesn't have a java counterpart (yet).
	component_type "Root_or_Pure_Component"		// Can have an owner.
	cache class="F4_StandardCache" {
		timeToLive 0
		maxElementsInMemory 0
	}
}



Object id="F4_FastHTTP_Client" class="example.http:F4_HTTP_Client" {
	numberOfRetries 2
	timeout 2
}

Object id="F4_CNN_NewsRetriever" class="F4_NewsRetriever" super="F4_FastHTTP_Client" {
	URL "www.cnn.com/news"
}


