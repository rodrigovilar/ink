package ink.eclipse.editors.page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.ink.core.vm.lang.DataTypeMarker;
import org.ink.core.vm.lang.property.mirror.CollectionPropertyMirror;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.utils.property.PrimitiveAttribute;
import org.ink.core.vm.utils.property.mirror.ListPropertyMirror;
import org.ink.core.vm.utils.property.mirror.MapPropertyMirror;

public class ObjectDataBlock extends DataBlock {
	
	private int startData = -1;
	private Map<String, String> attributes = new HashMap<String, String>();
	private Map<String, List<DataBlock>> innerBlocks = new HashMap<String, List<DataBlock>>();

	public ObjectDataBlock(ObjectDataBlock parent, char[] text, int startIndex, int endIndex) {
		super(parent, text, startIndex, endIndex);
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
		}
		return result;
	}
	
	public String getClassId(){
		return getAttributeValue("class");
	}
	
	@Override
	public DataBlock getBlock(int cursorLocation){
		DataBlock result = null;
		extractAttributes();
		StringBuilder currentLine = new StringBuilder(150);
		int startB=0;
		int endB = 0;
		int elementStart=0;
		int i=startData;
		if(i<0){
			i = startIndex;
		}
		for(;i<endIndex && result==null;i++){
			switch(text[i]){
			case '{':
				if(startB==endB){
					elementStart = i-currentLine.length()-1;
				}
				startB++;
				
				break;
			case '}':
				endB++;
				if(startB == endB){
					result = addObjectBlock(elementStart+1, i+1, cursorLocation);
				}
				currentLine.append(text[i]);
				break;
			case '\n':
				boolean isNewBlock = false;
				for(int j=currentLine.length()-1;j>=0;j--){
					char c = currentLine.charAt(j);
					if(c=='{'){
						isNewBlock=true;
						break;
					}
				}
				String line = currentLine.toString().trim();
				if(startB==endB && !isNewBlock && !line.equals("}") && !line.equals("")){
					result = addSimpleBlock(i-currentLine.length(), i, cursorLocation);
				}
				currentLine = new StringBuilder(100);
				break;
			default:
				//if(!Character.isWhitespace(text[i]) || currentLine.length()==0
						//removing comments
					//	|| currentLine.charAt(currentLine.length()-1)!='{'){
					currentLine.append(text[i]);
				//}
			}
		}
		if(result==null && cursorLocation>=startIndex && cursorLocation<=endIndex){
			result = this;
		}
		return result;
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

	private DataBlock addObjectBlock(int startIndex, int endIndex, int cursorLocation) {
		DataBlock b = new ObjectDataBlock(this, text, startIndex, endIndex);
		return addBlock(b.getKey(), b, cursorLocation);
	}
	
	private DataBlock addSimpleBlock(int startIndex, int endIndex, int cursorLocation) {
		DataBlock b = new SimpleDataBlock(this, text, startIndex, endIndex);
		return addBlock(b.getKey(), b, cursorLocation);
	}


	private DataBlock addBlock(String key, DataBlock b, int cursorLocation) {
		List<DataBlock> l;
		if((l=innerBlocks.get(key))==null){
			l = new ArrayList<DataBlock>();
			innerBlocks.put(key, l);
		}
		l.add(b);
		return b.getBlock(cursorLocation);
	}
	
	
	


	@Override
	protected List<ICompletionProposal> getNewLineProposals(int cursorLocation) {
		List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();
		String classId = getClassId();
		if(classId != null){
			Collection<PropertyMirror> avaliableProps = InkContentAssistUtil.getPropertiesMirrors(classId, innerBlocks.keySet());
			for(PropertyMirror pm : avaliableProps){
				if(pm.isMutable()){
					getProposal(cursorLocation, result, pm);
				}
			}
		}else if(parent!=null){
			classId = parent.getClassId();
			PropertyMirror pm = InkContentAssistUtil.getPropertyMirror(classId, getKey(), new ArrayList<String>());
			if(pm!=null && pm.getTypeMarker()==DataTypeMarker.Collection){
				switch(((CollectionPropertyMirror)pm).getCollectionTypeMarker()){
				case List:
					getProposal(cursorLocation, result,((ListPropertyMirror)pm).getItemMirror());
					break;
				case Map:
					getProposal(cursorLocation, result,((MapPropertyMirror)pm).getKeyMirror());
					getProposal(cursorLocation, result,((MapPropertyMirror)pm).getKeyMirror());
					break;
				}
			}
			
		}
		return result;
	}

	private void getProposal(int cursorLocation,
			List<ICompletionProposal> allProposals, PropertyMirror pm) {
		switch(pm.getTypeMarker()){
		case Class:
			allProposals.add(new CompletionProposal(pm.getName() +" ", cursorLocation, 0, pm.getName().length() + 1, null, pm.getName(), null, null));
			break;
		case Collection:
			allProposals.add(new CompletionProposal(pm.getName() + "{\n\t\t\n\t}", cursorLocation, 0, pm.getName().length() +"{\n\t\t\n\t}".length() -3, null, pm.getName(), null, null));
			break;
		case Enum:
			allProposals.add(new CompletionProposal(pm.getName() + " \"\"", cursorLocation, 0, pm.getName().length() + " \"\"".length()-1, null, pm.getName(), null, null));
			break;
		case Primitive:
			PrimitiveAttribute pa = pm.getTargetBehavior();
			if(pa.getType().isNumeric() || pa.getType().isBoolean()){
				allProposals.add(new CompletionProposal(pm.getName() + " ", cursorLocation, 0, pm.getName().length() + 1, null, pm.getName(), null, null));
			}else{
				allProposals.add(new CompletionProposal(pm.getName() + " \"\"", cursorLocation, 0, pm.getName().length() + " \"\"".length()-1, null, pm.getName(), null, null));
			}
			break;
			
		}
	}

}
