package org.ink.core.vm.factory;

import java.io.File;
import java.util.List;


/**
 * @author Lior Schachter
 */
public interface VM {
	public DslFactory getFactory(String namespace);
	public DslFactory getOwnerFactory(File f);
	public Context getContext();
	public DslFactory getFactory();
	public List<InkErrorDetails> collectErrors();

}
