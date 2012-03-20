package org.ink.example.customer;

import junit.framework.TestCase;

import org.ink.core.vm.exceptions.InkException;
import org.ink.core.vm.factory.Context;
import org.ink.core.vm.factory.InkVM;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.exceptions.InvalidPathException;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.mirror.Mirror;

/**
 * @author Atzmon Hen-tov
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ModelPathTests extends TestCase {

	private final Context context = InkVM.instance().getContext();

	public void testModelPath() throws InkException{
		InkObject customerClass = context.getObject("example.customer:Customer");
		assertNotNull(customerClass);
		CustomerState customer1 = context.getState("example.customer:TheFirstCustomer");
		assertNotNull(customer1);
		CustomerState customer2 = context.getState("example.customer:TheSecondCustomer");
		assertNotNull(customer2);

		CustomerState customer3 = context.getState("example.customer:TheThirdCustomer");
		assertNotNull(customer3);

		
		assertEquals(customer1.getFirstName(),"Lior");
		assertEquals(customer1.getLastName(),"Schachter");
		Byte age = 32;
		assertEquals(customer1.getAge(), age);
		assertNotNull(customer1.getAddress());

		assertEquals(customer2.getFirstName(),"Also Lior");
		assertEquals(customer2.getLastName(),"Also Schachter");
		age = 33;
		assertEquals(customer2.getAge(), age);
		assertNotNull(customer2.getAddress());
		
		Mirror c1 = customer1.reflect();
		assertEquals(c1.getValueByPath("first_name"), "Lior");
		assertEquals(c1.getValueByPath("last_name"), "Schachter");
		assertEquals(c1.getValueByPath("address.street"), "Bar Kokva");
		assertEquals(c1.getValueByPath("keyValueMap<ads>"), 23);
		assertEquals(c1.getValueByPath("elementsMap<Also Lior>.first_name"), "Also Lior");
		assertEquals(c1.getValueByPath("string_list[1]"), "asdasd");
		

		Mirror c2 = customer2.reflect();
		assertEquals(c2.getValueByPath("first_name"), "Also Lior");
		assertEquals(c2.getValueByPath("last_name"), "Also Schachter");
		assertEquals(c2.getValueByPath("address.street"), "Hovevey Zion");
		
		Mirror c3 = customer3.reflect();
		assertEquals(c3.getValueByPath("friends[1].first_name"), "Also Lior");
		
		Object zip = null;
		boolean exceptionThrown = false;
		try {
			zip = c1.getValueByPath("address.zip");
		}
		catch (InvalidPathException e) {
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown);
		
		exceptionThrown = false;
		try {
			zip = c2.getValueByPath("address.zip"); 
		}
		catch (InvalidPathException e) {
			exceptionThrown = true;
		}
		assertFalse(exceptionThrown);
		assertEquals(zip, 2468);
	}
	
	public void testModelPathValidation() throws InkException{
		InkObject customerClass = context.getObject("example.customer:Customer");
		assertNotNull(customerClass);
		
		ClassMirror cm = customerClass.reflect();
		assertNull(cm.validatePath("first_name"));
		assertNull(cm.validatePath("last_name"));
		assertNull(cm.validatePath("address.street"));
		assertNull(cm.validatePath("keyValueMap<ads>"));
		assertNull(cm.validatePath("elementsMap<Also Lior>.first_name"));
		assertNull(cm.validatePath("string_list[1]"));
		assertNull(cm.validatePath("friends[1].first_name"));
		assertNotNull(cm.validatePath("address.zip"));
		assertNotNull(cm.validatePath("friends.first_name"));
		assertNotNull(cm.validatePath("friends[a].first_name"));
		assertNotNull(cm.validatePath("friends[1].first_name2"));
		assertNotNull(cm.validatePath("keyValueMap<ads>.1"));
		assertNotNull(cm.validatePath("kuku"));
	}


}
