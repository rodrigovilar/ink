package org.ink.codegen;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.ink.codegen.velocity.VelocityGenerator;
import org.ink.codegen.velocity.VelocityGeneratorState;
import org.ink.core.vm.factory.InkVM;
import org.junit.Assert;
import org.junit.Test;

public class TestVelocity {


	@Test
	public void testPureVelocity(){
		VelocityEngine ve = new VelocityEngine();
		Properties props = new Properties();
		props.put("file.resource.loader.class", ClasspathResourceLoader.class.getName());
		ve.init(props);
		VelocityContext context = new VelocityContext();
		context.put( "foo", "Lior" );
		Template template = null;
		try
		{
			template = ve.getTemplate("hello_world.vm");
			Assert.assertNotNull(template);
			StringWriter sw = new StringWriter();
			template.merge(context, sw);
			Assert.assertTrue(sw.getBuffer().length()>0);
			System.out.println(sw.toString());
		}
		catch(Exception e){
			Assert.assertTrue(false);
		}

	}
	
	@Test
	public void testInkVelocity(){
		VelocityGeneratorState generatorState = InkVM.instance().getContext().newInstance("ink.codegen.velocity:VelocityGenerator");
		generatorState.setTemplateRelativePath("hello_world.vm");
		VelocityGenerator generator = generatorState.getBehavior();
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("foo", "Lior");
		List<String> l = new ArrayList<String>();
		l.add("Ink Language");
		l.add("Respect!!!");
		context.put("foo", "Lior");
		context.put( "l", l );
		String result = generator.generate(context);
		Assert.assertTrue(result.length() > 0);
		System.out.println(result);
	}
	
	@Test
	public void testClassGen(){
		VelocityGenerator generator = InkVM.instance().getContext().getObject("ink.codegen.velocity:example_class_gen");
		Map<String, Object> context = new HashMap<String, Object>();
		String result = generator.generate(context);
		Assert.assertTrue(result.length() > 0);
		System.out.println(result);
	}


}
