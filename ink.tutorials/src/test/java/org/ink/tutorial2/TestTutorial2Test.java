package org.ink.tutorial2;

import java.util.Date;

import junit.framework.Assert;

import org.ink.core.vm.factory.Context;
import org.ink.core.vm.factory.InkVM;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.mirror.editor.ObjectEditor;
import org.junit.Test;


public class TestTutorial2Test {

	private final Context context = InkVM.instance().getContext();

	@Test
	public void testSanityTest() {
		assert(true);
	}

	@Test
	public void testMagazineTest() {
		Magazine magazine1 = new Magazine("1", "IEEE Software", 98.0);
		Magazine magazine2 = new Magazine("2", "ACM Communications", 128.2);

		Assert.assertEquals(magazine1.getPrice(), 98.0);
		Assert.assertEquals(magazine2.getPrice(), 128.2);
	}

	@Test
	public void testSubscriptionTest() {
		Magazine magazine1 = new Magazine("1", "IEEE Software", 98.0);
		Customer customer1 = new Customer("Atzmon Hen-tov", "atzmon.hentov@gmail.com", "***********6238", true);
		Date startDate = new Date(2012,1,1);

		Subscription subscription = new Subscription(magazine1, customer1, 5, startDate);
		Assert.assertEquals(98.0*5, subscription.getPriceForSubscriptionPeriod());

	}

	@Test
	public void testPercentageOffers() {
		Magazine magazine1 = new Magazine("1", "IEEE Software", 98.0);
		Customer customer1 = new Customer("Atzmon Hen-tov", "atzmon.hentov@gmail.com", "***********6238", true);
		Date startDate = new Date(2012,1,1);

		Subscription subscription = new Subscription(magazine1, customer1, 5, startDate);

		double price = subscription.getPriceForSubscriptionPeriod();
		double promotionalPrice = subscription.getBestOffer().getPromotionalPrice();
		String promotionalMessage = subscription.getBestOffer().getPromotionalMessage();
		Assert.assertEquals(98.0*5, price);
		Assert.assertEquals(98.0*5*0.4, promotionalPrice);
		Assert.assertEquals("Save $294.0 and get 3 issues for free.", promotionalMessage);
	}

	@Test
	public void testRegistration() {
		Magazine magazine1 = new Magazine("1", "IEEE Software", 98.0);
		Customer customer1 = new Customer("Atzmon Hen-tov", "atzmon.hentov@gmail.com", "***********6238", true);
		Date startDate = new Date(2012,1,1);

		Subscription subscription = new Subscription(magazine1, customer1, 1, startDate);

		// Create a registration form object.
		BaseRegistrationForm registrationForm = (BaseRegistrationForm)context.newInstance("ink.tutorial2:Students_registration_form").getBehavior();

		// Edit the object using Mirror.
		Mirror mirror = registrationForm.reflect();
		ObjectEditor editor = mirror.edit();
		editor.setPropertyValue("firstName", "Lior");
		editor.setPropertyValue("lastName", "Schachter");
		editor.setPropertyValue("email", "lior@ink.org");
		editor.save();

		A_SpecialOffer bestOffer = subscription.getBestOffer();
		String registrationReceipt = bestOffer.register(registrationForm);
		Assert.assertEquals(registrationReceipt, "firstName='Lior',lastName='Schachter',email='lior@ink.org'");

	}

}
