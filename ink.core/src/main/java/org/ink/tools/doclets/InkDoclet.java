package org.ink.tools.doclets;

//import com.sun.javadoc.*;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;

public class InkDoclet {
	public static boolean start(final RootDoc root) {
		final String tagName = "ink";
		writeContents(root.classes(), tagName);
		return true;
	}

	private static void writeContents(final ClassDoc[] classes, final String tagName) {
		for (int i = 0; i < classes.length; i++) {
			boolean classNamePrinted = false;
			final MethodDoc[] methods = classes[i].methods();
			for (int j = 0; j < methods.length; j++) {
				final Tag[] tags = methods[j].tags(tagName);
				if (tags.length > 0) {
					if (!classNamePrinted) {
						System.out.println("\n" + classes[i].name() + "\n");
						classNamePrinted = true;
					}
					System.out.println(methods[j].name());
					for (int k = 0; k < tags.length; k++) {
						System.out.println("   " + tags[k].name() + ": " + tags[k].text());
					}
				}
			}
		}
	}
}
