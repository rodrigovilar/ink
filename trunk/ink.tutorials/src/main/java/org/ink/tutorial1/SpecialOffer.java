package org.ink.tutorial1;

public class SpecialOffer implements A_SpecialOffer {

	double promotionalPrice = 0;
	int freeIssues = 0;
	String promotionalMessage = null;
	
	public SpecialOffer(double promotionalPrice, int freeIssues, String promotionalMessage) {
		this.promotionalPrice = promotionalPrice;
		this.freeIssues = freeIssues;
		this.promotionalMessage = promotionalMessage;
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

}
