package org.ink.example.customer;


import java.util.List;

import junit.framework.TestCase;

import org.ink.core.vm.constraints.ValidationContext;
import org.ink.core.vm.exceptions.ValidationException;
import org.ink.core.vm.factory.Context;
import org.ink.core.vm.factory.InkVM;
import org.ink.core.vm.factory.internal.CoreNotations;
import org.ink.core.vm.lang.InkClass;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.InkObjectImpl;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.Property;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.mirror.editor.ObjectEditor;
import org.ink.core.vm.traits.Trait;
import org.ink.core.vm.traits.TraitClass;
import org.ink.core.vm.traits.TraitClassState;

/**
 * @author Lior Schachter
 */
public class CustomerTest extends TestCase{

	private static final String NUMBER = "number";
	private static final String STREET = "street";
	private static final String CITY = "city";
	private static final String ADDRESS = "address";
	private static final String LAST_NAME = "last_name";
	private static final String FIRST_NAME = "first_name";

	private final Context context = InkVM.instance().getContext();

	public void testCustomerRetrival(){
		InkObject customerClass = context.getObject("example.customer:Customer");
		assertNotNull(customerClass);
		CustomerState customer = context.getState("example.customer:TheFirstCustomer");

		assertNotNull(customer);


		assertEquals(customer.getFirstName(),"Lior");
		assertEquals(customer.getLastName(),"Schachter");
		assertNotNull(customer.getAddress());
		assertNotNull(customer.getAge());
		assertFalse(customer.getEnumList().isEmpty());
		assertFalse(customer.getStringList().isEmpty());
		//checking default value
		assertEquals(customer.getGender(), Gender.Male);
		Customer behv = customer.getBehavior();
		behv.sendLetter("kuku");
		assertEquals(customer.reflect().getPropertyValue(FIRST_NAME),"Lior");
		assertEquals(customer.reflect().getPropertyValue(LAST_NAME),"Schachter");
		assertNotNull(customer.reflect().getPropertyValue(ADDRESS));

		assertEquals(customer.reflect().getPropertyValue(CustomerState.p_first_name),"Lior");
		assertEquals(customer.reflect().getPropertyValue(CustomerState.p_last_name),"Schachter");
		assertNotNull(customer.reflect().getPropertyValue(CustomerState.p_address));

		assertNotNull(customer.getBehavior());
	}

	public void testCustomerCreation(){
		CustomerState customer = createState("example.customer:Customer");
		assertNotNull(customer);
		customer.setFirstName("kuku");
		customer.setLastName("shmupu");
		ObjectEditor customerEditor = customer.reflect().edit();
		customerEditor.setId("Moshe");

		Address addr = createState("example.customer:Address");
		ObjectEditor addrEditor = addr.reflect().edit();
		addrEditor.setPropertyValue(CITY, "Tel Aviv");
		addrEditor.setPropertyValue(STREET, "Bar-Giyora");
		addrEditor.setPropertyValue(NUMBER, (short)10);
		customer.setAddress(addr);

		assertNotNull(customer.getAddress());
		assertEquals(customer.getAddress().reflect().getPropertyValue(CITY), "Tel Aviv");
		Customer customerBehav = customer.getBehavior();
		assertNotNull(customerBehav.sendLetter("Hello !!!!!"));
		boolean exception = false;
		//try{
			InkObject object = addr.getBehavior();
			assertTrue(object.getClass()==InkObjectImpl.class);
		//}catch(UnsupportedOperationException e){
		//	exception = true;
		//}
		//assertTrue(exception);
	}

	@SuppressWarnings("unchecked")
	private <T extends InkObjectState> T createState(String classId) {
		InkClass cls = context.getObject(classId);
		InkObjectState object = cls.newInstance();
		return (T) object;
	}

	public void testCustomerCoreValidations(){
		CustomerState customer = createState("example.customer:Customer");
		ValidationContext vc = createState("ink.core:ValidationContext").getBehavior();
		customer.getBehavior().validate(vc);
		assertTrue(vc.containsError());
		assertTrue(vc.getMessages().size()==4);

		customer.setFirstName("Lior");
		customer.setLastName("Schachter");
		customer.setAge((byte)32);
		vc.reset();
		customer.getBehavior().validate(vc);
		assertTrue(vc.containsError());
		assertTrue(vc.getMessages().size()==1);
		assertTrue(extractFirstMessageId(vc).equals(CoreNotations.Ids.MISSING_FIELD_DATA));
		assertTrue(vc.getMessages().get(0).getFormattedMessage().contains(ADDRESS));

		Address addr = createState("example.customer:Address");
		ObjectEditor addrEditor = addr.reflect().edit();
		addrEditor.setPropertyValue(CITY, "Tel Aviv");
		addrEditor.setPropertyValue(STREET, "Bar-Giyora");
		addrEditor.setPropertyValue(NUMBER, 10);
		vc.reset();
		addr.validate(vc);
		assertTrue(vc.getMessages().size()==1);
		//the number should be of a Short type and not Integer
		assertTrue(extractFirstMessageId(vc).equals(CoreNotations.Ids.WRONG_VALUE_TYPE));
		assertTrue(vc.getMessages().get(0).getFormattedMessage().contains(NUMBER));
		addrEditor.setPropertyValue(NUMBER, (short)10);
		vc.reset();
		addr.validate(vc);
		assertFalse(vc.containsError());

		customer.setAddress(addr);
		customer.getBehavior().validate(vc);
		assertFalse(vc.containsError());

		addr.setNumber((short)-1);
		customer.getBehavior().validate(vc);
		assertTrue(vc.containsError());
		assertTrue(extractFirstMessageId(vc).equals(CoreNotations.Ids.MIN_VALUE_VIOLATION));
		assertTrue(vc.getMessages().get(0).getFormattedMessage().contains(NUMBER));

		addr.setNumber((short)1001);
		vc.reset();
		customer.getBehavior().validate(vc);
		assertTrue(vc.containsError());
		assertTrue(extractFirstMessageId(vc).equals(CoreNotations.Ids.MAX_VALUE_VIOLATION));
		assertTrue(vc.getMessages().get(0).getFormattedMessage().contains(NUMBER));

		CustomerState stamOdCustomer = customer.cloneState();
		customer.reflect().edit().setPropertyValue(CustomerState.p_address, stamOdCustomer);
		vc.reset();
		customer.getBehavior().validate(vc);
		assertTrue(vc.containsError());
		assertTrue(extractFirstMessageId(vc).equals(CoreNotations.Ids.WRONG_VALUE_TYPE));
		System.out.println(vc.getMessages().get(0).getFormattedMessage());
		assertTrue(vc.getMessages().get(0).getFormattedMessage().contains(ADDRESS));

	}

	public void testCustomerStructuralTraits(){
		CustomerState customer = context.getState("example.customer:TheFirstCustomer");
		InkVM.instance().reloadDSL("example.customer");
		customer = context.getState("example.customer:TheFirstCustomer");
		//TODO need to fix this in the state generator
		//Trait t = customer.asTrait(CustomerState.t_fan);
		Trait t = customer.asTrait(CustomerState.t_fan);
		assertNotNull(t);
		t = customer.asTrait("fan");
		assertNotNull(t);
		assertNotNull(customer.reflect().getPropertyValue("fan.favorite_sport"));
		assertEquals(customer.reflect().getPropertyValue("fan.favorite_sport"), SportsKind.BasketBall);
		customer = createState("example.customer:Customer");
		assertNotNull(customer.reflect().getPropertyValue("fan.favorite_sport"));
		assertEquals(customer.reflect().getPropertyValue("fan.favorite_sport"), SportsKind.FootBall);
	}

	@SuppressWarnings("unchecked")
	public void testCustomerDetachableTraits(){
		CustomerState customer = context.getState("example.customer:TheFirstCustomer");
		Trait t = customer.asTrait("stam");
		TraitClass tClass = t.getMeta();
		assertNotNull(t);
		List<Property> injectedProps = (List<Property>) tClass.reflect().getPropertyValue(TraitClassState.p_injected_properties);
		assertFalse(injectedProps.isEmpty());
		String name = tClass.getRole() + "." + injectedProps.get(0).getName();
		Mirror mirror = customer.reflect();
		assertNull(mirror.getPropertyValue(name));
		mirror.edit().setPropertyValue(name, "kukukuku");
		assertNotNull(mirror.getPropertyValue(name));

		customer = createState("example.customer:SubCustomer");
		mirror = customer.reflect();
		assertNull(mirror.getPropertyValue(name));
		mirror.edit().setPropertyValue(name, "kukukuku");
		assertNotNull(mirror.getPropertyValue(name));

	}

	public void testCustomerInterceptor(){
		CustomerState customer = createState("example.customer:Customer");
		customer.setFirstName("Lior");
		customer.setLastName("Schachter");
		customer.setAge((byte)32);

		Address addr = createState("example.customer:Address");
		addr.setCity("Tel Aviv");
		addr.setStreet("Bar Kokva");
		addr.setNumber((short)1001);
		customer.setAddress(addr);

		Customer customerImpl = customer.getBehavior();

		assertNotNull(customerImpl.sendLetter("kuku"));
		customer.setFirstName("Atzmon");

		boolean exception=false;
		try{
			customerImpl.sendLetter("kuku");
		}catch(RuntimeException e){
			exception = true;
		}
		assertTrue(exception);
		exception = false;
		try{
			customerImpl.isFriend(customerImpl);
		}catch(ValidationException e){
			exception = true;
		}
		assertTrue(exception);
		assertFalse(customerImpl.isFriend((Customer) context.getObject("example.customer:TheFirstCustomer")));
	}

	private String extractFirstMessageId(ValidationContext vc) {
		return vc.getMessages().get(0).getMessageTemplate().reflect().getId();
	}


}
