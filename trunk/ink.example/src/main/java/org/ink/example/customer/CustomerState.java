package org.ink.example.customer;

import org.ink.core.vm.lang.InkObjectState;

/**
 * @author Lior Schachter
 */
public interface CustomerState extends InkObjectState{
	
	public static final byte p_first_name = 0;
	public static final byte p_last_name = 1;
	public static final byte p_age = 2;
	public static final byte p_address = 3;
	public static final byte p_gender = 4;
	
	public static final byte t_fan = 2;
	
	
	public String getFirstName();
	public void setFirstName(String value);
	
	public String getLastName();
	public void setLastName(String value);
	
	public Byte getAge();
	public void setAge(Byte value);
	
	public Address getAddress();
	public void setAddress(Address value);
	
	public Gender getGender();
	public void setGender(Gender value);
	
	public class Data extends InkObjectState.Data implements CustomerState{

		@Override
		public String getFirstName() {
			return (String)getValue(p_first_name);
		}

		@Override
		public void setFirstName(String value) {
			setValue(p_first_name, value);
		}

		@Override
		public String getLastName() {
			return (String)getValue(p_last_name);
		}

		@Override
		public void setLastName(String value) {
			setValue(p_last_name, value);
		}
		
		@Override
		public Byte getAge() {
			return (Byte)getValue(p_age);
		}

		@Override
		public void setAge(Byte value) {
			setValue(p_age, value);
		}

		@Override
		public Address getAddress() {
			return (Address)getValue(p_address);
		}

		@Override
		public void setAddress(Address value) {
			setValue(p_address, value);
		}
		
		@Override
		public Gender getGender() {
			return (Gender)getValue(p_gender);
		}

		@Override
		public void setGender(Gender value) {
			setValue(p_gender, value);
		}
	}
	

}
