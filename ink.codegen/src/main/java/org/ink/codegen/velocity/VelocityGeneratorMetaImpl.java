package org.ink.codegen.velocity;

import java.io.Writer;
import java.util.Map;

import org.ink.core.vm.traits.TraitClassImpl;

public class VelocityGeneratorMetaImpl<S extends VelocityGeneratorMetaState>
		extends TraitClassImpl<S> implements VelocityGeneratorMeta {

	@Override
	public void generate(String templateRelativePath, Map<String, Object> context, Writer w) {
		getState().getConfigurator().generate(templateRelativePath, context, w);
	}

}