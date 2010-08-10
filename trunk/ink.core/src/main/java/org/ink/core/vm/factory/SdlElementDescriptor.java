package org.ink.core.vm.factory;

import java.io.File;

import org.ikayzo.sdl.Tag;
import org.ink.core.vm.utils.InkNotations;

/**
 * @author Lior Schachter
 */
public class SdlElementDescriptor extends BaseElementDescriptor<Tag>{

	private String id;
	private Tag data;
	private File f;

	public SdlElementDescriptor(Tag data, File f){
		this.data = data;
		this.id = (String) data.getAttribute(InkNotations.Path_Syntax.ID_ATTRIBUTE);
		this.f = f;
	}
	
	@Override
	public File getResource() {
		return f;
	}
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public Tag getRawData() {
		return data;
	}

}
