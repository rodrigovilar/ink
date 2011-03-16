package org.ink.language.example.http;

import org.ink.core.vm.lang.InkObjectImpl;


public class F1_HTTP_ClientImpl extends InkObjectImpl<F1_HTTP_ClientState> implements F1_HTTP_Client  {

	@Override
	public String sendReceive() {
		String result = "";
		if (getState().getUnifiedResourceLocator().contains("facebook")) {
			result = "access temporarily forbidden";
		}
		return result;
	}

}
