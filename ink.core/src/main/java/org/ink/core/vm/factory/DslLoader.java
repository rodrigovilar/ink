package org.ink.core.vm.factory;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.ink.core.vm.exceptions.ObjectLoadingException;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.InkObjectState;

/**
 * @author Lior Schachter
 */
public interface DslLoader extends InkObject {

	public void scan(DslFactory ownerFactory);

	public InkObjectState getObject(String id, Context context) throws ObjectLoadingException;

	public Iterator<String> iterator();

	public List<File> getInkFiles();

	public void init();

	public List<InkErrorDetails> collectErrors();

	public ElementDescriptor<?> getDescriptor(String id);

	public List<String> getElements(String filepath);

	List<String> getElementsIds();

}
