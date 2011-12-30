package org.ink.tutorial2;

import java.util.Date;

import org.ink.core.vm.lang.InkClass;
import org.ink.core.vm.lang.InkObjectImpl;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.mirror.Mirror;

public abstract class BaseOfferImpl<S extends BaseOfferState> extends
		InkObjectImpl<S> implements BaseOffer {

	// Common base offer.

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

	@Override
	public String register(BaseRegistrationForm registrationForm) {
		String result = null;
		boolean ok = true;
		
		
		// Validate this is the right form type
		Mirror mirror = null;
		ClassMirror classMirror = null;
		
		// TODO: The following line should work
		//InkClass requiredClass = getState().getRegistrationForm();
		
		Object rc = reflect().getPropertyValue("registrationForm");
		InkClass requiredClass = (InkClass)rc;
		
		if (requiredClass != null) {
			ClassMirror requiredClassMirror = (ClassMirror)(requiredClass).reflect();

			if (registrationForm == null) {
				ok = false;
			}
			else {
				mirror = registrationForm.reflect();
				classMirror = mirror.getClassMirror();
				if (!classMirror.isSubClassOf(requiredClassMirror)) {
					ok = false;
				}
			}
		}
		
		if (!ok) {
			throw new RuntimeException("Bad registration form.");
		}
		else {
			result = registrationForm.prepareReceipt();
		}
		
		return result;
	}
}
