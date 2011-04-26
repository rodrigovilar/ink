package org.ink.eclipse.editors.page;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;


public class PageAnalyzer {
	
	private String text;
	private int cursorLocation;
	private ObjectDataBlock currentElement;
	List<DataBlock> elements = new ArrayList<DataBlock>();
	private String ns;
	
	public PageAnalyzer(String ns, String text, int cursorLocation) {
		this.text = text;
		this.ns = ns;
		this.cursorLocation = cursorLocation;
		scan();
	}
	
	public List<ICompletionProposal> getContentAssist(){
		ObjectDataBlock currentElement = getCurrentElement();
		List<ICompletionProposal> result = null;
		if(currentElement!=null){
			DataBlock b = currentElement.getBlock(cursorLocation);
			result = b.getContentAssist(cursorLocation);
		}else{
			result = getResultForNewElement();
		}
		return result;
	}

	private List<ICompletionProposal> getResultForNewElement() {
		List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();
		result.add(new CompletionProposal("Class ", cursorLocation, 0, "Class ".length(), null, null, null, null));
		result.add(new CompletionProposal("Object ", cursorLocation, 0,"Object ".length(), null, null, null, null));
		return result;
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
				if(startB == endB && currentLine.toString().trim().length() > 0){
					addElement(cs, i-currentLine.length(), i+1);
				}
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
		if(currentLine.toString().trim().length()>0){
			addElement(cs, i-currentLine.length(), i);
		}
		
	}

	private void addElement(char[] text, int blockStart, int blockEnd) {
		ObjectDataBlock block;
		block = new ObjectDataBlock(ns, null, text, blockStart, blockEnd);
		elements.add(block);
		if(blockStart <= cursorLocation && blockEnd >= cursorLocation){
			currentElement = block;
		}
	}
	
	public ObjectDataBlock getCurrentElement(){
		return currentElement;
	}
	
	

}