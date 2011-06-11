package org.ink.tutorial1;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ink.core.vm.factory.InkVM;

public class Subscription implements A_Subscription {

	A_Product theMagazine;
	A_Customer theCustomer;
	int periods;
	Date periodStart;
	boolean isCommitted;
	
	public Subscription(Magazine product, A_Customer customer, int periods, Date periodStart) {
		this.theMagazine = product;
		this.theCustomer = customer;
		this.periods = periods;
		this.isCommitted = false;
	}
	
	@Override
	public double getSubscriptionTotalPrice() {
		return theMagazine.getPrice() * getPeriods();
	}


	@Override
	public A_Product getMagazine() {
		return theMagazine;
	}

	@Override
	public int getPeriods() {
		return periods;
	}
	
	public void setPeriods(int periods) {
		this.periods = periods;
	}

	@Override
	public Date getPeriodStart() {
		return periodStart;
	}
	
	public void setPeriodStart(Date periodStart) {
		this.periodStart = periodStart;
	}

	@Override
	public boolean isCommitted() {
		return this.isCommitted;
	}
	
	public boolean commit() {
		boolean result = false;
		
		if (!isCommitted()) {
			isCommitted = true;
			result = true;
		}
		
		return result;
	}
	
	@Override
	public A_Customer getCustomer() {
		return theCustomer;
	}
	
	
	public A_SpecialOffer getBestOffer() {
		A_SpecialOffer bestOffer;
		ActiveOffers offers = InkVM.instance().getContext().getState("ink.tutorial1:Active_offers").getBehavior();
		bestOffer = offers.getBestOffer(this);
		return bestOffer;
	}

}
