package ink.eclipse.editors.page;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.ink.core.vm.factory.internal.CoreNotations;
import org.ink.core.vm.lang.DataTypeMarker;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.utils.InkNotations;
import org.ink.core.vm.utils.property.mirror.ReferenceMirror;
import org.ink.eclipse.utils.InkUtils;


public abstract class DataBlock {

	protected ObjectDataBlock parent = null;
	protected int startIndex=-1;
	protected int endIndex = -1;
	protected String startLine;
	protected char[] text;
	private final String key;
	protected String ns;


	public DataBlock(String namespace, ObjectDataBlock parent, char[] text, int startIndex, int endIndex) {
		this.ns = namespace;
		this.text = text;
		this.parent = parent;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		int i=startIndex;
		boolean cFound = false;
		for(;i<endIndex;i++){
			if(!Character.isWhitespace(text[i])){
				cFound = true;
			}
			if(cFound && (text[i]==' ' || text[i]=='{')){
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

	public abstract DataBlock getBlock(int cursorLocation);

	public List<ICompletionProposal> getContentAssist(int cursorLocation) {
		List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();
		if(cursorLocation>=startIndex && cursorLocation <=endIndex){
			boolean isNewLineProposal = true;
			StringBuilder b = new StringBuilder(10);
			int i=cursorLocation-1;
			if(i >= text.length){
				i = text.length-1;
			}
			for(;i>=0;i--){
				if(text[i]=='\n'){
					break;
				}else if(!Character.isWhitespace(text[i])){
					isNewLineProposal = false;
					break;
				}
				b.append(text[i]);
			}
			if(isNewLineProposal){
				result = getNewLineProposals(cursorLocation);
			}else{
				result = getInlineProposals(cursorLocation);
			}
		}
		return result;
	}

	protected CompletionProposal createAttributeProposal(String attName, int cursorLocation,
			int count) {
		return new CompletionProposal(attName + "=\"\"", cursorLocation-count, count, attName.length() +"=\"\"".length()-1, null, attName, null, null);
	}

	protected String calculateTabs(){
		String tabs = "\t";
		DataBlock parent = getParent();
		while(parent!=null){
			tabs += "\t";
			parent = parent.getParent();
		}
		return tabs;
	}

	protected List<ICompletionProposal> getInnerBlockProposals(int cursorLocation, String textString, int newLineLoc, int count, int spaceLoc){
		List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();
		String line = textString.substring(newLineLoc, textString.indexOf('\n', cursorLocation));
		if(text[cursorLocation-1]==' '){
			if(!line.contains("class") && !line.contains("ref")){
				result.add(createAttributeProposal("class", cursorLocation, count));
			}
			if(!line.contains("ref")){
				result.add(createAttributeProposal("ref", cursorLocation, count));
			}
			if(result.isEmpty()){
				result.add(new CompletionProposal("{\n\t\n}", cursorLocation, 0, "{\n\t\n}".length()-2, null, "{", null, null));
			}
			if(!line.contains("super")){
				result.add(createAttributeProposal("super", cursorLocation, count));
			}
			if(line.contains("class") && !line.contains("{")){
				String tabs = calculateTabs();
				result.add(new CompletionProposal("{\n" + tabs+"\n"+tabs.substring(0, tabs.length()-1)+"}", cursorLocation, 0, new String("{\n" + tabs+"\n}").length()-2, null, "{", null, null));
			}
		}else if(textString.charAt(cursorLocation-1)=='\"'){
			String attr = textString.substring(spaceLoc + 1, cursorLocation-2);
			if(attr.equals("class")){
				PropertyMirror pm = InkUtils.getPropertyMirror(getContainingClass(), getKey(), getPathToClassBlock());
				if(pm.getTypeMarker()==DataTypeMarker.Class){
					Mirror m = ((ReferenceMirror)pm).getPropertyType().reflect();
					String constraintClass = m.getId();
					List<String> options = InkUtils.getSubClasses(ns, constraintClass, true, false);
					if(!m.isAbstract()){
						options.add(0, constraintClass);
					}
					for(String id : options){
						result.add(new CompletionProposal(id, cursorLocation, 0, id.length()+1, null, getDisplayString(id), null, null));
					}
				}
			}else if(attr.equals("super")){
				for(String id : getSuperProposals(line)){
					result.add(new CompletionProposal(id, cursorLocation, 0, id.length()+1, null, getDisplayString(id), null, null));
				}
			}
		}else{
			if(!line.contains("ref") && result.isEmpty() && (text[cursorLocation-1]!='\"' || text.length > cursorLocation && text[cursorLocation+1]!='\"')){
				result.add(new CompletionProposal("{\n\t\n}", cursorLocation, 0, "{\n\t\n}".length()-2, null, "{", null, null));
			}
		}
		return result;
	}

	protected String getDisplayString(String id) {
		int loc = id.indexOf(':');
		String displayString = id.substring(loc+1,id.length()) + " - " + id.substring(0, loc);
		return displayString;
	}

	protected List<String> getPathToClassBlock(){
		List<String> result = new ArrayList<String>();
		ObjectDataBlock p = parent;
		while(p!=null){
			if(p.getClassId()==null){
				result.add(0, p.getKey());
				p=p.parent;
			}
			else{
				p = null;
			}
		}

		return result;
	}

	public String getContainingClass(){
		String result = null;
		if(parent!=null){
			result = parent.getAttributeValue("class");
			if(result==null && parent!=null){
				result = parent.getContainingClass();
			}
		}
		return result;
	}

	protected List<ICompletionProposal> getInlineProposals(int cursorLocation) {
		boolean startElement = true;
		int count = 0;
		boolean toContinue = true;
		int newLineLoc = 0;
		int spaceLoc = -1;
		String textString = new String(text);
		for(int i=cursorLocation-1;i>0&&toContinue;i--){
			char c = textString.charAt(i);
			switch(c){
			case '\n':
				newLineLoc = i;
				toContinue = false;
				break;
			case ' ':
				if(spaceLoc<0){
					spaceLoc = i;
				}
				startElement = false;
				break;
			default:
				if(startElement){
					count++;
				}
				break;
			}

		}
		if(parent==null){
			return getNewElementProposals(cursorLocation, textString, newLineLoc, count, spaceLoc);
		}else{
			return getInnerBlockProposals(cursorLocation, textString, newLineLoc, count, spaceLoc);
		}
	}

	protected List<ICompletionProposal> getNewElementProposals(int cursorLocation, String textString, int newLineLoc, int count, int spaceLoc){
		List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();
		int lastIndex = textString.indexOf('\n', cursorLocation);
		if(lastIndex < 0){
			lastIndex = endIndex;
		}
		String line = textString.substring(newLineLoc, lastIndex).trim();
		if(text[cursorLocation-1]==' '){
			if(!line.contains("id")){
				result.add(createAttributeProposal("id", cursorLocation, count));
			}
			if(!line.contains("class")){
				result.add(createAttributeProposal("class", cursorLocation, count));
			}
			if(result.isEmpty()){
				result.add(new CompletionProposal("{\n\t\n}", cursorLocation, 0, "{\n\t\n}".length()-2, null, "{", null, null));
			}
			if(!line.contains("super")){
				result.add(createAttributeProposal("super", cursorLocation, count));
			}
			if(result.isEmpty() && !(text[cursorLocation-1]=='\"' && text.length > cursorLocation+1 && text[cursorLocation+1]=='\"')){
				result.add(new CompletionProposal("{\n\t\n}", cursorLocation, 0, "{\n\t\n}".length()-2, null, "{", null, null));
			}
		}else if(textString.charAt(cursorLocation-1)=='\"'){
			String attr = textString.substring(spaceLoc + 1, cursorLocation-2);
			if(attr.equals("class")){
				List<String> options;
				if(line.startsWith("Object")){
					options = InkUtils.getSubClasses(ns, CoreNotations.Ids.INK_OBJECT, true, false);
				}else{
					options = InkUtils.getSubClasses(ns, CoreNotations.Ids.INK_CLASS, true, false);
					options.add(CoreNotations.Ids.INK_CLASS);
				}
				for(String id : options){
					result.add(new CompletionProposal(id, cursorLocation, 0, id.length()+1, null, getDisplayString(id), null, null));
				}
			}else if(attr.equals("super")){
				String elementId = extractAttributeValue(line, "id");
				if(elementId!=null){
					elementId = ns +InkNotations.Path_Syntax.NAMESPACE_DELIMITER_C + elementId;
				}
				for(String id : getSuperProposals(line)){
					if(elementId==null || !elementId.equals(id) ){
						result.add(new CompletionProposal(id, cursorLocation, 0, id.length()+1, null, getDisplayString(id), null, null));
					}
				}
			}else{
				result.add(new CompletionProposal("{\n\t\n}", cursorLocation, 0, "{\n\t\n}".length()-2, null, "{", null, null));
			}
		}
		return result;
	}

	protected List<String> getSuperProposals(String line){
		String classAtt = extractAttributeValue(line, "class");
		List<String> relevantClasses = InkUtils.getAllSupers(classAtt);
		relevantClasses.add(classAtt);
		return InkUtils.getInstances(ns, relevantClasses);
	}

	private String extractAttributeValue(String line, String attName){
		String result = null;
		int attValueStartIndex = line.indexOf(attName+ "=\"")+attName.length() + "=\"".length();
		if(attValueStartIndex > 0){
			int attValueEndIndex = line.indexOf("\"", attValueStartIndex);
			if(attValueEndIndex > attValueStartIndex){
				result = line.substring(attValueStartIndex, attValueEndIndex);
			}
		}
		return result;
	}

	protected abstract List<ICompletionProposal> getNewLineProposals(int cursorLocation);

}
