package org.ink.core.vm.serialization;

import java.net.URL;

/**
 * @author Lior Schachter
 */
public class ParseError {
	private int lineNumber = -1;
	private int position = -1;
	private String desc;
	private URL url;

	public ParseError(int lineNumber, int position, String desc, URL url) {
		this.lineNumber = lineNumber;
		this.position = position;
		this.desc = desc;
		this.url = url;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public int getPosition() {
		return position;
	}

	public String getDescription() {
		return desc;
	}

	public URL getURL() {
		return url;
	}

}
