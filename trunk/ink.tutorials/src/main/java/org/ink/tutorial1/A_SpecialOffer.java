package org.ink.tutorial1;

public interface A_SpecialOffer {
	// Is this subscription eligible to the special offer?
	boolean isEligible(A_Subscription subscription);
	
	double getPromotionalPrice(A_Subscription subscription);
	int getFreePeriods(A_Subscription subscription);
	String getPromotionalMessage(A_Subscription subscription);
}
