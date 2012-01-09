package org.ink.core.vm.factory.resources;

import org.ink.core.vm.factory.DslFactory;
import org.ink.core.vm.lang.InkClassState;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.types.EnumTypeState;
import org.ink.core.vm.types.GenericEnum;
import org.ink.core.vm.utils.CoreUtils;

/**
 * @author Lior Schachter
 */
public abstract class CoreResourceResolver extends ResourceResolver {

	public InkClassState findCoreClass(InkClassState cls) {
		ClassMirror cm = cls.reflect();
		if (cm.isCoreObject() && !cm.isAbstract()) {
			return cls;
		}
		return findCoreClass((InkClassState) cls.reflect().getSuper().edit().getEditedState());
	}

	@Override
	public String getBehaviorClassName(InkClassState cls) {
		InkClassState coreClass = findCoreClass(cls);
		ClassMirror cm = coreClass.reflect();
		String result = cm.getFullJavaPackage() + "." + getBehaviorShortClassName(cm);
		return result;
	}

	@Override
	public String getInterfaceClassName(InkClassState cls) {
		InkClassState coreClass = findCoreClass(cls);
		ClassMirror cm = coreClass.reflect();
		String result = cm.getFullJavaPackage() + "." + getInterfaceClassShortName(cm);
		return result;
	}

	@Override
	public String getDataClassName(InkClassState cls) {
		InkClassState coreClass = findCoreClass(cls);
		ClassMirror cm = coreClass.reflect();
		String result = cm.getFullJavaPackage() + "." + getDataClassShortName(cm);
		return result;
	}

	@Override
	public String getStructDataClassName(InkClassState cls) {
		InkClassState coreClass = findCoreClass(cls);
		ClassMirror cm = coreClass.reflect();
		String result = cm.getFullJavaPackage() + "." + getStructDataClassShortName(cm);
		return result;
	}

	@Override
	public String getEnumClassName(EnumTypeState enumState) {
		if (enumState.reflect().isCoreObject()) {
			StringBuilder builder = new StringBuilder(100);
			String javaPack = enumState.getJavaPath();
			DslFactory factory = enumState.reflect().getTragetOwnerFactory();
			if (javaPack == null || javaPack.equals("")) {
				builder.append(factory.getJavaPackage()).append(".").append(CoreUtils.getShortId(enumState.getId()));
			} else {
				builder.append(factory.getJavaPackage()).append(".").append(javaPack).append(".").append(CoreUtils.getShortId(enumState.getId()));
			}
			return builder.toString();
		} else {
			return GenericEnum.class.getName();
		}
	}

	@Override
	public boolean enableEagerFetch() {
		return true;
	}

}