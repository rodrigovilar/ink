package org.ink.core.vm.mirror;

import org.ink.core.vm.types.EnumTypeState;


/**
 * @author Lior Schachter
 */
public class EnumTypeMirrorImpl<S extends EnumTypeMirrorState> extends MirrorImpl<S> implements EnumTypeMirror{

	@Override
	public String getFullJavaPackage() {
		String result = getTragetOwnerFactory().getJavaPackage();
		String ownPath = ((EnumTypeState)getTargetState()).getJavaPath();
		if(ownPath==null || ownPath.equals("")){
			return result;
		}
		return result + "." + ownPath;
	}

}
