package ink.eclipse.editors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.ink.core.vm.factory.internal.CoreNotations;
import org.ink.core.vm.lang.DataTypeMarker;
import org.ink.core.vm.lang.InkClass;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.modelinfo.ModelInfoFactory;
import org.ink.core.vm.modelinfo.ModelInfoRepository;
import org.ink.core.vm.modelinfo.relations.ExtendsRelation;
import org.ink.core.vm.utils.property.PrimitiveAttribute;
import org.ink.core.vm.utils.property.mirror.ReferenceMirror;
import org.ink.eclipse.InkPlugin;

public class InkContentAssistProcessorOld implements IContentAssistProcessor {

	protected IProgressMonitor createProgressMonitor() {
		return new NullProgressMonitor();
	}

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset) {
		// TODO Auto-generated method stub
		String doc = viewer.getDocument().get();
		String part1 = doc.substring(0, offset);
		String part2;
		if(offset<doc.length()-1){
			part2 = doc.substring(offset+1, doc.length());
		}else{
			part2 = "";
		}
		int startB=0;
		int endB = 0;
		for(char c : part1.toCharArray()){
			switch(c){
			case '{':
				startB++;
				break;
			case '}':
				endB++;
				break;
			}
		}
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		boolean toContinue = true;
		String line = part1.substring(part1.lastIndexOf('\n'));
		if(startB==endB){
			boolean startElement = false;
			int count = 0;
			for(int i=offset-1;i>0&&toContinue;i--){
				char c = part1.charAt(i);
				switch(c){
				case '\n':
					startElement = true;
					toContinue = false;
					break;
				case ' ':
					toContinue = false;
					break;
				default:
					count++;
					break;
				}

			}
			if(startElement){
				proposals.add(new CompletionProposal("Class ", offset-count, count, "Class ".length(), null, null, null, null));
				proposals.add(new CompletionProposal("Object ", offset-count, count,"Object ".length(), null, null, null, null));
			}else if(part1.charAt(offset-1)==' '){
				if(!line.contains("id")){
					proposals.add(new CompletionProposal("id=\"\"", offset-count, count, "id=\"\"".length()-1, null, "id", null, null));
				}
				if(!line.contains("class")){
					proposals.add(new CompletionProposal("class=\"\"", offset-count, count,"class=\"\"".length()-1, null, "class", null, null));
				}
				if(proposals.isEmpty()){
					proposals.add(new CompletionProposal("{\n\t\n}", offset, 0, "{\n\t\n}".length()-2, null, "{", null, null));
				}
				if(!line.contains("super")){
					proposals.add(new CompletionProposal("super=\"\"", offset-count, count, "super=\"\"".length()-1, null, "super", null, null));
				}
			}else if(part1.charAt(offset-1)=='\"'){
				String attr = part1.substring(part1.lastIndexOf(' ')+1, offset-2);
				if(attr.equals("class")){
					ModelInfoRepository repo = ModelInfoFactory.getInstance();
					Collection<InkObject> referrers;
					if(line.startsWith("Object")){
						InkObject inkObject = InkPlugin.getDefault().getInkContext().getObject(CoreNotations.Ids.INK_OBJECT);
						referrers = repo.findReferrers(inkObject, ExtendsRelation.getInstance(), false);
					}else{
						InkObject inkObject = InkPlugin.getDefault().getInkContext().getObject(CoreNotations.Ids.INK_OBJECT);
						referrers = repo.findReferrers(inkObject, ExtendsRelation.getInstance(), false);
					}
					for(InkObject o : referrers){
						String id = o.reflect().getId();
						proposals.add(new CompletionProposal(id, offset, 0, id.length()+2, null, id, null, null));
					}
				}
			}
			if(proposals.isEmpty()){
				proposals.add(new CompletionProposal("{\n\t\n}", offset, 0, "{\n\t\n}".length()-2, null, "{", null, null));
			}
		}else if(startB > endB){
			int startIndex = part1.lastIndexOf("Object");
			int classindex = part1.lastIndexOf("Class");
			if(startIndex < classindex){
				startIndex = classindex;
			}
			String currentObject = part1.substring(startIndex, part1.length());
			boolean isFieldName = true;
			for(char c : line.toCharArray()){
				if(!Character.isWhitespace(c)){
					isFieldName = false;
					break;
				}
			}
			try{
				if(isFieldName){
					int classIdStartIndex = part1.lastIndexOf("class=\"") + "class=\"".length();
					int classIdEndIndex = part1.indexOf("\"", classIdStartIndex);
					if(classIdStartIndex > -1 && classIdEndIndex>-1){
						String classId = part1.substring(classIdStartIndex, classIdEndIndex);
						if(classId!=null){
							InkClass inkCls = InkPlugin.getDefault().getInkContext().getObject(classId);
							ClassMirror mirror = inkCls.reflect();
							PropertyMirror[] pMirrors = mirror.getClassPropertiesMirrors();
							for(PropertyMirror pm : pMirrors){
								switch(pm.getTypeMarker()){
								case Class:
									proposals.add(new CompletionProposal(pm.getName() + " class=\"\"{\n\t\n\t}", offset, 0, pm.getName().length() + " class=\"\"{\n\t\n\t}".length()-7, null, pm.getName(), null, null));
									break;
								case Collection:
									proposals.add(new CompletionProposal(pm.getName() + "{\n\t\t\n\t}", offset, 0, pm.getName().length() +"{\n\t\t\n\t}".length() -3, null, pm.getName(), null, null));
									break;
								case Enum:
									proposals.add(new CompletionProposal(pm.getName() + " \"\"", offset, 0, pm.getName().length() + " \"\"".length()-1, null, pm.getName(), null, null));
									break;
								case Primitive:
									PrimitiveAttribute pa = pm.getTargetBehavior();
									if(pa.getType().isNumeric()){
										proposals.add(new CompletionProposal(pm.getName() + " ", offset, 0, pm.getName().length() + 1, null, pm.getName(), null, null));
									}else{
										proposals.add(new CompletionProposal(pm.getName() + " \"\"", offset, 0, pm.getName().length() + " \"\"".length()-1, null, pm.getName(), null, null));
									}
									break;

								}
							}
						}
					}
				}else{
					if(part1.charAt(offset-1)=='\"'){
						int lastSpaceIndex = line.lastIndexOf(' ');
						String attr = line.substring(lastSpaceIndex+1, line.length()-2);
						if(attr.equals("class")){
							String propertyName = line.substring(0, lastSpaceIndex).trim();
							String part1a = currentObject.substring(0, currentObject.lastIndexOf(propertyName)-1);
							int classIdStartIndex = part1a.lastIndexOf("class=\"") + "class=\"".length();
							int classIdEndIndex = part1a.indexOf("\"", classIdStartIndex);
							if(classIdStartIndex > -1 && classIdEndIndex>-1){
								String classId = part1a.substring(classIdStartIndex, classIdEndIndex);
								if(classId!=null){
									InkClass inkCls = InkPlugin.getDefault().getInkContext().getObject(classId);
									ClassMirror mirror = inkCls.reflect();
									PropertyMirror pm = mirror.getClassPropertyMirror(propertyName);
									if(pm.getTypeMarker()==DataTypeMarker.Class){
										InkClass propertyType = (InkClass) ((ReferenceMirror)pm).getPropertyType();
										ModelInfoRepository repo = ModelInfoFactory.getInstance();
										Collection<InkObject> referrers;
										referrers = repo.findReferrers(propertyType, ExtendsRelation.getInstance(), false);
										String id = propertyType.reflect().getId();
										proposals.add(new CompletionProposal(id, offset, 0, id.length()+2, null, id, null, null));
										if(referrers!=null){
											for(InkObject o : referrers){
												id = o.reflect().getId();
												proposals.add(new CompletionProposal(id, offset, 0, id.length()+2, null, id, null, null));
											}
										}
									}
								}
							}
						}
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return proposals.toArray(new ICompletionProposal[]{});
	}

	@Override
	public IContextInformation[] computeContextInformation(ITextViewer viewer,
			int offset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IContextInformationValidator getContextInformationValidator() {
		// TODO Auto-generated method stub
		return null;
	}

}
