package org.ink.tutorial3;

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
		Videotape terminatorTape1 = context.getObject("ink.tutorial3:TerminatorTape1");
		Videotape terminatorTape2 = context.getObject("ink.tutorial3:TerminatorTape2");
		Videotape spidermanTape1 = context.getObject("ink.tutorial3:SpidermanTape1");
		Videotape killBillTape1 = context.getObject("ink.tutorial3:KillBillTape1");

		CustomerState customer1 = context.getState("ink.tutorial3:Customer1");
		CustomerState customer2 = context.getState("ink.tutorial3:Customer2");

		assertTrue(terminatorTape1.canRent(customer1));
		assertTrue(!terminatorTape2.canRent(customer1));

		assertTrue(!killBillTape1.canRent(customer1));
		assertTrue(killBillTape1.canRent(customer2));

		assertTrue((Boolean) spidermanTape1.reflect().getPropertyValue("hasSubtitles"));
	}

	@Test
	public void testMOP() {
		CustomerState customer1 = InkVM.instance().getContext().getState("ink.tutorial3:Customer1");

		createMovie("ink.tutorial3:Shrek", "Shrek (2001)", "PG");
		Videotape shrekTape1 = createTape("ink.tutorial3:Shrek");
		assertTrue(shrekTape1.canRent(customer1));

		createRestrictedMovie("ink.tutorial3:FightClub", "Fight Club (1999)", 21);
		Videotape fightClubTape1 = createTape("ink.tutorial3:FightClub");
		assertTrue(!fightClubTape1.canRent(customer1));
	}

	private Videotape createTape(String videotapeId) {
		VideotapeState newTapeState = InkVM.instance().getContext().newInstance(videotapeId);
		newTapeState.setIsRented(false);
		newTapeState.setRenter(null);
		return newTapeState.getBehavior();
	}

	public void createRestrictedMovie(String id, String title, int minimumAge) {
		Context context = InkVM.instance().getContext();
		InkObjectState restrictedVideotape = context.getState("ink.tutorial3:RestrictedVideotape");
		ObjectEditor dynamicRestrictedVideotapeEditor = restrictedVideotape.reflect().edit().createDescendent(id);
		dynamicRestrictedVideotapeEditor.setPropertyValue(InkClassState.p_java_path, "");
		dynamicRestrictedVideotapeEditor.setPropertyValue(InkClassState.p_java_mapping, JavaMapping.No_Java);
		dynamicRestrictedVideotapeEditor.setPropertyValue(MovieState.p_title, title);
		dynamicRestrictedVideotapeEditor.setPropertyValue(RestrictedMovieState.p_minimumage, minimumAge);
		dynamicRestrictedVideotapeEditor.save();
		context.register(dynamicRestrictedVideotapeEditor.getEditedState());
	}

	public void createMovie(String id, String title, String rating) {
		Context context = InkVM.instance().getContext();
		InkObjectState videotape = context.getState("ink.tutorial3:Videotape");
		ObjectEditor dynamicVideotapeEditor = videotape.reflect().edit().createDescendent(id);
		dynamicVideotapeEditor.setPropertyValue(InkClassState.p_java_path, "");
		dynamicVideotapeEditor.setPropertyValue(InkClassState.p_java_mapping, JavaMapping.No_Java);
		dynamicVideotapeEditor.setPropertyValue(MovieState.p_title, title);
		dynamicVideotapeEditor.setPropertyValue(MovieState.p_rating, rating);
		dynamicVideotapeEditor.save();
		context.register(dynamicVideotapeEditor.getEditedState());
	}
}