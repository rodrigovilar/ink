package org.ink.example.http;

import junit.framework.TestCase;

import org.ink.core.vm.factory.Context;
import org.ink.core.vm.factory.InkVM;
import org.ink.core.vm.lang.InkObject;
import org.ink.language.example.http.F1_HTTP_Client;

/**
 * @author Atzmon Hen-tov
 */
public class HTTPTest extends TestCase {

	private final Context context = InkVM.instance().getContext();

	public void testFigure1Test() {
		String prefix = "F1";
		System.out.println("Figure1");
		System.out.println("-------");
		showHTTP_Client(prefix);
		showFastHTTP_Client(prefix);
		showRobustHTTP_Client(prefix);
		showPontisLogoRetriever_Client(prefix);
	}

	public void testFirstGCTest() {
		F1_HTTP_Client httpClient = (F1_HTTP_Client)context
		.getObject("example.http:F1_PontisLogoRetriever");
		System.out.println(httpClient.sendReceive());
		
	}
	
	public void testFigure3Test() {
		String prefix = "F3";
		System.out.println("Figure3");
		System.out.println("-------");
		showMetaCache(prefix);
		showHTTP_Client(prefix);
		showFastHTTP_Client(prefix);
		showRobustHTTP_Client(prefix);
		showPontisLogoRetriever_Client(prefix);
	}
	
	public void testFigure4Test() {
		String prefix = "F4";
		System.out.println("Figure4");
		System.out.println("-------");
		showMetaCache(prefix);
		InkObject o1 = context
		.getObject("example.http:F4_CNN_NewsRetriever");
		assertNotNull(o1);	
		System.out.println(o1);
	}
	
	public void testFigure5Test() {
		String prefix = "F5";
		System.out.println("Figure5");
		System.out.println("-------");
		InkObject httpClientClass = context
		.getObject("example.http:F5_BankBalanceRetriever");
		assertNotNull(httpClientClass);

		System.out.println(httpClientClass);	
		}
	


	private void showHTTP_Client(String prefix) {
		InkObject httpClientClass = context
		.getObject("example.http:" + prefix + "_" + "HTTP_Client");
		assertNotNull(httpClientClass);

		System.out.println("HTTP_Client = ");
		System.out.println(httpClientClass);

	}

	private void showFastHTTP_Client(String prefix) {
		InkObject httpClient = context
		.getObject("example.http:" + prefix + "_" + "FastHTTP_Client");
		assertNotNull(httpClient);

		System.out.println("FastHTTP_Client = ");
		System.out.println(httpClient);

	}

	private void showRobustHTTP_Client(String prefix) {
		InkObject httpClient = context
		.getObject("example.http:" + prefix + "_" + "RobustHTTP_Client");
		assertNotNull(httpClient);

		System.out.println("RobustHTTP_Client = ");
		System.out.println(httpClient);

	}

	private void showPontisLogoRetriever_Client(String prefix) {
		InkObject httpClient = context
		.getObject("example.http:"  + prefix + "_" + "PontisLogoRetriever");
		assertNotNull(httpClient);

		System.out.println("PontisLogoRetriever = ");
		System.out.println(httpClient);

	}

	private void showMetaCache(String prefix) {
		InkObject httpClient = context
		.getObject("example.http:"  + prefix + "_" + "MetaCache");
		assertNotNull(httpClient);

		System.out.println("MetaCache = ");
		System.out.println(httpClient);

	}
}
