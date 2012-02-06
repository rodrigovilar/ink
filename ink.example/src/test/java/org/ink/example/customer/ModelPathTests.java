package org.ink.example.customer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.ink.core.vm.constraints.ValidationContext;
import org.ink.core.vm.exceptions.WeaveException;
import org.ink.core.vm.factory.Context;
import org.ink.core.vm.factory.DslFactoryEventDispatcher;
import org.ink.core.vm.factory.DslFactoryState;
import org.ink.core.vm.factory.InkVM;
import org.ink.core.vm.factory.VMMain;
import org.ink.core.vm.factory.internal.CoreNotations;
import org.ink.core.vm.lang.InkClass;
import org.ink.core.vm.lang.InkClassState;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.JavaMapping;
import org.ink.core.vm.lang.Property;
import org.ink.core.vm.lang.PropertyState;
import org.ink.core.vm.lang.exceptions.InvalidPathException;
import org.ink.core.vm.lang.internal.ClassMirrorAPI;
import org.ink.core.vm.lang.internal.MirrorAPI;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.mirror.editor.ClassEditor;
import org.ink.core.vm.mirror.editor.ObjectEditor;
import org.ink.core.vm.modelinfo.ModelInfoFactory;
import org.ink.core.vm.modelinfo.ModelInfoRepository;
import org.ink.core.vm.modelinfo.relations.ExtendsRelation;
import org.ink.core.vm.modelinfo.relations.IsInstanceOfRelation;
import org.ink.core.vm.traits.Trait;
import org.ink.core.vm.traits.TraitClass;
import org.ink.core.vm.traits.TraitClassState;
import org.ink.core.vm.traits.TraitState;
import org.ink.core.vm.types.PrimitiveType;
import org.ink.core.vm.types.PrimitiveTypeMarker;
import org.ink.core.vm.utils.property.LongAttributeState;
import org.ink.core.vm.utils.property.Reference;
import org.ink.core.vm.utils.property.ReferenceState;
import org.ink.core.vm.utils.property.StringAttributeState;

/**
 * @author Atzmon Hen-tov
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ModelPathTests extends TestCase {

	private final Context context = InkVM.instance().getContext();

	public void testModelPath(){
		InkObject customerClass = context.getObject("example.customer:Customer");
		assertNotNull(customerClass);
		CustomerState customer1 = context.getState("example.customer:TheFirstCustomer");
		assertNotNull(customer1);
		CustomerState customer2 = context.getState("example.customer:TheSecondCustomer");
		assertNotNull(customer2);

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
		
		Customer c1 = customer1.getBehavior();
		assertEquals(c1.getValueByPath("first_name"), "Lior");
		assertEquals(c1.getValueByPath("last_name"), "Schachter");
		assertEquals(c1.getValueByPath("address.street"), "Bar Kokva");

		Customer c2 = customer2.getBehavior();
		assertEquals(c2.getValueByPath("first_name"), "Also Lior");
		assertEquals(c2.getValueByPath("last_name"), "Also Schachter");
		assertEquals(c2.getValueByPath("address.street"), "Hovevey Zion");
		
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

}
