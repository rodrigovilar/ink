package org.ink.eclipse.editors.page;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.ink.core.vm.lang.DataTypeMarker;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.types.EnumType;
import org.ink.core.vm.utils.property.mirror.PrimitiveAttributeMirror;
import org.ink.core.vm.utils.property.mirror.ReferenceMirror;
import org.ink.eclipse.utils.InkUtils;


public class SimpleDataBlock extends DataBlock {

	public SimpleDataBlock(String namespace, ObjectDataBlock parent, char[] text, int startIndex, int endIndex) {
		super(namespace, parent, text, startIndex, endIndex);
	}

	@Override
	public DataBlock getBlock(int cursorLocation){
		if(cursorLocation>=startIndex && cursorLocation<=endIndex){
			return this;
		}
		return null;
	}

	private void addValueProposal(List<ICompletionProposal> all, String value, int cursorLocation, String prefix) {
		boolean isPrefix = isPrefix(value, prefix);
		if(isPrefix){
			all.add(new CompletionProposal(value, cursorLocation-prefix.length(), prefix.length(), value.length(), null, value, null, null));
		}
	}

	@Override
	protected List<ICompletionProposal> getInlineProposals(int cursorLocation, String prefix) {
		List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();
		PropertyMirror pm = InkUtils.getPropertyMirror(getContainingClass(), getKey(), getPathToClassBlock());
		String line = new String(text, startIndex, endIndex-startIndex);
		if(pm!=null){
			switch(pm.getTypeMarker()){
			case Primitive:
				switch(((PrimitiveAttributeMirror)pm).getPrimitiveTypeMarker()){
				case Boolean:
					addValueProposal(result, "true", cursorLocation, prefix);
					addValueProposal(result, "false", cursorLocation, prefix);
					break;
				}
				break;
			case Enum:
				EnumType enumT = (EnumType) pm.getPropertyType();
				for(String val : enumT.getValues()){
					addValueProposal(result, val, cursorLocation, prefix);
				}
				break;
			case Class:
				if(prefix.length()>0){
					StringBuilder attrB = new StringBuilder(10);
					for(int i=cursorLocation-(3+prefix.length());i>=0;i--){
						if(text[i]==' '){
							break;
						}
						attrB.append(text[i]);
					}
					String attr = attrB.reverse().toString();
					if(attr.equals("ref")){
						addRefPropsals(cursorLocation, prefix, result);
					}else if(attr.equals("class")){
						pm = InkUtils.getPropertyMirror(getContainingClass(), getKey(), getPathToClassBlock());
						if(pm.getTypeMarker()==DataTypeMarker.Class){
							Mirror m = ((ReferenceMirror)pm).getPropertyType().reflect();
							String constraintClass = m.getId();
							List<String> options = InkUtils.getSubClasses(ns, constraintClass, true, false);
							if(!m.isAbstract()){
								options.add(constraintClass);
							}
							for(String id : options){
								addIdProposal(result, cursorLocation, id, prefix);
							}
						}
					}else if(attr.equals("super")){
						for(String id : getSuperProposals(line, null)){
							addIdProposal(result, cursorLocation, id, prefix);
						}
					}else{
						if(line.contains("class") && !line.contains("{")){
							String tabs = calculateTabs();
							result.add(new CompletionProposal("{\n" + tabs+"\n"+tabs.substring(0, tabs.length()-1)+"}", cursorLocation, 0, new String("{\n" + tabs+"\n}").length()-2, null, "{", null, null));
						}
					}
				}else{
					result = super.getInlineProposals(cursorLocation, prefix);
				}

				break;
			}
		}
		return result;
	}


	@Override
	protected List<ICompletionProposal> getNewLineProposals(int cursorLocation, String prefix) {
		return parent.getNewLineProposals(cursorLocation, prefix);
	}

}
