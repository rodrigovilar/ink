package org.ink.core.vm.factory.internal;

import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.internal.MirrorAPI;

/**
 * @author Lior Schachter
 */
public class CoreObjectDescriptorImpl extends FileSystemObjectDescriptor implements CoreObjectDescriptor {

	private MirrorAPI object;

	public CoreObjectDescriptorImpl(String id, String classId, Class<?> stateClass, InkObjectState object) {
		super(id, classId, stateClass);
		this.object = (MirrorAPI) object;
	}

	/*
	 * (non-Javadoc)
	 * @see org.ink.core.vm.internal.CoreObjectDescriptor#getObject()
	 */
	public MirrorAPI getObject() {
		return object;
	}

	@Override
	public String toString() {
		String superId = object.getSuper() != null ? object.getSuper().getId() : "null";
		String result = "Core object: ID='" + getId() + "', SUPER='" + superId + "', Class='" + getClassId();
		return result;
	}

	@Override
	public boolean isClass() {
		return false;
	}

}
