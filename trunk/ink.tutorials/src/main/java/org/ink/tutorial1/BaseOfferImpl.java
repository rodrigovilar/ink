package org.ink.tutorial1;

import java.util.Date;

import org.ink.core.vm.lang.InkObjectImpl;

public abstract class BaseOfferImpl<S extends BaseOfferState> extends
		InkObjectImpl<S> implements BaseOffer {

	// Common base offer.

	@Override
	public boolean isEligible(A_Subscription subscription) {
		boolean result = false;
		if (
				byPassValidity(subscription)
				&& byPassStudentOnlyConstraint(subscription) 
				&& byPassPeriodsConstraint(subscription)) {
			result = true;
		}

		return result;
	}
	
	@Override
	public int getFreeIssues(A_Subscription subscription) {
		int result = 0;
		
		if (getState().getFreeIssues() != null) {
			result = getState().getFreeIssues();
		}
		
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
	
	protected boolean byPassPeriodsConstraint(A_Subscription subscription) {
		boolean result = false;
		
		if (subscription.getPeriods() >= getState().getConditionForPeriodsSigned()) {
			result = true;
		}
		
		return result;
	}

	protected boolean byPassValidity(A_Subscription subscription) {
		boolean result = false;

		if (getState().getValidUntil().after(now())) {
			result = true;
		}
		
		return result;
	}

	private Date now() {
		Date result = null;
		
		result = new Date();
		
		return result;
	}
	
}
