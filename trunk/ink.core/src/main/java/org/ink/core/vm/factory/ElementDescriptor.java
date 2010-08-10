package org.ink.core.vm.factory;

import java.io.File;
import java.util.List;

import org.ink.core.vm.constraints.ValidationMessage;
import org.ink.core.vm.serialization.ParseError;


public interface ElementDescriptor<D>{
	public String getId();
	public D getRawData();
	public File getResource();
	public abstract void setValidationErrorMessages(List<ValidationMessage> validationErrorMessages);
	public abstract List<ValidationMessage> getValidationErrorMessages();
	public abstract void setParsingErrors(List<ParseError> parsingErrors);
	public abstract List<ParseError> getParsingErrors();
}
