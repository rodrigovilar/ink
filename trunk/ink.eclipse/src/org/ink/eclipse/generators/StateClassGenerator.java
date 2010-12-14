package org.ink.eclipse.generators;

import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.mirror.Mirror;

public class StateClassGenerator implements Generator{

	@Override
	public StringBuilder generate(Mirror mirror) {
		ClassMirror classMirror = (ClassMirror)mirror;
		StringBuilder interfaceClass = new StringBuilder(500);
		StringBuilder innerClass = new StringBuilder(500);


		StringBuilder result = new StringBuilder(1000);
		return result;
	}




}
