package org.ink.core.vm.factory;

import java.io.File;
import java.util.List;

import org.ink.core.vm.exceptions.InkException;

/**
 * The interface of the Ink Virtual Machine.
 * <br/>
 * Use the {@link InkVM#instance()} method to retrieve the singleton instance of the Ink VM.
 * <br/>
 * Example:
 * <pre>
 *     InkVM.instance().getContext().getObject("ink.tutorial1:Active_offers");
 * </pre>
 *
 * @author Lior Schachter
 */
public interface VM {

	/**
	 * Returns the {@link DslFactory} of the required namespace.
	 * @param namespace the namespace of the factory, such as <code>ink.core</code>.
	 * @return the {@link DslFactory} of the required namespace, or <code>null</code> if no factory is bound for the namespace.
	 */
	public DslFactory getFactory(String namespace);

	/**
	 * Returns the {@link DslFactory} associated with the required source file.
	 * @param f a source file (<code>.ink</code>).
	 * @return the {@link DslFactory} associated with the source file, or <code>null</code> if no such factory is found.
	 */
	public DslFactory getOwnerFactory(File f);

	/**
	 * Retrieves the default {@link Context} object of the running Ink VM.
	 * @return the {@link Context} of the default DSL factory.
	 */
	public Context getContext();

	/**
	 * Returns the default {@link DslFactory}.
	 * @return the default {@link DslFactory}.
	 */
	public DslFactory getFactory();

	/**
	 * Collects the parsing and validation errors from all the namespaces loaded to the Ink VM.
	 * @return a list of parsing and validation errors from all the namespaces.
	 */
	public List<InkErrorDetails> collectErrors();

	/**
	 * Collects the parsing and validation errors from the required namespace.
	 * @param namespace the namespace to collect the errors from.
	 * @return a list of parsing and validation errors from the required namespace.
	 * @throws NullPointerException if the namespace is not found.
	 */
	public List<InkErrorDetails> collectErrors(String namespace);

	/**
	 * Re-scans and reloads the {@link DslFactory} of the required namespace. Has no effect if the namespace is not found.
	 * @param namespace the namespace of the factory, such as <code>ink.core</code>.
	 */
	public void reloadDSL(String namespace);

	/**
	 * Loads a new {@link DslFactory} and adds it to the running Ink VM. This also loads the new factory's dependencies (i.e., additional DSL factories).
	 * @param path the path to the <code>dsls.ink</code> metadata file (those files are normally handled by Ink itself).
	 * @throws InkException if the factory couldn't be loaded successfully.
	 */
	public void introduceNewDSl(String path) throws InkException;

}
