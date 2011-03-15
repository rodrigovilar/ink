package org.ink.tutorial1;

public class FixedPercentageDiscountOfferImpl<S extends FixedPercentageDiscountOfferState>
		extends AbstractOfferImpl<S> implements A_SpecialOffer {

	@Override
	public double getPromotionalPrice(A_Subscription subscription) {
		double result = 0.0;

		if (isEligible(subscription)) {

			result = subscription.getPrice()
					* (100.0 - getState().getPercentage());

		} else {
			result = subscription.getPrice();
		}

		return result;
	}

	@Override
	public String getPromotionalMessage(A_Subscription subscription) {
		String result = null;

		if (isEligible(subscription)) {
			result = "Save $" + subscription.getPrice()
					* (getState().getPercentage());
			if (getFreeIssues(subscription) > 0) {
				result = result + " and get " + getFreeIssues(subscription)
						+ " issues for free.";
			} else {
				result = result + ".";
			}
		}

		return result;
	}

}
