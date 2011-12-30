package org.ink.tutorial2;

public class Magazine implements A_Product {

	String ID = null;
	String name = null;
	Double price = 0.0;
	
	public Magazine(String id, String name, Double price) {
		this.ID = id;
		this.name = name;
		this.price = price;
	}
	
	@Override
	public String getID() {
		return ID;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Double getPrice() {
		return price;
	}

}
