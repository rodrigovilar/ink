package org.ink.example.customer;

import org.ink.core.vm.lang.Struct;

/**
 * @author Lior Schachter
 */
public interface Address extends Struct{
	
	public static final byte p_city = 0;
	public static final byte p_street = 1;
	public static final byte p_number = 2;
	
	public String getCity();
	public void setCity(String value);
	
	public String getStreet();
	public void setStreet(String value);
	
	public Short getNumber();
	public void setNumber(Short value);
	
	public class Data extends Struct.Data implements Address{

		@Override
		public String getCity() {
			return (String)getValue(p_city);
		}

		@Override
		public void setCity(String value) {
			setValue(p_city, value);
		}

		@Override
		public String getStreet() {
			return (String)getValue(p_street);
		}

		@Override
		public void setStreet(String value) {
			setValue(p_street, value);
		}

		@Override
		public Short getNumber() {
			return (Short)getValue(p_number);
		}

		@Override
		public void setNumber(Short value) {
			setValue(p_number, value);
		}
	}
	

}
