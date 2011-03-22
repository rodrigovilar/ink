package org.ink.tutorial1;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;


public class TestTutorial1Test {

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
		Assert.assertEquals(98.0*5, subscription.getPrice());
		
	}
	
	@Test
	public void testFixedPercentageOffers() {
		Magazine magazine1 = new Magazine("1", "IEEE Software", 98.0);
		Customer customer1 = new Customer("Atzmon Hen-tov", "atzmon.hentov@gmail.com", "***********6238", true);
		Date startDate = new Date(2012,1,1);
		
		Subscription subscription = new Subscription(magazine1, customer1, 5, startDate);
		
		double price = subscription.getPrice();
		double promotionalPrice = subscription.getPromotionalPrice();
		String promotionalMessage = subscription.getPromotionalMessage();
		Assert.assertEquals(98.0*5, price);
		Assert.assertEquals(98.0*5*0.4, promotionalPrice);
		Assert.assertEquals("Save $294.0 and get 3 issues for free.", promotionalMessage);
	}
	
}
