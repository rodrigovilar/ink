// Meta-class
Class id="F5_MetaCache" class="ink.core:InkClass" super="ink.core:InkClass"{
	java_path ""
	java_mapping "No_Java"				// This model class doesn't have a java counterpart (yet).
	component_type "Root"		// Can not have an owner.
	properties{
		property class="ink.core:Reference"{
			type ref="example.http:F5_CacheManager"
			name "cache"
			mandatory true
		}
	}
}

Class id="F5_SecuredMetaCache" class="ink.core:InkClass" super="F5_MetaCache"{
	java_path ""
	java_mapping "No_Java"				// This model class doesn't have a java counterpart (yet).
	component_type "Root"		// Can not have an owner.
	properties{
		property class="ink.core:Reference"{
			type ref="F5_SecuredCache"
			name "cache"
			mandatory true
		}
	}
}

Class id="F5_CacheManager" class="ink.core:InkClass" super="ink.core:InkObject" abstract=true{
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

Class id="F5_StandardCache" class="ink.core:InkClass" super="example.http:F5_CacheManager" {
	java_path ""
	java_mapping "No_Java"				// This model class doesn't have a java counterpart (yet).
	component_type "Root_or_Pure_Component"		// Can have an owner.
}

Class id="F5_SecuredCache" class="ink.core:InkClass" super="example.http:F5_CacheManager" {
	java_path ""
	java_mapping "No_Java"				// This model class doesn't have a java counterpart (yet).
	component_type "Root_or_Pure_Component"		// Can have an owner.
	properties{
		property class="ink.core:StringAttribute"{
			name "publicKey"
			mandatory true
		}
	}
}


// HTTP_Client class is an instance of MetaCache meta-class.
Class id="F5_HTTP_Client" class="ink.core:InkClass" super="ink.core:InkObject" {
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

Class id="F5_PictureRetriever" class="F5_MetaCache" super="F5_HTTP_Client" {
	java_path ""
	java_mapping "No_Java"				// This model class doesn't have a java counterpart (yet).
	component_type "Root_or_Pure_Component"		// Can have an owner.
	cache class="F5_StandardCache" {
		timeToLive 600
		maxElementsInMemory 1000
	}
}

Class id="F5_NewsRetriever" class="F5_MetaCache" super="F5_HTTP_Client" {
	java_path ""
	java_mapping "No_Java"				// This model class doesn't have a java counterpart (yet).
	component_type "Root_or_Pure_Component"		// Can have an owner.
	cache class="F5_StandardCache" {
		timeToLive 10
		maxElementsInMemory 10000
	}
}

Class id="F5_StockQuoteRetriever" class="F5_MetaCache" super="F5_HTTP_Client" {
	java_path ""
	java_mapping "No_Java"				// This model class doesn't have a java counterpart (yet).
	component_type "Root_or_Pure_Component"		// Can have an owner.
	cache class="F5_StandardCache" {
		timeToLive 0
		maxElementsInMemory 0
	}
}

Class id="F5_BankBalanceRetriever" class="F5_SecuredMetaCache" super="F5_HTTP_Client" {
	java_path ""
	java_mapping "No_Java"				// This model class doesn't have a java counterpart (yet).
	component_type "Root_or_Pure_Component"		// Can have an owner.
	cache class="F5_SecuredCache" {
		timeToLive 0
		maxElementsInMemory 0
		publicKey "1324"
	}
}


Object id="F5_FastHTTP_Client" class="example.http:F5_HTTP_Client" {
	numberOfRetries 2
	timeout 2
}

Object id="F5_CNN_NewsRetriever" class="F5_NewsRetriever" super="F5_FastHTTP_Client" {
	URL "www.cnn.com/news"
}


