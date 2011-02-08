package org.ink.core.vm.serialization;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.ink.core.vm.factory.Context;
import org.ink.core.vm.factory.ElementDescriptor;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.InkObjectState;

/**
 * @author Lior Schachter
 */
public interface InkReader<D> extends InkObject{

	public InkObjectState[] read(File f) throws IOException;
	public InkObjectState[] read(URL url) throws IOException;
	public InkObjectState[] read(String data);
	public InkObjectState read(D data);
	public InkObjectState[] read(File f, Context context) throws IOException;
	public InkObjectState[] read(URL url, Context context) throws IOException;
	public InkObjectState[] read(String data, Context context);
	public InkObjectState read(D data, Context context);
	public List<ElementDescriptor<D>> extractRawData(File f, Context context) throws IOException;
	public List<ParseError> getErrors();
	public boolean containsErrors();
	public void reset();

}
