package org.ink.core.vm.utils.file;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lior Schachter
 */
public class FileUtils {

	public static List<File> listFiles(String dirPath, FilenameFilter filter) {
		return listFiles(new File(dirPath), filter);
	}

	public static List<File> listInkFiles(File dir) {
		return listFiles(dir, new InkFileFilter());
	}

	public static List<File> listFiles(File dir, FilenameFilter filter) {
		List<File> result = new ArrayList<File>();
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (File f : files) {
				if (f.isDirectory()) {
					result.addAll(listFiles(f, filter));
				} else if (filter.accept(dir, f.getName())) {
					result.add(f);
				}
			}
		}
		return result;
	}

}
