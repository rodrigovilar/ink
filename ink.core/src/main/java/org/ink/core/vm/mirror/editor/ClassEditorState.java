package org.ink.core.vm.mirror.editor;


/**
 * @author Lior Schachter
 */
public interface ClassEditorState extends ObjectEditorState{
	public class Data extends ObjectEditorState.Data implements ClassEditorState{
	}
}
