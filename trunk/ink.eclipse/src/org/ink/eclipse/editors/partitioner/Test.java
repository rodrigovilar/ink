package org.ink.eclipse.editors.partitioner;

import java.lang.annotation.Annotation;

public class Test {

	public static void main(String[] args) {
		new SomeAnnotation() {public String value() {return "1";}
        public Class<? extends Annotation> annotationType() {return SomeAnnotation.class;}};

	}

}
