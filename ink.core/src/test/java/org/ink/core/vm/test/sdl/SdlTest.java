package org.ink.core.vm.test.sdl;

import java.io.IOException;
import java.io.InputStreamReader;

import junit.framework.TestCase;

import org.ikayzo.sdl.SDLParseException;
import org.ikayzo.sdl.Tag;
import org.ink.core.vm.factory.Context;
import org.ink.core.vm.factory.InkVM;
import org.ink.core.vm.factory.VMMain;
import org.ink.core.vm.lang.InkClass;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.serialization.InkReader;

public class SdlTest extends TestCase{
	
	private Context context = InkVM.instance().getContext();
	
	@Override
	protected void tearDown() throws Exception {
		VMMain.stop();
	}
	
	public void testParsing() throws Exception{
		try {
			new Tag("root").read(new InputStreamReader(
					SdlTest.class.getResourceAsStream("test.ink"),
					"UTF8"));
		} catch(IOException ioe) {
			ioe.printStackTrace();
			assertFalse(true);
		} catch(SDLParseException spe) {
			spe.printStackTrace();
			assertFalse(true);
		}
		InkClass c = context.getObject("ink.core:InkReader");
		InkReader<Tag> reader = c.newInstance().getBehavior();
		InkObjectState[] result = reader.read(SdlTest.class.getResource("test.ink"));
		assertTrue(result.length == 2);
	}
	
}
