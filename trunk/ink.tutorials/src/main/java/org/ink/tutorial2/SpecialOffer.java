package org.ink.tutorial2;

public class SpecialOffer implements A_SpecialOffer {

	double promotionalPrice = 0;
	int freeIssues = 0;
	String promotionalMessage = null;
	BaseOffer bestOffer = null;
	
	public SpecialOffer(double promotionalPrice, int freeIssues, String promotionalMessage, BaseOffer bestOffer) {
		this.promotionalPrice = promotionalPrice;
		this.freeIssues = freeIssues;
		this.promotionalMessage = promotionalMessage;
		this.bestOffer = bestOffer;
	}
	
	@Override
	public double getPromotionalPrice() {
		return promotionalPrice;
	}

	@Override
	public int getFreeIssues() {
		return freeIssues;
	}

	@Override
	public String getPromotionalMessage() {
		return promotionalMessage;
	}

	@Override
	public String register(BaseRegistrationForm registrationForm) {
		String result = null;
		
		if (bestOffer == null) {
			result = "You are not eligible to register";
		}
		else {
			result = bestOffer.register(registrationForm);
		}
		
		return result;
	}

}
