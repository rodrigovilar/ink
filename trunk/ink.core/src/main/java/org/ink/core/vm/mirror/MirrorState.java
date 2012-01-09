package org.ink.core.vm.mirror;

import org.ink.core.vm.mirror.editor.ObjectEditor;
import org.ink.core.vm.mirror.editor.ObjectEditorState;
import org.ink.core.vm.traits.TraitState;

/**
 * @author Lior Schachter
 */
public interface MirrorState extends TraitState {

	public static final byte p_editor = 1;

	public ObjectEditor getEditor();

	public void setEditor(ObjectEditorState value);

	public class Data extends TraitState.Data implements MirrorState {

		@Override
		public ObjectEditor getEditor() {
			return (ObjectEditor) getValue(p_editor);
		}

		@Override
		public void setEditor(ObjectEditorState value) {
			setValue(p_editor, value);
		}

	}

}
