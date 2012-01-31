package org.ink.codegen.velocity;

import java.io.Writer;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.ink.core.vm.lang.InkObjectImpl;

public class VelocityExecutorImpl<S extends VelocityExecutorState>
extends InkObjectImpl<S> implements VelocityExecutor {

	VelocityEngine ve;

	@Override
	public void generate(String templateRelativePath, Map<String, Object> context, Writer target) {
		VelocityContext velocityContext = new VelocityContext();
		for(Map.Entry<String, Object> en : context.entrySet()){
			velocityContext.put(en.getKey(), en.getValue());
		}
		Template template = ve.getTemplate(templateRelativePath);
		template.merge(velocityContext, target);
	}

	@Override
	public void afterStateSet() {
		super.afterStateSet();
		ve = new VelocityEngine();
		Properties props = new Properties();
		for(Map.Entry<String, String> en : getState().getConfigurations().entrySet()){
			props.put(en.getKey(), en.getValue());
		}
		ve.init(props);
	}


}