package org.ink.eclipse.editors.page;

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
import org.ink.core.vm.types.CollectionTypeMarker;
import org.ink.core.vm.utils.property.Dictionary;
import org.ink.core.vm.utils.property.ElementsDictionary;
import org.ink.core.vm.utils.property.PrimitiveAttribute;
import org.ink.core.vm.utils.property.mirror.ListPropertyMirror;
import org.ink.core.vm.utils.property.mirror.MapPropertyMirror;
import org.ink.eclipse.utils.InkUtils;

public class ObjectDataBlock extends DataBlock {

	private final Map<String, List<DataBlock>> innerBlocks = new HashMap<String, List<DataBlock>>();

	public ObjectDataBlock(String namespace, ObjectDataBlock parent, char[] text, int startIndex, int endIndex) {
		super(namespace, parent, text, startIndex, endIndex);
	}


	public String getClassId(){
		return getAttributeValue("class");
	}

	@Override
	public DataBlock getBlock(int cursorLocation){
		DataBlock result = null;
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


	private DataBlock addObjectBlock(int startIndex, int endIndex, int cursorLocation) {
		DataBlock b = new ObjectDataBlock(ns, this, text, startIndex, endIndex);
		return addBlock(b.getKey(), b, cursorLocation);
	}

	private DataBlock addSimpleBlock(int startIndex, int endIndex, int cursorLocation) {
		DataBlock b = new SimpleDataBlock(ns, this, text, startIndex, endIndex);
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


	public List<DataBlock> getBlocks(String key){
		return innerBlocks.get(key);
	}


	@Override
	protected List<ICompletionProposal> getNewLineProposals(int cursorLocation, String prefix) {
		List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();
		String classId = getClassId();
		if(classId != null){
			Collection<PropertyMirror> avaliableProps = InkUtils.getPropertiesMirrors(classId, innerBlocks.keySet());
			for(PropertyMirror pm : avaliableProps){
				if(pm.isMutable()){
					getProposal(cursorLocation, result, pm, prefix);
				}
			}
		}else if(parent!=null){
			classId = parent.getClassId();
			String key = getKey();
			boolean mapItem = false;
			if(classId== null && parent.getParent()!=null){
				classId = parent.getParent().getClassId();
				key = parent.getKey();
				mapItem = true;
			}
			if(classId!=null){
				PropertyMirror pm = InkUtils.getPropertyMirror(classId, key, new ArrayList<String>());
				if(pm!=null && pm.getTypeMarker()==DataTypeMarker.Collection){
					switch(((CollectionPropertyMirror)pm).getCollectionTypeMarker()){
					case List:
						getProposal(cursorLocation, result,((ListPropertyMirror)pm).getItemMirror(), prefix);
						break;
					case Map:
						PropertyMirror keyMirror = ((MapPropertyMirror)pm).getKeyMirror();
						PropertyMirror valueMirror = ((MapPropertyMirror)pm).getValueMirror();
						if(keyMirror !=null && valueMirror!=null){
							if(mapItem){
								String keyName = keyMirror.getName();
								String valueName = valueMirror.getName();
								if(!innerBlocks.containsKey(keyName)){
									getProposal(cursorLocation, result,((MapPropertyMirror)pm).getKeyMirror(), prefix);
								}
								if(!innerBlocks.containsKey(valueName)){
									getProposal(cursorLocation, result,((MapPropertyMirror)pm).getValueMirror(), prefix);
								}
							}else{
								Dictionary dic = ((MapPropertyMirror)pm).getSpecifictation();
								if(dic instanceof ElementsDictionary){
									getProposal(cursorLocation, result,((MapPropertyMirror)pm).getValueMirror(), prefix);
								}else{
									String name = dic.getEntryName();
									String displayString = name + " - " + "<" + getTypeDisplayString(keyMirror) +","+getTypeDisplayString(valueMirror) +">";
									String tabs = calculateTabs() + "\t";
									addPropertyNameCompletion(cursorLocation, tabs.length()+1,result,
											 displayString, name, "{\n" + tabs+"\n"+tabs.substring(0, tabs.length()-1)+"}", prefix);
								}
							}
						}
						break;
					}
				}
			}
		}
		return result;
	}

	private String getTypeDisplayString(PropertyMirror pm){
		String result = "";
		switch(pm.getTypeMarker()){
		case Collection:
			switch(((CollectionPropertyMirror)pm).getCollectionTypeMarker()){
			case List:
				result = "List<" + getTypeDisplayString(((ListPropertyMirror)pm).getItemMirror()) + ">";
				break;
			case Map:
				result = "Map<" + getTypeDisplayString(((MapPropertyMirror)pm).getKeyMirror()) +"," +getTypeDisplayString(((MapPropertyMirror)pm).getValueMirror())  + ">";
				break;
			}
		break;
		default:
			result = pm.getPropertyType().reflect().getShortId();
		break;
		}
		return result;
	}


	private void getProposal(int cursorLocation,
			List<ICompletionProposal> allProposals, PropertyMirror pm, String prefix) {
		String displayString;
		boolean isPrefix = isPrefix(pm.getName(), prefix);
		if(isPrefix){
			String name = pm.getName();
			switch(pm.getTypeMarker()){
			case Class:
				displayString = name +" - " + getTypeDisplayString(pm);
				addPropertyNameCompletion(cursorLocation, 0,allProposals,
						displayString, name," ", prefix);
				break;
			case Collection:
				displayString = name + " - " + getTypeDisplayString(pm);
				if(((CollectionPropertyMirror)pm).getCollectionTypeMarker()==CollectionTypeMarker.List &&
						((ListPropertyMirror)pm).getItemMirror().getTypeMarker()==DataTypeMarker.Primitive){
					addPropertyNameCompletion(cursorLocation, 1,allProposals,
							displayString, name," \"\"", prefix);
				}else{
					String tabs = calculateTabs() + "\t";
					addPropertyNameCompletion(cursorLocation, tabs.length()+1,allProposals,
							displayString, name,"{\n" + tabs+"\n"+tabs.substring(0, tabs.length()-1)+"}", prefix);
				}
				break;
			case Enum:
				displayString = name +" - " + getTypeDisplayString(pm);
				addPropertyNameCompletion(cursorLocation, 1,allProposals,
						displayString, name," \"\"", prefix);
				break;
			case Primitive:
				displayString = pm.getName() +" - " + getTypeDisplayString(pm);
				PrimitiveAttribute pa = pm.getTargetBehavior();
				if(pa.getType().isNumeric() || pa.getType().isBoolean()){
					addPropertyNameCompletion(cursorLocation, 0,allProposals,
							displayString, name, " ", prefix);
				}else if(pa.getType().isDate()){
					addPropertyNameCompletion(cursorLocation, 1,allProposals,
							displayString + " (yyyy/MM/dd)", name," \"\"", prefix);
				}else{
					addPropertyNameCompletion(cursorLocation, 1,allProposals,
							displayString, name," \"\"", prefix);
				}
				break;

			}
		}
	}

	private boolean addPropertyNameCompletion(int cursorLocation,int offset,
			List<ICompletionProposal> allProposals, String displayString,
			String name, String postFix, String prefix) {
		return allProposals.add(new CompletionProposal(name + postFix, cursorLocation - prefix.length(), prefix.length(), name.length() + postFix.length()-offset, null, displayString, null, null));
	}

}