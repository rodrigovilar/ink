package org.ink.example.shapes;

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
public class ShapesTest extends TestCase {

	private Context context = InkVM.instance().getContext();

	public void testCustomerRetrival() {
		InkObject myDrawing = context
				.getObject("example.shapes:myFirstShape2");
		assertNotNull(myDrawing);
		InkObjectState myDrawingStruct = context
				.getState("example.shapes:myFirstShape2");
		assertNotNull(myDrawingStruct);

		System.out.println("MyFirstDrawing2 = " + myDrawingStruct);

	}
}
