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
		Videotape terminatorTape1 = context.getObject("example.videostore:TerminatorTape1");
		Videotape terminatorTape2 = context.getObject("example.videostore:TerminatorTape2");
		Videotape spidermanTape1 = context.getObject("example.videostore:SpidermanTape1");
		Videotape killBillTape1 = context.getObject("example.videostore:KillBillTape1");

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
		CustomerState customer1 = InkVM.instance().getContext().getState("example.videostore:Customer1");

		createMovie("example.videostore:Shrek", "Shrek (2001)", "PG");
		Videotape shrekTape1 = createTape("example.videostore:Shrek");
		assertTrue(shrekTape1.canRent(customer1));

		createRestrictedMovie("example.videostore:FightClub", "Fight Club (1999)", 21);
		Videotape fightClubTape1 = createTape("example.videostore:FightClub");
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
		InkObjectState restrictedVideotape = context.getState("example.videostore:RestrictedVideotape");
		ObjectEditor dynamicRestrictedVideotapeEditor = createDescendent(restrictedVideotape, id);
		dynamicRestrictedVideotapeEditor.setPropertyValue(InkClassState.p_java_path, "");
		dynamicRestrictedVideotapeEditor.setPropertyValue(InkClassState.p_java_mapping, JavaMapping.No_Java);
		dynamicRestrictedVideotapeEditor.setPropertyValue(MovieState.p_title, title);
		dynamicRestrictedVideotapeEditor.setPropertyValue(RestrictedMovieState.p_minimumage, minimumAge);
		dynamicRestrictedVideotapeEditor.save();
		context.register(dynamicRestrictedVideotapeEditor.getEditedState());
	}

	public void createMovie(String id, String title, String rating) {
		Context context = InkVM.instance().getContext();
		InkObjectState videotape = context.getState("example.videostore:Videotape");
		ObjectEditor dynamicVideotapeEditor = createDescendent(videotape, id);
		dynamicVideotapeEditor.setPropertyValue(InkClassState.p_java_path, "");
		dynamicVideotapeEditor.setPropertyValue(InkClassState.p_java_mapping, JavaMapping.No_Java);
		dynamicVideotapeEditor.setPropertyValue(MovieState.p_title, title);
		dynamicVideotapeEditor.setPropertyValue(MovieState.p_rating, rating);
		dynamicVideotapeEditor.save();
		context.register(dynamicVideotapeEditor.getEditedState());
	}

	public ObjectEditor createDescendent(InkObjectState superState, String descendentId) {
		InkObjectState descendentState = superState.cloneState();
		ObjectEditor descendentEditor = descendentState.reflect().edit();
		descendentEditor.setSuper(superState);
		descendentEditor.setId(descendentId);
		return descendentEditor;
	}
}