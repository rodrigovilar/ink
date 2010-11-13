package ink.eclipse.editors.page;

import java.util.ArrayList;
import java.util.List;


public class PageAnalyzer {
	
	private String text;
	private int cursorLocation;
	private DataBlock currentElement;
	List<DataBlock> elements = new ArrayList<DataBlock>();
	public PageAnalyzer(String text, int cursorLocation) {
		this.text = text;
		this.cursorLocation = cursorLocation;
		scan();
	}
	
	private void scan(){
		StringBuilder currentLine = new StringBuilder(150);
		int startB=0;
		int endB = 0;
		char[] cs = text.toCharArray();
		int elementStart=0;
		boolean checkStartElement = false;
		int i=0;
		StringBuilder lastLine = currentLine;
		for(;i<cs.length;i++){
			switch(cs[i]){
			case '{':
				startB++;
				break;
			case '}':
				endB++;
				if(startB == endB){
					addElement(cs, elementStart, i+1);
				}
				break;
			case '\n':
				checkStartElement = true;
				lastLine = currentLine;
				currentLine = new StringBuilder(100);
				break;
			case ' ':
				if(checkStartElement){
					String currentLineString = currentLine.toString();
					if(currentLineString.startsWith("Object") || currentLineString.startsWith("Class")){
						if(startB!=endB){
							addElement(cs, elementStart, i-currentLineString.length());
						}
						String lastLineSting = lastLine.toString();
						if(lastLineSting.startsWith("Object") || lastLineSting.startsWith("Class")){
							addElement(cs, i-lastLineSting.length()-currentLineString.length()-1, i-currentLineString.length()-1);
						}
						elementStart = i-currentLine.length();
						startB = endB = 0;
					}
					checkStartElement = false;
				}
				currentLine.append(cs[i]);
				break;
			default:
				currentLine.append(cs[i]);
			}
		}
	}

	private void addElement(char[] text, int blockStart, int blockEnd) {
		DataBlock block;
		block = new ObjectDataBlock(null, text, blockStart, blockEnd);
		elements.add(block);
		if(blockStart <= cursorLocation && blockEnd >= cursorLocation){
			currentElement = block;
		}
	}
	
	public DataBlock getCurrentElement(){
		return currentElement;
	}
	
	

}
