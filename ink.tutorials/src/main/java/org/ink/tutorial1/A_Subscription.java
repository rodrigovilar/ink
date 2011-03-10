package org.ink.tutorial1;

import java.util.Date;
import java.util.List;

public interface A_Subscription {

	// List price
	double getPrice();

	// Subscription is to this magazine
	A_Product getMagazine();

	// 1 year, 2 years, etc.
	int getPeriod();

	// Start period of the subscription
	Date getPeriodStart();

	// Indicates if this subscription is in effect or just draft
	boolean isCommitted();

	// When the customer commits to the subscription, call commit()
	boolean commit();

	// The customer
	A_Customer getCustomer();

	double getPromotionalPrice();
	int getFreePeriods();
	String getPromotionalMessage();

}
