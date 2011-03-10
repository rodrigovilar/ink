package org.ink.tutorial1;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Subscription implements A_Subscription {

	A_Product theMagazine;
	A_Customer theCustomer;
	int period;
	Date periodStart;
	boolean isCommitted;
	
	public Subscription(Magazine product, A_Customer customer, int period, Date periodStart) {
		this.theMagazine = product;
		this.theCustomer = customer;
		this.period = period;
		this.isCommitted = false;
	}
	
	@Override
	public double getPrice() {
		return theMagazine.getPrice();
	}


	@Override
	public A_Product getMagazine() {
		return theMagazine;
	}

	@Override
	public int getPeriod() {
		return period;
	}
	
	public void setPeriod(int period) {
		this.period = period;
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
	
	@Override
	public double getPromotionalPrice() {
		return getPrice();
	}

	@Override
	public int getFreePeriods() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getPromotionalMessage() {
		String result = null;
	
		return result;
	}

}
