package org.ink.core.vm.factory;

import java.io.File;

import org.ikayzo.sdl.Tag;
import org.ink.core.vm.utils.InkNotations;

/**
 * @author Lior Schachter
 */
public class SdlElementDescriptor extends BaseElementDescriptor<Tag> {

	private String id;
	private String superId;
	private String classId;
	private Tag data;
	private File f;

	public SdlElementDescriptor(String ns, Tag data, File f) {
		this.data = data;
		this.id = (String) data.getAttribute(InkNotations.Path_Syntax.ID_ATTRIBUTE);
		if (ns != null) {
			this.id = ns + InkNotations.Path_Syntax.NAMESPACE_DELIMITER_C + this.id;
		}
		this.f = f;
		this.superId = (String) data.getAttribute(InkNotations.Path_Syntax.SUPER_ATTRIBUTE);
		this.superId = applyNamespace(ns, this.superId);
		this.classId = (String) data.getAttribute(InkNotations.Path_Syntax.CLASS_ATTRIBUTE);
		this.classId = applyNamespace(ns, this.classId);
	}

	private String applyNamespace(String ns, String id) {
		if (id != null && id.indexOf(InkNotations.Path_Syntax.NAMESPACE_DELIMITER_C) < 0) {
			return ns + InkNotations.Path_Syntax.NAMESPACE_DELIMITER_C + id;
		}
		return id;
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

	@Override
	public String getSuperId() {
		return superId;
	}

	@Override
	public String getClassId() {
		return classId;
	}

}
