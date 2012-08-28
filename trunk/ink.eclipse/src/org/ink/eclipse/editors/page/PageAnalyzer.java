package org.ink.eclipse.editors.page;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

public class PageAnalyzer {

	private final String text;
	private final int cursorLocation;
	private ObjectDataBlock currentElement;
	List<DataBlock> elements = new ArrayList<DataBlock>();
	private final String ns;
	private final int lineNumber;

	public PageAnalyzer(String ns, String text, int cursorLocation, int lineNumber) {
		this.text = text;
		this.ns = ns;
		this.cursorLocation = cursorLocation;
		this.lineNumber = lineNumber;
		scan();
	}

	public List<ICompletionProposal> getContentAssist() {
		ObjectDataBlock currentElement = getCurrentElement();
		List<ICompletionProposal> result = null;
		if (currentElement != null) {
			DataBlock b = currentElement.getBlock(cursorLocation);
			result = b.getContentAssist(lineNumber, cursorLocation);
		} else {
			result = getResultForNewElement();
		}
		return result;
	}

	public ObjectDataBlock getRootElement() {
		DataBlock result = getCurrentElement();
		while (result.getParent() != null) {
			result = result.getParent();
		}
		return (ObjectDataBlock) result;
	}

	private List<ICompletionProposal> getResultForNewElement() {
		List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();
		result.add(new CompletionProposal("Class ", cursorLocation, 0, "Class ".length(), null, null, null, null));
		result.add(new CompletionProposal("Object ", cursorLocation, 0, "Object ".length(), null, null, null, null));
		result.add(new CompletionProposal("Ink ", cursorLocation, 0, "Ink ".length(), null, null, null, null));
		return result;
	}

	private void scan() {
		StringBuilder currentLine = new StringBuilder(150);
		int startB = 0;
		int endB = 0;
		char[] cs = text.toCharArray();
		int elementStart = 0;
		boolean checkStartElement = false;
		int i = 0;
		StringBuilder lastLine = currentLine;
		boolean comment = false;
		for (; i < cs.length; i++) {
			if (comment && cs[i] != '\n') {
				continue;
			}
			switch (cs[i]) {
			case '{':
				startB++;
				break;
			case '}':
				endB++;
				if (startB == endB) {
					addElement(cs, elementStart, i + 1);
					elementStart = i + 2;
				}
				break;
			case '\n':
				comment = false;
				checkStartElement = true;
				lastLine = currentLine;
				if (startB == endB && currentLine.toString().trim().length() > 0) {
					addElement(cs, i - currentLine.length(), i + 1);
					elementStart = i + 2;
				}
				currentLine = new StringBuilder(100);
				break;
			case ' ':
				if (checkStartElement) {
					String currentLineString = currentLine.toString();
					if (currentLineString.startsWith("Object") || currentLineString.startsWith("Class") || currentLineString.startsWith("Ink") || currentLineString.startsWith("InkInk")) {
						if (startB != endB) {
							addElement(cs, elementStart, i - currentLineString.length());
						}
						String lastLineSting = lastLine.toString();
						if (lastLineSting.startsWith("Object") || lastLineSting.startsWith("Class") || currentLineString.startsWith("Ink") || currentLineString.startsWith("InkInk")) {
							addElement(cs, i - lastLineSting.length() - currentLineString.length() - 1, i - currentLineString.length() - 1);
						}
						elementStart = i - currentLine.length();
						startB = endB = 0;
					}
					checkStartElement = false;
				}
				currentLine.append(cs[i]);
				break;
			case '/':
				if (cs.length > i + 1 && cs[i + 1] == '/') {
					comment = true;
					break;
				}
			default:
				currentLine.append(cs[i]);
			}
		}
		if (currentLine.toString().trim().length() > 0) {
			addElement(cs, i - currentLine.length(), i);
		}

	}

	private void addElement(char[] text, int blockStart, int blockEnd) {
		ObjectDataBlock block;
		block = new ObjectDataBlock(ns, null, text, blockStart, blockEnd);
		elements.add(block);
		if (blockStart <= cursorLocation && blockEnd >= cursorLocation) {
			currentElement = block;
		}
	}

	public ObjectDataBlock getCurrentElement() {
		return currentElement;
	}

}
