package org.ink.tutorial1;

public class PercentageDiscountOfferImpl<S extends PercentageDiscountOfferState>
		extends BaseOfferImpl<S> {

	@Override
	public double getPromotionalPrice(A_Subscription subscription) {
		double result = 0.0;

		if (isEligible(subscription)) {

			result = subscription.getSubscriptionTotalPrice()
					* (100.0 - getState().getPercentage()) / 100.0;

		} else {
			result = subscription.getSubscriptionTotalPrice();
		}

		return result;
	}

	@Override
	public String getPromotionalMessage(A_Subscription subscription) {
		String result = null;

		if (isEligible(subscription)) {
			result = "Save $" + subscription.getSubscriptionTotalPrice()
					* (getState().getPercentage() / 100.0);
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
