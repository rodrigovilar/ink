package org.ink.language.example.videostore;

import static org.junit.Assert.assertTrue;

import org.ink.core.vm.factory.Context;
import org.ink.core.vm.factory.InkVM;
import org.junit.Test;

public class VideoStoreTest {

	@Test
	public void testVideoStore() {
		Context context = InkVM.instance().getContext();
		Videotape terminatorTape1 = context.getState("example.videostore:TerminatorTape1").getBehavior();
		Videotape terminatorTape2 = context.getState("example.videostore:TerminatorTape2").getBehavior();
		Videotape spidermanTape1 = context.getState("example.videostore:SpidermanTape1").getBehavior();
		Videotape killBillTape1 = context.getState("example.videostore:KillBillTape1").getBehavior();

		CustomerState customer1 = context.getState("example.videostore:Customer1");
		CustomerState customer2 = context.getState("example.videostore:Customer2");

		assertTrue(terminatorTape1.canRent(customer1));
		assertTrue(!terminatorTape2.canRent(customer1));

		assertTrue(!killBillTape1.canRent(customer1));
		assertTrue(killBillTape1.canRent(customer2));

		assertTrue((Boolean) spidermanTape1.reflect().getPropertyValue("hasSubtitles"));

	}
}