package ink.eclipse.editors.page;

import java.util.List;

import org.eclipse.jface.text.contentassist.ICompletionProposal;

public class SimpleDataBlock extends DataBlock {

	public SimpleDataBlock(ObjectDataBlock parent, char[] text, int startIndex, int endIndex) {
		super(parent, text, startIndex, endIndex);
	}

	@Override
	public List<ICompletionProposal> getProposals(int cursorLocation){
		if(cursorLocation>=startIndex && cursorLocation<=endIndex){
			boolean isNewElement = true;
			StringBuilder b = new StringBuilder(10);
			for(int i=cursorLocation;i>=0;i--){
				if(text[i]=='\n'){
					isNewElement = true;
					break;
				}else if(!Character.isWhitespace(text[i])){
					isNewElement = false;
				}
				b.append(text[i]);
			}
			if(isNewElement){
				String classId = parent.getClassId();
			}
			return null;
		}
		return null;
	}

}
