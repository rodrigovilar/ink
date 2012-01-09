package org.ink.core.vm.constraints;

import org.ink.core.vm.factory.VMConfig;
import org.ink.core.vm.factory.VMMain;
import org.ink.core.vm.factory.internal.CoreNotations;
import org.ink.core.vm.factory.resources.DefaultResourceResolver;
import org.ink.core.vm.factory.resources.JavaClassDescription;
import org.ink.core.vm.factory.resources.ResourceResolver;
import org.ink.core.vm.lang.InkClassState;
import org.ink.core.vm.messages.Message;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.mirror.Mirror;

/**
 * @author Lior Schachter
 */
public class ClassConstraintsImpl<S extends ClassConstraintsState> extends ConstraintsImpl<S> {

	private DefaultResourceResolver defaultResolver = new DefaultResourceResolver();

	@Override
	public boolean validateTarget(Mirror stateSuper, ValidationContext context, SystemState systemState) {
		boolean valid = super.validateTarget(stateSuper, context, systemState);
		if (valid) {
			ClassMirror cm = getTargetState().reflect();
			ResourceResolver rr = VMConfig.instance().getResourceResolver();
			JavaClassDescription interfaceDesc = null;
			if (cm.getJavaMapping().hasBehavior()) {
				JavaClassDescription desc = rr.getBehaviorClassDescription(cm);
				if (desc == null) {
					context.add(getTargetState(), getMessage(), Severity.MAPPING_ERROR, false, "missing Behavior class '" + defaultResolver.getBehaviorClassName(cm) + "'");
				} else {
					if (desc.getSuperClass() == null) {
						if (cm.getSuper() != null) {
							context.add(getTargetState(), getMessage(), Severity.MAPPING_ERROR, ResourceType.JAVA_CLASS, false, "Behavior class should extend '" + defaultResolver.getBehaviorClassName((ClassMirror) cm.getSuper()) + "'");
						}
					} else if (cm.getSuper() != null) {
						ClassMirror sm = cm.getSuper();
						while (sm != null && !sm.getJavaMapping().hasBehavior()) {
							sm = sm.getSuper();
						}
						String superClassName = defaultResolver.getBehaviorClassName(sm);
						if (!desc.getSuperClass().equals(superClassName)) {
							context.add(getTargetState(), getMessage(), Severity.MAPPING_ERROR, ResourceType.JAVA_CLASS, false, "Behavior class should extend '" + defaultResolver.getBehaviorClassName((ClassMirror) cm.getSuper()) + "'");
						}
					}

					if (cm.getJavaMapping().hasInterface()) {
						interfaceDesc = rr.getInterfaceDescription(cm);
						if (interfaceDesc != null) {
							if (desc.getInterfaces() == null) {
								context.add(getTargetState(), getMessage(), Severity.MAPPING_ERROR, ResourceType.JAVA_CLASS, false, "Behavior class should implement '" + defaultResolver.getInterfaceClassName(cm) + "'");
							} else {
								String interfaceClassName = defaultResolver.getInterfaceClassName((InkClassState) getTargetState());
								if (!desc.getInterfaces().contains(interfaceClassName)) {
									context.add(getTargetState(), getMessage(), Severity.MAPPING_ERROR, ResourceType.JAVA_CLASS, false, "Behavior class should implement '" + defaultResolver.getInterfaceClassName(cm) + "'");
								}
							}
						}
					}
				}

			}
			if (cm.getJavaMapping().hasInterface()) {
				if (interfaceDesc == null) {
					interfaceDesc = rr.getInterfaceDescription(cm);
				}
				if (interfaceDesc == null) {
					context.add(getTargetState(), getMessage(), Severity.MAPPING_ERROR, false, "missing Interface class '" + defaultResolver.getInterfaceClassName(cm) + "'");
				} else {
					if (interfaceDesc.getInterfaces().isEmpty()) {
						if (cm.getSuper() != null) {
							context.add(getTargetState(), getMessage(), Severity.MAPPING_ERROR, ResourceType.JAVA_INTERFACE, false, "Interface class should extend '" + defaultResolver.getInterfaceClassName(cm) + "'");
						}
					} else if (cm.getSuper() != null) {
						ClassMirror sm = cm.getSuper();
						while (sm != null && !sm.getJavaMapping().hasInterface()) {
							sm = sm.getSuper();
						}
						String superInterfaceName = defaultResolver.getInterfaceClassName(sm);
						if (!interfaceDesc.getInterfaces().contains(superInterfaceName)) {
							context.add(getTargetState(), getMessage(), Severity.MAPPING_ERROR, ResourceType.JAVA_INTERFACE, false, "Interface class should extend '" + defaultResolver.getInterfaceClassName(cm) + "'");
						}
					}
				}
			}

			return !context.containsError();
		}
		return valid;
	}

	private Message getMessage() {
		// this is to solve eclipse bootsrapping problem. need to fixed using metaclass
		return VMMain.getCoreFactory().getObject(CoreNotations.Ids.JAVA_MAPPING_ERROR);
	}

}
