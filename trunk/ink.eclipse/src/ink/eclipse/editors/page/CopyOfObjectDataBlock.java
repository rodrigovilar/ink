package ink.eclipse.editors.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.contentassist.ICompletionProposal;

public class CopyOfObjectDataBlock extends DataBlock {
	
	private int startData = -1;
	private Map<String, String> attributes = new HashMap<String, String>();
	private Map<String, List<DataBlock>> innerBlocks = new HashMap<String, List<DataBlock>>();
	private Map<String, String> keyValues = new HashMap<String, String>();

	public CopyOfObjectDataBlock(ObjectDataBlock parent, char[] text, int startIndex, int endIndex) {
		super(parent, text, startIndex, endIndex);
	}

	
	public DataBlock analyzeData(int cursorLocation) {
		extractAttributes();
		StringBuilder currentLine = new StringBuilder(150);
		int startB=0;
		int endB = 0;
		int elementStart=0;
		boolean firstSpace = true; 
		int i=startData;
		String key = null;
		String value = null;
		String blockKey = null;
		for(;i<endIndex;i++){
			switch(text[i]){
			case '{':
				if(startB==endB){
					elementStart = i-currentLine.length()-1;
					int k=0;
					for(;k<currentLine.length();k++){
						if(currentLine.charAt(k)==' '){
							break;
						}
					}
					blockKey = currentLine.substring(0, k).trim(); 
				}
				startB++;
				
				break;
			case '}':
				endB++;
				if(startB == endB){
					addObjectBlock(elementStart+1, i+1, blockKey, cursorLocation);
				}
				currentLine.append(text[i]);
				break;
			case '\n':
				boolean isNewBlock = true;
				firstSpace = true;
				for(int j=currentLine.length()-1;j>=0;j--){
					char c = currentLine.charAt(j);
					if(!Character.isWhitespace(c) && c!='{'){
						isNewBlock=false;
						break;
					}
				}
				if(startB==endB){
					if(!isNewBlock && key!=null && value != null){
						keyValues.put(key, value);
					}
				}
				key = value = null;
				currentLine = new StringBuilder(100);
				break;
			case ' ':
				if(firstSpace){
					firstSpace = false;
					key = extractKey(i);
					value = extractValue(i);
				}
				currentLine.append(text[i]);
				break;
			default:
				if(!Character.isWhitespace(text[i]) || currentLine.length()==0
						//removing comments
						|| currentLine.charAt(currentLine.length()-1)!='{'){
					currentLine.append(text[i]);
				}
			}
		}
		return null;
	}

	private void extractAttributes() {
		String key;
		String value;
		for(int i=startIndex;i<endIndex;i++){
			if(startData < 0){
				if(this.text[i]=='='){
					key = extractKey(i);
					value = extractValue(i);
					if(key!=null && value!=null){
						attributes.put(key, value);
					}
				}
			}
			if(text[i]=='\n'){
				startData = i + 1;
				break;
			}
		}
		
	}

	private String extractValue(int index) {
		StringBuilder result = new StringBuilder(10);
		for(int i=index+1;i<text.length;i++){
			if(Character.isWhitespace(text[i])|| text[i]=='{'){
				break;
			}
			result.append(text[i]);
		}
		if(result.length()==0){
			return null;
		}
		return result.toString();
	}

	private String extractKey(int index) {
		StringBuilder result = new StringBuilder(10);
		for(int i=index-1;i>=0;i--){
			if(Character.isWhitespace(text[i])){
				break;
			}
			result.append(text[i]);
		}
		if(result.length()==0){
			return null;
		}
		return result.reverse().toString();
	}

	private void addObjectBlock(int startIndex, int endIndex, String key, int cursorLocation) {
		DataBlock b = new CopyOfObjectDataBlock(null, text, startIndex, endIndex);
		b.getProposals(cursorLocation);
		addBlock(key, b);
	}


	private void addBlock(String key, DataBlock b) {
		List<DataBlock> l;
		if((l=innerBlocks.get(key))==null){
			l = new ArrayList<DataBlock>();
			innerBlocks.put(key, l);
		}
		l.add(b);
	}


	@Override
	public List<ICompletionProposal> getProposals(int cursorLocation) {
		// TODO Auto-generated method stub
		return null;
	}

}
