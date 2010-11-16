package ink.eclipse.editors.page;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.ink.core.vm.lang.DataTypeMarker;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.types.EnumType;
import org.ink.core.vm.utils.property.mirror.PrimitiveAttributeMirror;
import org.ink.core.vm.utils.property.mirror.ReferenceMirror;


public class SimpleDataBlock extends DataBlock {

	public SimpleDataBlock(ObjectDataBlock parent, char[] text, int startIndex, int endIndex) {
		super(parent, text, startIndex, endIndex);
	}

	@Override
	public DataBlock getBlock(int cursorLocation){
		if(cursorLocation>=startIndex && cursorLocation<=endIndex){
			return this;
		}
		return null;
	}
	
	private CompletionProposal createValueProposal(String value, int cursorLocation) {
		return new CompletionProposal(value, cursorLocation, 0, value.length(), null, value, null, null);
	}

	@Override
	protected List<ICompletionProposal> getInlineProposals(int cursorLocation) {
		List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();
		PropertyMirror pm = InkContentAssistUtil.getPropertyMirror(getContainingClass(), getKey(), getPathToClassBlock());
		if(pm!=null){
			switch(pm.getTypeMarker()){
			case Primitive:
				switch(((PrimitiveAttributeMirror)pm).getPrimitiveTypeMarker()){
				case Boolean:
					result.add(createValueProposal("true", cursorLocation));
					result.add(createValueProposal("false", cursorLocation));
					break;
				}
				break;
			case Enum:
				EnumType enumT = (EnumType) pm.getPropertyType();
				for(String val : enumT.getValues()){
					result.add(createValueProposal(val, cursorLocation));
				}
				break;
			case Class:
				if(text[cursorLocation-1]=='\"'){
					StringBuilder attrB = new StringBuilder(10);
					for(int i=cursorLocation-3;i>=0;i--){
						if(text[i]==' '){
							break;
						}
						attrB.append(text[i]);
					}
					String attr = attrB.reverse().toString();
					if(attr.equals("ref")){
						pm = InkContentAssistUtil.getPropertyMirror(getContainingClass(), getKey(), getPathToClassBlock());
						if(pm.getTypeMarker()==DataTypeMarker.Class){
							String constraintClass = ((ReferenceMirror)pm).getPropertyType().reflect().getId();
							List<String> options = InkContentAssistUtil.getInstances(constraintClass);
							for(String id : options){
								result.add(new CompletionProposal(id, cursorLocation, 0, id.length()+1, null, id, null, null));
							}
						}
					}else if(attr.equals("class")){
						pm = InkContentAssistUtil.getPropertyMirror(getContainingClass(), getKey(), getPathToClassBlock());
						if(pm.getTypeMarker()==DataTypeMarker.Class){
							String constraintClass = ((ReferenceMirror)pm).getPropertyType().reflect().getId();
							List<String> options = InkContentAssistUtil.getSubClasses(constraintClass);
							options.add(constraintClass);
							for(String id : options){
								result.add(new CompletionProposal(id, cursorLocation, 0, id.length()+1, null, id, null, null));
							}
						}
					}else{
						String line = new String(text, startIndex, endIndex-startIndex);
						if(line.contains("class") && !line.contains("{")){
							result.add(new CompletionProposal("{\n\t\n}", cursorLocation, 0, "{\n\t\n}".length()-2, null, "{", null, null));
						}
					}
				}else{
					result = super.getInlineProposals(cursorLocation);
				}
				
				break;
			}
		}
		return result;
	}

	@Override
	protected List<ICompletionProposal> getNewLineProposals(int cursorLocation) {
		return parent.getNewLineProposals(cursorLocation);
	}

}
