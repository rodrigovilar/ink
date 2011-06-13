package org.ink.tutorial1;

import java.util.Date;
import java.util.List;

public interface A_Subscription {

	// The customer
	A_Customer getCustomer();

	// Subscription is to this magazine
	A_Product getMagazine();

	// List price
	double getPriceForSubscriptionPeriod();

	// 1 year, 2 years, etc.
	int getPeriods();

	// Start period of the subscription
	Date getPeriodStart();

	// When the customer commits to the subscription, call commit()
	boolean commit();

	// Indicates if this subscription is in effect or just draft
	boolean isCommitted();


	// Find the best promotional offer
	A_SpecialOffer getBestOffer();

}
