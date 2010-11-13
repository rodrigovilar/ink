package ink.eclipse.editors.page;

import java.util.List;

import org.eclipse.jface.text.contentassist.ICompletionProposal;


public abstract class DataBlock {
	
	protected ObjectDataBlock parent = null;
	protected int startIndex=-1;
	protected int endIndex = -1;
	protected char[] text;
	private String key;
	
	
	public DataBlock(ObjectDataBlock parent, char[] text, int startIndex, int endIndex) {
		this.text = text;
		this.parent = parent;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		int i=startIndex;
		for(;i<endIndex;i++){
			if(text[i]==' ' || text[i]=='{'){
				break;
			}
		}
		key = String.valueOf(text, startIndex, i-startIndex).trim();
	}
	
	public ObjectDataBlock getParent(){
		return parent;
	}
	
	public String getKey(){
		return key;
	}
	
	@Override
	public String toString() {
		return new String(text, startIndex, endIndex-startIndex);
	}
	
	public abstract List<ICompletionProposal> getProposals(int cursorLocation);
	
}
