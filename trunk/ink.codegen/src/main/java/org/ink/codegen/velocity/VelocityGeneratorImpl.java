package org.ink.codegen.velocity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import org.ink.core.vm.traits.TraitImpl;

public class VelocityGeneratorImpl<S extends VelocityGeneratorState> extends
TraitImpl<S> implements VelocityGenerator {

	@Override
	public void generate(Map<String, Object> context, File targetFile) throws IOException {
		if(targetFile.exists()){
			targetFile.delete();
		}else{
			targetFile.getParentFile().mkdirs();
		}

		Writer w = null;
		try{
			w = new FileWriter(targetFile);
			generate(context, w);
		}finally{
			if(w!=null){
				w.flush();
				w.close();
			}
		}

	}

	private void generate(Map context, Writer w) {
		VelocityGeneratorMeta meta = getMeta();
		prepareContext(context);
		String template = getState().getTemplateRelativePath();
		if(template==null){
			template = getTargetState().reflect().getClassMirror().getShortId() + ".vm";
		}
		meta.generate(template, context, w);
	}

	protected void prepareContext(Map context) {}

	@Override
	public String generate(Map<String, Object> context) {
		StringWriter w = new StringWriter();
		generate(context, w);
		return w.getBuffer().toString();
	}

}