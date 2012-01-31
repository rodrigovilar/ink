package org.ink.codegen.velocity;

import java.io.Writer;
import java.util.Map;

import org.ink.core.vm.traits.TraitClass;

public interface VelocityGeneratorMeta extends TraitClass {

	void generate(String templateRelativePath, Map<String, Object> context, Writer w);

}