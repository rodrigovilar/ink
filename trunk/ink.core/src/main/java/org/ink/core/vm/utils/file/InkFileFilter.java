package org.ink.core.vm.utils.file;

import java.io.File;
import java.io.FilenameFilter;

import org.ink.core.vm.utils.InkNotations;

/**
 * @author Lior Schachter
 */
public class InkFileFilter implements FilenameFilter{

	private static final String searchFor = "." + InkNotations.Names.INK_FILE_EXTENSION;
	
	@Override
	public boolean accept(File dir, String name) {
		return name.endsWith(searchFor);
	}

	
}
