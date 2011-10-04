package org.ink.eclipse.editors.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private final Map<String, String> attributes = new HashMap<String, String>();
	protected int endIndex = -1;
	protected String startLine;
	protected char[] text;
	private final String key;
	protected String ns;
	protected int startData = -1;


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
		extractAttributes();
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
						if(key.equals("class") || key.equals("super") || key.equals("id")){
							if(value.indexOf(InkNotations.Path_Syntax.NAMESPACE_DELIMITER_C) < 0){
								value = ns + InkNotations.Path_Syntax.NAMESPACE_DELIMITER_C + value;
							}
						}
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

	public ObjectDataBlock getParent(){
		return parent;
	}

	public String getAttributeValue(String key){
		String result = attributes.get(key);
		if(result!=null){
			result = result.trim();
			StringBuilder b = new StringBuilder(result.length());
			for(char c : result.toCharArray()){
				if(c!='\"'){
					b.append(c);
				}
			}
			result = b.toString();
			if(result.indexOf(InkNotations.Path_Syntax.NAMESPACE_DELIMITER_C) < 0){
				result = ns + InkNotations.Path_Syntax.NAMESPACE_DELIMITER_C + result;
			}
		}
		return result;
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
		StringBuilder b = new StringBuilder(10);
		if(cursorLocation>=startIndex && cursorLocation <=endIndex){
			boolean isNewLineProposal = true;
			int i=cursorLocation-1;
			if(i >= text.length){
				i = text.length-1;
			}
			if(text[i]==' '){
				for(;i>=0;i--){
					if(text[i]=='\n'){
						break;
					}else if(!Character.isWhitespace(text[i])){
						isNewLineProposal = false;
						break;
					}
				}
			}else{
				boolean foundChar = false;
				boolean foundWhitspace = false;
				boolean foundGeresh = false;
				for(;i>=0;i--){
					if(text[i]=='\n'){
						break;
					}else if(!Character.isWhitespace(text[i])){
						if(text[i]=='\"'){
							foundGeresh = true;
							continue;
						}
						if(foundWhitspace && foundChar){
							isNewLineProposal = false;
							break;
						}
						foundChar = true;
					}else{
						foundWhitspace = true;

					}
					if(!foundGeresh){
						b.append(text[i]);
					}
				}
			}
			String prefix = b.reverse().toString().trim().toLowerCase();
			if(prefix.length()>0){
				if(prefix.charAt(prefix.length()-1)=='\"'){
					prefix = "";
				}else if(prefix.indexOf('\"')>=0){
					prefix = prefix.substring(prefix.indexOf('\"')+1, prefix.length());
				}
			}
			if(isNewLineProposal){
				result = getNewLineProposals(cursorLocation, prefix);
			}else{
				result = getInlineProposals(cursorLocation, prefix);
			}
		}
		return result;
	}

	protected CompletionProposal createAttributeProposal(String attName, int cursorLocation, int count, boolean stringAtt) {
		String postfix;
		if(stringAtt){
			postfix = "=\"\"";
		}else{
			postfix = "= ";
		}
		return new CompletionProposal(attName + postfix, cursorLocation-count, count, attName.length() +postfix.length()-1, null, attName, null, null);
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

	protected void addRefPropsals(int cursorLocation, String prefix, List<ICompletionProposal> result) {
		PropertyMirror pm;
		pm = InkUtils.getPropertyMirror(getContainingClass(), getKey(), getPathToClassBlock());
		if(pm.getTypeMarker()==DataTypeMarker.Class){
			String constraintClass = ((ReferenceMirror)pm).getPropertyType().reflect().getId();
			List<String> options = InkUtils.getInstancesIds(ns, constraintClass, true);
			for(String id : options){
				addIdProposal(result, cursorLocation, id, prefix);
			}
		}
	}

	protected List<ICompletionProposal> getInnerBlockProposals(int cursorLocation, String textString, int newLineLoc, int count, int spaceLoc, String prefix){
		List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();
		String line = textString.substring(newLineLoc, textString.indexOf('\n', cursorLocation));
		if(text[cursorLocation-1]==' '){
			if(!line.contains("class") && !line.contains("ref")){
				result.add(createAttributeProposal("class", cursorLocation, count, true));
			}
			if(!line.contains("ref")){
				result.add(createAttributeProposal("ref", cursorLocation, count, true));
			}
			if(result.isEmpty()){
				result.add(new CompletionProposal("{\n\t\n}", cursorLocation, 0, "{\n\t\n}".length()-2, null, "{", null, null));
			}
			if(!line.contains("super")){
				result.add(createAttributeProposal("super", cursorLocation, count, true));
			}
			if(line.contains("class") && !line.contains("{")){
				String tabs = calculateTabs();
				result.add(new CompletionProposal("{\n" + tabs+"\n"+tabs.substring(0, tabs.length()-1)+"}", cursorLocation, 0, new String("{\n" + tabs+"\n}").length()-2, null, "{", null, null));
			}
		}else{
			StringBuilder buf = new StringBuilder(10);
			boolean found = false;
			int i=cursorLocation-1;
			for(;i>=1;i--){
				char c = textString.charAt(i);
				if(Character.isWhitespace(c)){
					break;
				}
				else if(c=='\"'){
					if(textString.charAt(i-1)=='='){
						found = true;
					}
					break;
				}
				buf.append(c);
			}
			if(found){
				String attr = textString.substring(spaceLoc + 1, cursorLocation-(2+buf.length()));
				PropertyMirror pm = InkUtils.getPropertyMirror(getContainingClass(), getKey(), getPathToClassBlock());
				if(attr.equals("class")){
					if(pm.getTypeMarker()==DataTypeMarker.Class){
						Mirror m = ((ReferenceMirror)pm).getPropertyType().reflect();
						String constraintClass = m.getId();
						List<String> options = InkUtils.getSubClasses(ns, constraintClass, true, false);
						if(!m.isAbstract()){
							options.add(0, constraintClass);
						}
						for(String id : options){
							addIdProposal(result, cursorLocation, id, prefix);
						}
					}
				}else if(attr.equals("super")){
					for(String id : getSuperProposals(line, pm)){
						result.add(new CompletionProposal(id, cursorLocation, 0, id.length()+1, null, getDisplayString(id), null, null));
					}
				}else if(attr.equals("ref")){
					addRefPropsals(cursorLocation, prefix, result);
				}
			}
			else if(!line.contains("ref") && result.isEmpty() && (text[cursorLocation-1]!='\"' || text.length > cursorLocation && text[cursorLocation+1]!='\"')){
				String tabs = calculateTabs();
				String str = "{\n"+tabs+"\n"+tabs.substring(1) +"}";
				result.add(new CompletionProposal(str, cursorLocation, 0, str.length()-(tabs.length()+1), null, "{", null, null));
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

	protected List<ICompletionProposal> getInlineProposals(int cursorLocation, String prefix) {
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
			return getNewElementProposals(cursorLocation, textString, newLineLoc, count, spaceLoc, prefix);
		}else{
			return getInnerBlockProposals(cursorLocation, textString, newLineLoc, count, spaceLoc, prefix);
		}
	}

	protected List<ICompletionProposal> getNewElementProposals(int cursorLocation, String textString, int newLineLoc, int count, int spaceLoc, String prefix){
		List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();
		int lastIndex = textString.indexOf('\n', cursorLocation);
		if(lastIndex < 0){
			lastIndex = endIndex;
		}
		String line = textString.substring(newLineLoc, lastIndex).trim();
		if(text[cursorLocation-1]==' '){
			if(!line.contains("id")){
				result.add(createAttributeProposal("id", cursorLocation, count, true));
			}
			if(!line.contains("class")){
				result.add(createAttributeProposal("class", cursorLocation, count, true));
			}
			if(result.isEmpty()){
				result.add(new CompletionProposal("{\n\t\n}", cursorLocation, 0, "{\n\t\n}".length()-2, null, "{", null, null));
			}
			if(!line.contains("super")){
				result.add(createAttributeProposal("super", cursorLocation, count, true));
			}
			if(!line.contains("abstract")){
				result.add(createAttributeProposal("abstract", cursorLocation, count, false));
			}
			if(result.isEmpty() && !(text[cursorLocation-1]=='\"' && text.length > cursorLocation+1 && text[cursorLocation+1]=='\"')){
				result.add(new CompletionProposal("{\n\t\n}", cursorLocation, 0, "{\n\t\n}".length()-2, null, "{", null, null));
			}
		}else{
			int offset = 0;
			boolean found = false;
			int i=cursorLocation-1;
			for(;i>=1;i--){
				char c = textString.charAt(i);
				if(Character.isWhitespace(c)){
					break;
				}
				else if(c=='\"'){
					if(textString.charAt(i-1)=='='){
						found = true;
						offset+=2;
					}
					break;
				}else if(textString.charAt(i)=='=' && textString.substring(i-"abstract".length(),i).equals("abstract")){
					found = true;
					offset++;
					break;
				}
				offset++;
			}
			if(found){
				String attr = textString.substring(spaceLoc + 1, cursorLocation-offset);
				if(attr.equals("class")){
					List<String> options = null;
					String superId = attributes.get("super");
					if(superId!=null){
						String classId = InkUtils.getClassId(superId);
						if(classId!=null){
							options = InkUtils.getSubClasses(ns, classId, true, true);
							addIdProposal(result, cursorLocation, classId, prefix);
						}
					}
					if(options==null){
						if(line.startsWith("Object") || line.startsWith("Ink ")){
							options = InkUtils.getSubClasses(ns, CoreNotations.Ids.INK_OBJECT, true, true);
							addIdProposal(result, cursorLocation, CoreNotations.Ids.INK_OBJECT, prefix);
						}else{
							options = InkUtils.getSubClasses(ns, CoreNotations.Ids.INK_CLASS, true, false);
							addIdProposal(result, cursorLocation, CoreNotations.Ids.INK_CLASS, prefix);
						}
					}
					for(String id : options){
						addIdProposal(result, cursorLocation, id, prefix);
					}
				}else if(attr.equals("super")){
					String elementId = attributes.get("id");
					for(String id : getSuperProposals(line, null)){
						if(elementId==null || !elementId.equals(id) ){
							addIdProposal(result, cursorLocation, id, prefix);
						}
					}
				}else if(attr.equals("abstract")){
					result.add(new CompletionProposal("true", cursorLocation, 0, "true".length(), null, "true", null, null));
					result.add(new CompletionProposal("false", cursorLocation, 0, "false".length(), null, "false", null, null));
				}else{
					result.add(new CompletionProposal("{\n\t\n}", cursorLocation, 0, "{\n\t\n}".length()-2, null, "{", null, null));
				}
			}else if (prefix.length()>0){
				if(isPrefix("class", prefix, false) && !line.contains("class")){
					result.add(createAttributeProposal("class", cursorLocation, count, true));
				}
				if(isPrefix("id", prefix, false) && !line.contains("id")){
					result.add(createAttributeProposal("id", cursorLocation, count, true));
				}
				if(isPrefix("super", prefix, false) && !line.contains("super")){
					result.add(createAttributeProposal("super", cursorLocation, count, true));
				}
				if(isPrefix("abstract", prefix, false) && !line.contains("abstract")){
					result.add(createAttributeProposal("abstract", cursorLocation, count, true));
				}
				if(isPrefix("Object", prefix, false) && !line.contains("Object") && !line.contains("Class")){
					result.add(createAttributeProposal("Object", cursorLocation, count, true));
				}
				if(isPrefix("Class", prefix, false) && !line.contains("Object") && !line.contains("Class")){
					result.add(createAttributeProposal("Class", cursorLocation, count, true));
				}
			}else{
				result.add(new CompletionProposal("{\n\t\n}", cursorLocation, 0, "{\n\t\n}".length()-2, null, "{", null, null));
			}
		}
		return result;
	}

	protected List<String> getSuperProposals(String line, PropertyMirror pm){
		String classAtt = attributes.get("class");
		List<String> result = new ArrayList<String>();
		if(classAtt!=null){
			List<String> relevantClasses = InkUtils.getAllSupers(classAtt);
			relevantClasses.add(classAtt);
			if(pm==null){
				result = InkUtils.getInstances(ns, relevantClasses);
			}
		}
		if(pm==null || pm.getTypeMarker()!=DataTypeMarker.Class){
			return result;
		}
		Mirror m = ((ReferenceMirror)pm).getPropertyType().reflect();
		String constraintClass = m.getId();
		List<String> options = InkUtils.getInstancesIds(ns, constraintClass, true);
		result.retainAll(options);
		return result;
	}

	protected String getId(String id) {
		if(id.startsWith(ns)){
			return id.substring(ns.length()+1, id.length());
		}
		return id;
	}

	public char[] getText(){
		return text;
	}

	protected void addIdProposal(List<ICompletionProposal> all, int cursorLocation, String id, String prefix) {
		boolean isPrefix = isPrefix(id, prefix);
		if(!isPrefix && id.indexOf(InkNotations.Path_Syntax.NAMESPACE_DELIMITER_C)>=0){
			String shortId = id.substring(id.indexOf(InkNotations.Path_Syntax.NAMESPACE_DELIMITER_C)+1, id.length());
			if(shortId.startsWith(prefix)){
				isPrefix = isPrefix(shortId, prefix);
			}
		}
		if(isPrefix){
			String usedId = getId(id);
			if(id.toLowerCase().contains(":"+prefix.toLowerCase())){
				all.add(0, new CompletionProposal(usedId, cursorLocation-prefix.length(), prefix.length(), usedId.length()+1, null, getDisplayString(id), null, null));
			}else{
				all.add(new CompletionProposal(usedId, cursorLocation-prefix.length(), prefix.length(), usedId.length()+1, null, getDisplayString(id), null, null));
			}

		}
	}


	protected boolean isPrefix(String str, String prefix) {
		return isPrefix(str, prefix, true);
	}

	protected boolean isPrefix(String str, String prefix, boolean contains) {
		if(prefix.length()==0){
			return true;
		}
		if(contains && str.toLowerCase().indexOf(prefix)>=0){
			return true;
		}
		if(!contains && str.toLowerCase().startsWith(prefix)){
			return true;
		}
		return false;
	}

	public int getStartIndex() {
		return startIndex;
	}

	private String extractValue(int index) {
		StringBuilder result = new StringBuilder(10);
		for(int i=index+1;i<text.length;i++){
			if(Character.isWhitespace(text[i])|| text[i]=='{'){
				break;
			}
			if(text[i]!='\"') {
				result.append(text[i]);
			}
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


	protected abstract List<ICompletionProposal> getNewLineProposals(int cursorLocation, String prefix);

}
