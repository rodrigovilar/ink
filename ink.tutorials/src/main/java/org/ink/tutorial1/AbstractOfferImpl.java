package org.ink.tutorial1;

import org.ink.core.vm.lang.InkObjectImpl;

public abstract class AbstractOfferImpl<S extends AbstractOfferState> extends
		InkObjectImpl<S> implements A_SpecialOffer {

	// Common base offer.

	@Override
	public boolean isEligible(A_Subscription subscription) {
		boolean result = false;
		if (byPassStudentOnlyConstraint(subscription)
				&& byPassValidity(subscription)) {
			result = true;
		}

		return result;
	}
	
	@Override
	public int getFreeIssues(A_Subscription subscription) {
		int result = 0;
		
		result = getState().getFreeIssues();
		
		return result;
	}

	protected boolean byPassStudentOnlyConstraint(A_Subscription subscription) {
		boolean result = false;

		if (!(getState().getStudentOnlyOffer())
				|| (subscription.getCustomer().isStudent())) {
			result = true;
		}

		return result;
	}

	protected boolean byPassValidity(A_Subscription subscription) {
		boolean result = false;

		// TODO: AAA Atzmon - Add validity check
		
		return result;
	}

}
