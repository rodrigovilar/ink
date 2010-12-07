package org.ink.core.vm.factory;

import java.io.File;

public class InkErrorDetails {

	private final String id;
	private final String formattedMessage;
	private String path = null;
	private final File resource;
	private int lineNumber = -1;

	public InkErrorDetails(String id, String path, String formattedMessage, File resource) {
		this.id = id;
		this.path = path;
		this.formattedMessage = formattedMessage;
		this.resource = resource;
	}

	public InkErrorDetails(String id, int lineNumber, String formattedMessage, File resource) {
		this.id = id;
		this.lineNumber = lineNumber;
		this.formattedMessage = formattedMessage;
		this.resource = resource;
	}

	public String getId() {
		return id;
	}
	public String getFormattedMessage() {
		return formattedMessage;
	}
	public String getPath() {
		return path;
	}
	public File getResource() {
		return resource;
	}


}
