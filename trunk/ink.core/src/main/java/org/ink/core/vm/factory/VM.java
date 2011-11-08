package org.ink.core.vm.factory;

import java.io.File;
import java.util.List;

import org.ink.core.vm.exceptions.InkExcpetion;


/**
 * @author Lior Schachter
 */
public interface VM {
	public DslFactory getFactory(String namespace);
	public DslFactory getOwnerFactory(File f);
	public Context getContext();
	public DslFactory getFactory();
	public List<InkErrorDetails> collectErrors();
	public List<InkErrorDetails> collectErrors(String namespace);
	public void reloadDSL(String namespace);
	public void introduceNewDSl(String path) throws InkExcpetion;

}
