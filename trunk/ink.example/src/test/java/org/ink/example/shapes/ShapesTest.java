package org.ink.example.shapes;

import junit.framework.TestCase;

import org.ink.core.vm.factory.Context;
import org.ink.core.vm.factory.InkVM;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.InkObjectState;

/**
 * @author Atzmon Hen-tov
 */
public class ShapesTest extends TestCase {

	private final Context context = InkVM.instance().getContext();

	public void testCustomerRetrival() {
		//InkObject myDrawingClass = context.getObject("example.shapes:Drawing");
		InkObject myDrawing = context.getObject("example.shapes:myFirstShape2");
		assertNotNull(myDrawing);
		InkObjectState myDrawingStruct = context.getState("example.shapes:myFirstShape2");
		assertNotNull(myDrawingStruct);

		System.out.println("MyFirstDrawing2 = " + myDrawingStruct);

	}
}
