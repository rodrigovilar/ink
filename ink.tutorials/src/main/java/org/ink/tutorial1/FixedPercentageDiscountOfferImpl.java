package org.ink.tutorial1;

public class FixedPercentageDiscountOfferImpl<S extends FixedPercentageDiscountOfferState>
		extends BaseOfferImpl<S> implements BaseOffer {

	int nInvocations = 0;

	@Override
	public double getPromotionalPrice(A_Subscription subscription) {
		double result = 0.0;

		countInvocations();

		if (isEligible(subscription)) {

			result = subscription.getPrice()
					* (100.0 - getState().getPercentage()) / 100.0;

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

	private void countInvocations() {
		nInvocations++;
		if (nInvocations == 1) {
			System.out.println("FixedPercentageDiscountOffer.getPromotionalPrice invoked for the first time for instance '" + reflect().getId() + "'."); 
		}
		else	if (nInvocations == 2) {
			System.out.println("FixedPercentageDiscountOffer.getPromotionalPrice invoked for the second time for instance '" + reflect().getId() + "'.");
		}
		else {
			System.out.println("FixedPercentageDiscountOffer.getPromotionalPrice invoked " + nInvocations +" times for instance '" + reflect().getId() + "'.");
		}
	}
}
