package org.ink.core.vm.lang;

import org.ink.core.vm.factory.Context;
import org.ink.core.vm.factory.DslFactory;
import org.ink.core.vm.factory.InkVM;
import org.ink.core.vm.mirror.ClassMirror;

/**
 * @author Lior Schachter
 */
public class InkClassImpl<S extends InkClassState> extends InkObjectImpl<S> implements InkClass {

	@Override
	public Class<?> getJavaClass() {
		return ((ClassMirror) reflect()).getStateInterface();
	}

	/**
	 * Returns <code>DataTypeMarker.Class</code>.
	 */
	@Override
	public DataTypeMarker getTypeMarker() {
		return DataTypeMarker.Class;
	}

	@Override
	public final <T extends InkObjectState> T newInstance(boolean initObjectId, boolean initDefaults) {
		return ((ClassMirror) reflect()).getFactory().newInstance(InkVM.instance().getFactory(), getState(), initObjectId, initDefaults);
	}

	@Override
	public final <T extends InkObjectState> T newInstance(Context context, boolean initObjectId, boolean initDefaults) {
		return ((ClassMirror) reflect()).getFactory().newInstance(context.getFactory(), getState(), initObjectId, initDefaults);
	}

	@Override
	public final <T extends InkObjectState> T newInstance(DslFactory factory, boolean initObjectId, boolean initDefaults) {
		return ((ClassMirror) reflect()).getFactory().newInstance(factory, getState(), initObjectId, initDefaults);
	}

	@Override
	public final <T extends InkObjectState> T newInstance() {
		return newInstance(getContext(), false, true);
	}

	@Override
	public final <T extends InkObjectState> T newInstance(Context context) {
		return newInstance(context, false, true);
	}

	@Override
	public void initInstance(InkObjectState state, boolean initObjectId, boolean initDefaults) {
		// implement
	}

	@Override
	public boolean isPrimitive() {
		return false;
	}

	@Override
	public boolean isObject() {
		return true;
	}

	@Override
	public final boolean isCollection() {
		return false;
	}

	@Override
	public boolean isEnumeration() {
		return false;
	}

}
