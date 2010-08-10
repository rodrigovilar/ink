package org.ink.core.vm.utils;

/**
 * @author Lior Schachter
 */
public class InkNotations {
	
	public static class Path_Syntax{
		public static final char NAMESPACE_DELIMITER_C = ':';
		public static final String ABSTRACT_ATTRIBUTE = "abstract";
		public static final String SUPER_ATTRIBUTE = "super";
		public static final String REF_ATTRIBUTE = "ref";
		public static final String CLASS_ATTRIBUTE = "class";
		public static final String ID_ATTRIBUTE = "id";
		public static final String SCOPE_ATTRIBUTE = "scope";
	}
	
	public static class Names{
		public static final String INK_FILE_EXTENSION = "ink";
		public static final String DATA_CLASS_EXTENSION = "State$Data";
		public static final String STRUCT_CLASS_EXTENSION = "$Data";
		public static final String BEHAVIOR_EXTENSION = "Impl";
		
	}
	
	public static class Reflection{
		public static final String VALUE_OF_METHOD_NAME = "valueOf";
	}
	

}
