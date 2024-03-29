package org.ink.eclipse.editors.page;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.ink.core.vm.factory.InkVM;
import org.ink.core.vm.lang.DataTypeMarker;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.exceptions.InvalidPathException;
import org.ink.core.vm.lang.property.mirror.CollectionPropertyMirror;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.proxy.Proxiable;
import org.ink.core.vm.types.EnumType;
import org.ink.core.vm.utils.property.ModelPathAttribute;
import org.ink.core.vm.utils.property.mirror.ListPropertyMirror;
import org.ink.core.vm.utils.property.mirror.PrimitiveAttributeMirror;
import org.ink.core.vm.utils.property.mirror.ReferenceMirror;
import org.ink.eclipse.utils.InkUtils;

public class SimpleDataBlock extends DataBlock {

	public SimpleDataBlock(String namespace, ObjectDataBlock parent, char[] text, int startIndex, int endIndex) {
		super(namespace, parent, text, startIndex, endIndex);
	}

	@Override
	public DataBlock getBlock(int cursorLocation) {
		if (cursorLocation >= startIndex && cursorLocation <= endIndex) {
			return this;
		}
		return null;
	}

	@Override
	protected List<ICompletionProposal> getInlineProposals(int cursorLocation, String prefix) {
		List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();
		List<String> pathsToClassBlock = getPathToClassBlock();
		PropertyMirror pm = InkUtils.getPropertyMirror(getContainingClass(), getKey(), pathsToClassBlock);
		if (pm == null && pathsToClassBlock != null && pathsToClassBlock.size() > 0) {
			List<String> paths = new ArrayList<String>(pathsToClassBlock);
			pm = InkUtils.getPropertyMirror(getContainingClass(), paths.remove(pathsToClassBlock.size() - 1), paths);
		}
		String line = new String(text, startIndex, endIndex - startIndex);
		if (pm != null) {
			switch (pm.getTypeMarker()) {
			case PRIMITIVE:
				switch (((PrimitiveAttributeMirror) pm).getPrimitiveTypeMarker()) {
				case BOOLEAN:
					addValueProposal(result, "true", "true", cursorLocation, prefix);
					addValueProposal(result, "false", "false", cursorLocation, prefix);
					break;
				case STRING:
					if(pm.getTargetBehavior() instanceof ModelPathAttribute){
						DataBlock root = this;
						while(root.getParent()!=null){
							root = root.getParent();
						}
						String id = root.getAttributeValue("id");
						if(id!=null){
							InkObject o = InkVM.instance().getContext().getFactory().getObject(id, false);
							if(o!=null){
								int startPath = line.indexOf('\"') + 1;
								if(startPath>0){
									String existingPath = line.substring(startPath, cursorLocation - startIndex);
									try{
										try{
											getPathOptions(cursorLocation, result, o, existingPath);
										}catch(InvalidPathException e){}
									}catch(InvalidPathException e){
										e.printStackTrace();
									}
								}
							}
						}
						
					}
					break;
				}
				break;
			case ENUM:
				if (checkCursorLocation(cursorLocation, prefix)) {
					EnumType enumT = (EnumType) pm.getPropertyType();
					for (String val : enumT.getValues()) {
						addValueProposal(result, val, val, cursorLocation, prefix);
					}
				}
				break;
			case COLLECTION:
				switch (((CollectionPropertyMirror) pm).getCollectionTypeMarker()) {
				case LIST:
					PropertyMirror itempMirror = ((ListPropertyMirror) pm).getItemMirror();
					if (itempMirror.getPropertyType().isEnumeration()) {
						EnumType enumT = (EnumType) itempMirror.getPropertyType();
						for (String val : enumT.getValues()) {
							addValueProposal(result, "\"" + val + "\"", val, cursorLocation, prefix);
						}
					} else if (itempMirror.getPropertyType().isPrimitive()) {
						PrimitiveAttributeMirror primitivePM = (PrimitiveAttributeMirror) itempMirror;
						switch (primitivePM.getPrimitiveTypeMarker()) {
						case STRING:
							String space = " ";
							if (text[cursorLocation - 1] == ' ') {
								space = "";
							}
							addPropertyNameCompletion(cursorLocation, 1, result, "Insert " + getTypeDisplayString(itempMirror) + " values separated by space.", "", space + "\"\"", prefix);
							break;
						case DATE:
							addPropertyNameCompletion(cursorLocation, 1, result, "Insert " + getTypeDisplayString(itempMirror) + " (yyyy/MM/dd) values separated by space.", "", " \"\"", prefix);
							break;
						default:
							result.add(new CompletionProposal("", cursorLocation, prefix.length(), 0, null, "Insert " + getTypeDisplayString(itempMirror) + " values separated by space.", null, null));
						}
					}
					break;
					/*
					 * case Map:
					 * PropertyMirror keyMirror = ((MapPropertyMirror)pm).getKeyMirror();
					 * PropertyMirror valueMirror = ((MapPropertyMirror)pm).getValueMirror();
					 * if(keyMirror !=null && valueMirror!=null){
					 * if(mapItem){
					 * String keyName = keyMirror.getName();
					 * String valueName = valueMirror.getName();
					 * if(!innerBlocks.containsKey(keyName)){
					 * getProposal(cursorLocation, result,((MapPropertyMirror)pm).getKeyMirror(), prefix);
					 * }
					 * if(!innerBlocks.containsKey(valueName)){
					 * getProposal(cursorLocation, result,((MapPropertyMirror)pm).getValueMirror(), prefix);
					 * }
					 * }else{
					 * Dictionary dic = ((MapPropertyMirror)pm).getSpecifictation();
					 * if(dic instanceof ElementsDictionary){
					 * getProposal(cursorLocation, result,((MapPropertyMirror)pm).getValueMirror(), prefix);
					 * }else{
					 * String name = dic.getEntryName();
					 * String displayString = name + " - " + "<" + getTypeDisplayString(keyMirror) +","+getTypeDisplayString(valueMirror) +">";
					 * String tabs = calculateTabs() + "\t";
					 * addPropertyNameCompletion(cursorLocation, tabs.length()+1,result,
					 * displayString, name, "{\n" + tabs+"\n"+tabs.substring(0, tabs.length()-1)+"}", prefix);
					 * }
					 * }
					 * }
					 * break;
					 */
				}

				break;
			case CLASS:
				if (prefix.length() > 0) {
					StringBuilder attrB = new StringBuilder(10);
					for (int i = cursorLocation - (3 + prefix.length()); i >= 0; i--) {
						if (text[i] == ' ') {
							break;
						}
						attrB.append(text[i]);
					}
					String attr = attrB.reverse().toString();
					if (attr.equals("ref")) {
						addRefPropsals(cursorLocation, prefix, result);
					} else if (attr.equals("class")) {
						pm = InkUtils.getPropertyMirror(getContainingClass(), getKey(), getPathToClassBlock());
						if (pm.getTypeMarker() == DataTypeMarker.CLASS) {
							Mirror m = ((ReferenceMirror) pm).getPropertyType().reflect();
							String constraintClass = m.getId();
							List<String> options = InkUtils.getSubClasses(ns, constraintClass, true, false);
							if (!m.isAbstract()) {
								options.add(constraintClass);
							}
							for (String id : options) {
								addIdProposal(result, cursorLocation, id, prefix);
							}
						}
					} else if (attr.equals("super")) {
						for (String id : getSuperProposals(line, null)) {
							addIdProposal(result, cursorLocation, id, prefix);
						}
					} else {
						if (attributes.containsKey("class") && !line.contains("{")) {
							String tabs = calculateTabs();
							result.add(new CompletionProposal("{\n" + tabs + "\n" + tabs.substring(0, tabs.length() - 1) + "}", cursorLocation, 0, new String("{\n" + tabs + "\n}").length() - 2, null, "{", null, null));
						}
					}
				} else {
					result = super.getInlineProposals(cursorLocation, prefix);
				}

				break;
			}
		}
		return result;
	}

	private void getPathOptions(int cursorLocation, List<ICompletionProposal> result, InkObject o, String existingPath) {
		Object innerO = null;
		try{
			innerO = o.reflect().getValueByPath(existingPath);
		}catch(InvalidPathException e){
			int loc = existingPath.lastIndexOf(".");
			if(loc>0){
				//String start = existingPath.substring(0, loc-1);
				//String end = existingPath.
			}else{
				
			}
		}
		if(innerO instanceof Proxiable){
			Mirror m = ((Proxiable)innerO).reflect();
			for(PropertyMirror p : m.getPropertiesMirrors()){
				if(m.getPropertyValue(p.getIndex())!=null){
					if(p.isMutable()){
						if(existingPath.length()>0){
							if(existingPath.endsWith(".")){
								addValueProposal(result, p.getName(), p.getName(), cursorLocation, "");
							}else{
								addValueProposal(result, "." + p.getName(), p.getName(), cursorLocation, "");
							}
						}else{
							addValueProposal(result, p.getName(), p.getName(), cursorLocation, "");
						}
						
					}
				}
			} 
		}else if(innerO instanceof Map){
			Map<?,?> m = (Map<?,?>)innerO;
			for(Object k : m.keySet()){
				addValueProposal(result, "<"+k.toString() +">", k.toString(), cursorLocation, "");
			}
		}else if(innerO instanceof List){
			List<?> l = (List<?>)innerO;
			for(int i=0;i<l.size();i++){
				addValueProposal(result, "["+i +"]", i +"", cursorLocation, "");
			}
		}
	}

	private boolean checkCursorLocation(int cursorLocation, String prefix) {
		int count = 0;
		String line = new String(text).substring(startIndex, cursorLocation).trim();
		for (char c : line.toCharArray()) {
			if (c == '\"') {
				count++;
			}
		}
		return count % 2 != 0;
	}

	@Override
	protected List<ICompletionProposal> getNewLineProposals(int lineNumber, int cursorLocation, String prefix) {
		return parent.getNewLineProposals(lineNumber, cursorLocation, prefix);
	}

}
