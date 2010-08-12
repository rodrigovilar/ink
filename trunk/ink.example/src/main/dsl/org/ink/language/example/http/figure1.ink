Class id="HTTP_Client_1" class="ink.core:InkClass" super="ink.core:InkObject"{
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

Object id="FastHTTP_Client_1" class="example.http:HTTP_Client_1" {
	numberOfRetries 2
	timeout 2
}

Object id="RobustHTTP_Client_1" class="example.http:HTTP_Client_1" {
	numberOfRetries 8
	timeout 15
}

Object id="PontisLogoRetriever_1" class="example.http:HTTP_Client_1" super="example.http:FastHTTP_Client_1" {
	URL "http://www.pontis.com/logo.bmp"
}

