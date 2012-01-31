package org.ink.codegen.velocity;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.ink.core.vm.traits.Trait;

public interface VelocityGenerator extends Trait {

	public void generate(Map<String, Object> context, File targetFile) throws IOException;
	public String generate(Map<String, Object> context);

}