package org.ink.codegen.velocity;

import java.io.Writer;
import java.util.Map;

import org.ink.core.vm.lang.InkObject;

public interface VelocityExecutor extends InkObject {

	public void generate(String templateRelativePath, Map<String, Object> context, Writer target);

}