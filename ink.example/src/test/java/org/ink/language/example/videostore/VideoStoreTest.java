package org.ink.language.example.videostore;

import static org.junit.Assert.assertTrue;

import org.ink.core.vm.factory.Context;
import org.ink.core.vm.factory.InkVM;
import org.ink.core.vm.lang.InkClassState;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.JavaMapping;
import org.ink.core.vm.mirror.editor.ObjectEditor;
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

	@Test
	public void testMOP() {
		Context context = InkVM.instance().getContext();
		CustomerState customer1 = context.getState("example.videostore:Customer1");
		InkObjectState videotape = context.getState("example.videostore:Videotape");

		InkObjectState dynamicVideotape = videotape.cloneState();
		ObjectEditor dynamicVideotapeEditor = dynamicVideotape.reflect().edit();
		dynamicVideotapeEditor.setSuper(videotape);
		dynamicVideotapeEditor.setId("example.videostore:Shrek");
		dynamicVideotapeEditor.setPropertyValue(InkClassState.p_java_path, "");
		dynamicVideotapeEditor.setPropertyValue(InkClassState.p_java_mapping, JavaMapping.No_Java);
		dynamicVideotapeEditor.setPropertyValue(MovieState.p_title, "Shrek (2001)");
		dynamicVideotapeEditor.setPropertyValue(MovieState.p_rating, "PG");
		dynamicVideotapeEditor.save();
		context.register(dynamicVideotape);

		Movie shrek = context.getState("example.videostore:Shrek").getBehavior();

		VideotapeState shrekTape1State = shrek.newInstance();
		shrekTape1State.setIsRented(false);
		shrekTape1State.setRenter(null);
		Videotape shrekTape1 = shrekTape1State.getBehavior();
		assertTrue(shrekTape1.canRent(customer1));

		InkObjectState restrictedVideotape = context.getState("example.videostore:RestrictedVideotape");
		InkObjectState dynamicRestrictedVideotape = restrictedVideotape.cloneState();
		ObjectEditor dynamicRestrictedVideotapeEditor = dynamicRestrictedVideotape.reflect().edit();
		dynamicRestrictedVideotapeEditor.setSuper(restrictedVideotape);
		dynamicRestrictedVideotapeEditor.setId("example.videostore:FightClub");
		dynamicRestrictedVideotapeEditor.setPropertyValue(InkClassState.p_java_path, "");
		dynamicRestrictedVideotapeEditor.setPropertyValue(InkClassState.p_java_mapping, JavaMapping.No_Java);
		dynamicRestrictedVideotapeEditor.setPropertyValue(MovieState.p_title, "Fight Club (1999)");
		dynamicRestrictedVideotapeEditor.setPropertyValue(RestrictedMovieState.p_minimumage, 21);
		dynamicRestrictedVideotapeEditor.save();
		context.register(dynamicRestrictedVideotape);

		Movie fightClub = context.getState("example.videostore:FightClub").getBehavior();

		VideotapeState fightClubTape1State = fightClub.newInstance();
		fightClubTape1State.setIsRented(false);
		fightClubTape1State.setRenter(null);
		Videotape fightClubTape1 = fightClubTape1State.getBehavior();
		assertTrue(!fightClubTape1.canRent(customer1));
	}
}