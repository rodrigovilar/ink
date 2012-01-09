package org.ink.core.vm.factory;

import java.io.File;

import org.ink.core.vm.constraints.ResourceType;

public class InkErrorDetails {

	private final String id;
	private final String formattedMessage;
	private String path = null;
	private final ResourceType resourceType;
	private final File inkSourceDefinition;
	private int lineNumber = -1;

	public InkErrorDetails(String id, String path, String formattedMessage, File inkSourceDefinition, ResourceType resourceType) {
		this.id = id;
		this.path = path;
		this.formattedMessage = formattedMessage;
		this.inkSourceDefinition = inkSourceDefinition;
		this.resourceType = resourceType;
	}

	public InkErrorDetails(String id, int lineNumber, String formattedMessage, File resource, ResourceType resourceType) {
		this.id = id;
		this.lineNumber = lineNumber;
		this.formattedMessage = formattedMessage;
		this.inkSourceDefinition = resource;
		this.resourceType = resourceType;
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

	public File getInkSourceDefinition() {
		return inkSourceDefinition;
	}

	public ResourceType getResourceType() {
		return resourceType;
	}

	public int getLineNumber() {
		return lineNumber;
	}
}
