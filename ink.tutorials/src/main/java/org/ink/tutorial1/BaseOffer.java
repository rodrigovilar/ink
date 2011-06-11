package org.ink.tutorial1;

import org.ink.core.vm.lang.InkObject;

public interface BaseOffer extends InkObject {

	public boolean isEligible(A_Subscription subscription);
	public double getPromotionalPrice(A_Subscription subscription);
	public int getFreeIssues(A_Subscription subscription);
	public String getPromotionalMessage(A_Subscription subscription);

}
