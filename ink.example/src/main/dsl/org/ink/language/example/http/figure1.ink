Class id="F1_HTTP_Client" class="ink.core:InkClass" super="ink.core:InkObject"{
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

Object id="F1_FastHTTP_Client" class="example.http:F1_HTTP_Client" {
	numberOfRetries 2
	timeout 2
}

Object id="F1_RobustHTTP_Client" class="example.http:F1_HTTP_Client" {
	numberOfRetries 8
	timeout 15
}

Object id="F1_PontisLogoRetriever" class="example.http:F1_HTTP_Client" super="example.http:F1_FastHTTP_Client" {
	URL "http://www.pontis.com/logo.bmp"
}

