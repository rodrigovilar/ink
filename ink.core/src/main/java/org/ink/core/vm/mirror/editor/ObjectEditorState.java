package org.ink.core.vm.mirror.editor;

import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;

/**
 * @author Lior Schachter
 */
@CoreClassSpec(metaclass=ObjectEditorClassState.class)
public interface ObjectEditorState extends InkObjectState{
	
	public class Data extends InkObjectState.Data implements ObjectEditorState{
		
	}

}
