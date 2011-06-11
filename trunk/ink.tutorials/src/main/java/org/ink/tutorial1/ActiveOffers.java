package org.ink.tutorial1;

import org.ink.core.vm.lang.InkObject;

public interface ActiveOffers extends InkObject {

	public A_SpecialOffer getBestOffer(A_Subscription subscription);
	
}
