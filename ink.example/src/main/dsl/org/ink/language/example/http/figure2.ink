Class id="MetaCache_2" class="ink.core:InkClass" super="ink.core:InkClass"{
	java_path ""
	java_mapping "No_Java"				// This model class doesn't have a java counterpart (yet).
	component_type "Root"		// Can not have an owner.
	properties{
		property class="ink.core:Reference"{
			type ref="example.http:Cache_2"
			name "cache"
			mandatory true
		}
	}
}

Class id="CacheManager_2" class="ink.core:InkClass" super="ink.core:InkObject" abstract="true"{
	java_path ""
	java_mapping "No_Java"				// This model class doesn't have a java counterpart (yet).
	component_type "Root_or_Pure_Component"		// Can have an owner.
}

Class id="StandardCache_2" class="ink.core:InkClass" super="ink.core:InkObject" abstract="true"{
	java_path ""
	java_mapping "No_Java"				// This model class doesn't have a java counterpart (yet).
	component_type "Root_or_Pure_Component"		// Can have an owner.
	properties{
		property class="ink.core:IntAttribute"{
			name "maxElementsInMemory"
			mandatory true
		}
		property class="ink.core:IntAttribute"{
			name "timeToLive"
			mandatory true
		}
	}
}



Class id="HTTP_Client_2" class="ink.core:InkClass" super="ink.core:InkObject"{
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
	cache class="example.http:StandardCache" {
		maxElementsInMemory 500
		timeToLive 60
	}
}

Object id="FastHTTP_Client_2" class="example.http:HTTP_Client_2" {
	numberOfRetries 2
	timeout 2
}

Object id="RobustHTTP_Client_2" class="example.http:HTTP_Client_2" {
	numberOfRetries 8
	timeout 15
}

Object id="PontisLogoRetriever_2" class="example.http:HTTP_Client_2" super="example.http:FastHTTP_Client_2" {
	URL "http://www.pontis.com/logo.bmp"
	timeout 2
}

