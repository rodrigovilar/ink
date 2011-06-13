package org.ink.tutorial1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.ink.core.vm.lang.InkObjectImpl;

public class ActiveOffersImpl<S extends ActiveOffersState> extends
InkObjectImpl<S> implements ActiveOffers {

	private A_SpecialOffer bestOffer;
	
	@Override
	public A_SpecialOffer getBestOffer(A_Subscription subscription) {
		SpecialOffer result = null;
		BaseOffer bestOffer = null;
		bestOffer = findOffer(subscription);
		if (bestOffer != null) {
			result = new SpecialOffer(bestOffer.getPromotionalPrice(subscription), bestOffer.getFreeIssues(subscription), bestOffer.getPromotionalMessage(subscription));
		}
		else {
			result = new SpecialOffer(subscription.getPriceForSubscriptionPeriod(), 0, "Sorry, No special offer for you.");
		}
		
		return result;
	}

	private BaseOffer findOffer(A_Subscription subscription) {
		BaseOffer result = null;
		Comparator<BaseOffer> bestPriceComp = new BestPriceComparator(subscription);
		List<BaseOffer> offers = new ArrayList<BaseOffer>(getState().getOffers());
		List<BaseOffer> eligibleOffers = new ArrayList<BaseOffer>();
		Iterator<BaseOffer> allOffersIterator = offers.iterator();
		while (allOffersIterator.hasNext()) {
			BaseOffer offer = allOffersIterator.next();
			if (offer.isEligible(subscription)) {
				eligibleOffers.add(offer);
			}
		}
		Collections.sort(eligibleOffers, bestPriceComp);
		result = eligibleOffers.get(0);
		return result;
	}
	
	private static class BestPriceComparator implements Comparator<BaseOffer>{
		
		private A_Subscription subscription;

		public BestPriceComparator(A_Subscription subscription) {
			super();
			this.subscription = subscription;
		}

		@Override
		public int compare(BaseOffer offer1, BaseOffer offer2) {
			return offer1.getPromotionalPrice(subscription)>offer2.getPromotionalPrice(subscription)?1:-1;
		}
		
	}

}
