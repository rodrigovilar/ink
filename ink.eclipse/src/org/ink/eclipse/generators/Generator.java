package org.ink.eclipse.generators;

import org.ink.core.vm.mirror.Mirror;

public interface Generator {

	public StringBuilder generate(Mirror mirror);

}
