package org.ink.core.vm.factory;

import java.io.File;
import java.util.List;

import org.ink.core.vm.constraints.ValidationMessage;
import org.ink.core.vm.serialization.ParseError;


public interface ElementDescriptor<D>{
	public String getId();
	public String getSuperId();
	public String getClassId();
	public D getRawData();
	public File getResource();
	public void setValidationErrorMessages(List<ValidationMessage> validationErrorMessages);
	public List<ValidationMessage> getValidationErrorMessages();
	public void setParsingErrors(List<ParseError> parsingErrors);
	public List<ParseError> getParsingErrors();
	public boolean containsErrors();
	public void clearErrors();
}
