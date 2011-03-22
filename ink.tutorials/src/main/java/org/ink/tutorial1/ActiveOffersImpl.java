package org.ink.tutorial1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.ink.core.vm.lang.InkObjectImpl;

public class ActiveOffersImpl<S extends ActiveOffersState> extends
InkObjectImpl<S> implements ActiveOffers {

	@Override
	public boolean isEligible(A_Subscription subscription) {
		boolean result = false;
		A_SpecialOffer offer = findOffer(subscription);
		if (offer != null) {
			result = offer.isEligible(subscription);
		}
		return result;
	}

	@Override
	public double getPromotionalPrice(A_Subscription subscription) {
		double result = 0.0;
		A_SpecialOffer offer = findOffer(subscription);
		if (offer != null) {
			result = offer.getPromotionalPrice(subscription);
		}
		return result;
	}

	@Override
	public int getFreeIssues(A_Subscription subscription) {
		int result = 0;
		A_SpecialOffer offer = findOffer(subscription);
		if (offer != null) {
			result = offer.getFreeIssues(subscription);
		}
		return result;
	}

	@Override
	public String getPromotionalMessage(A_Subscription subscription) {
		String result = null;
		A_SpecialOffer offer = findOffer(subscription);
		if (offer != null) {
			result = offer.getPromotionalMessage(subscription);
		}
		return result;
	}

	private A_SpecialOffer findOffer(A_Subscription subscription) {
		A_SpecialOffer result = null;
		Comparator<A_SpecialOffer> bestPriceComp = new BestPriceComparator(subscription);
		List<A_SpecialOffer> offers = new ArrayList<A_SpecialOffer>(getState().getOffers());
		Collections.sort(offers, bestPriceComp);
		result = offers.get(0);
		return result;
	}
	
	private static class BestPriceComparator implements Comparator<A_SpecialOffer>{
		
		private A_Subscription subscription;

		public BestPriceComparator(A_Subscription subscription) {
			super();
			this.subscription = subscription;
		}

		@Override
		public int compare(A_SpecialOffer offer1, A_SpecialOffer offer2) {
			return offer1.getPromotionalPrice(subscription)>offer2.getPromotionalPrice(subscription)?1:-1;
		}
		
		
		
		
		
	}

}
