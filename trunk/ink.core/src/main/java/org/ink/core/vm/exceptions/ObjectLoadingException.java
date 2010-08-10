package org.ink.core.vm.exceptions;

import java.io.File;
import java.util.List;

import org.ink.core.vm.constraints.ValidationMessage;
import org.ink.core.vm.serialization.ParseError;

/**
 * @author Lior Schachter
 */
public class ObjectLoadingException extends RuntimeException{
	
	public File f = null;
	List<ParseError> parseErrors;
	List<ValidationMessage> validationErrors;
	
	public ObjectLoadingException(List<ValidationMessage> validationErrors, List<ParseError> parseErrors, File f, String id){
		super("Error while Loading Ink Object '" + id +"'.");
		this.f = f;
		this.parseErrors = parseErrors;
		this.validationErrors = validationErrors;
	}

	public File getFile() {
		return f;
	}

	public List<ParseError> getParseErrors() {
		return parseErrors;
	}

	public List<ValidationMessage> getValidationErrors() {
		return validationErrors;
	}
	
}
