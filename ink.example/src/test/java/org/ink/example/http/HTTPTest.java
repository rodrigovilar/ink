package org.ink.example.http;

import junit.framework.TestCase;

import org.ink.core.vm.constraints.ValidationContext;
import org.ink.core.vm.factory.Context;
import org.ink.core.vm.factory.InkVM;
import org.ink.core.vm.factory.internal.CoreNotations;
import org.ink.core.vm.lang.InkClass;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.mirror.editor.ObjectEditor;
import org.ink.core.vm.traits.Trait;

/**
 * @author Atzmon Hen-tov
 */
public class HTTPTest extends TestCase {

	private Context context = InkVM.instance().getContext();

	public void testFigure1Test() {
		System.out.println("Figure1");
		System.out.println("-------");
		testShowHTTP_ClientTest();
		testShowFastHTTP_ClientTest();
		testShowRobustHTTP_ClientTest();
		testShowPontisLogoRetriever_ClientTest();
	}
	
	private void testShowHTTP_ClientTest() {
		InkObject httpClientClass = context
				.getObject("example.http:HTTP_Client_1");
		assertNotNull(httpClientClass);

		System.out.println("HTTP_Client = ");
		System.out.println(httpClientClass);

	}
	
	private void testShowFastHTTP_ClientTest() {
		InkObject httpClient = context
				.getObject("example.http:FastHTTP_Client_1");
		assertNotNull(httpClient);

		System.out.println("FastHTTP_Client = ");
		System.out.println(httpClient);

	}
	
	private void testShowRobustHTTP_ClientTest() {
		InkObject httpClient = context
				.getObject("example.http:RobustHTTP_Client_1");
		assertNotNull(httpClient);

		System.out.println("RobustHTTP_Client = ");
		System.out.println(httpClient);

	}
	
	private void testShowPontisLogoRetriever_ClientTest() {
		InkObject httpClient = context
				.getObject("example.http:PontisLogoRetriever_1");
		assertNotNull(httpClient);

		System.out.println("PontisLogoRetriever = ");
		System.out.println(httpClient);

	}
}
