package org.ink.core.vm.factory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.ink.core.vm.constraints.ValidationContext;
import org.ink.core.vm.constraints.ValidationMessage;
import org.ink.core.vm.exceptions.ObjectLoadingException;
import org.ink.core.vm.factory.internal.CoreNotations;
import org.ink.core.vm.lang.InkClass;
import org.ink.core.vm.lang.InkObjectImpl;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.serialization.InkReader;
import org.ink.core.vm.serialization.ParseError;
import org.ink.core.vm.traits.TraitClass;
import org.ink.core.vm.traits.TraitState;
import org.ink.core.vm.utils.InkNotations;
import org.ink.core.vm.utils.file.FileUtils;

/**
 * @author Lior Schachter
 */
public class DslLoaderImpl<S extends DslLoaderState, D> extends InkObjectImpl<S> implements DslLoader{

	private final Map<String, ElementDescriptor<D>> elements = new HashMap<String, ElementDescriptor<D>>(100);
	private DslFactory ownerFactory = null;
	private InkClass readerCls = null;
	ValidationContext vc = null;
	private File folder = null;

	@Override
	public void destroy() {
		elements.clear();
		readerCls = null;
		vc = null;
		folder = null;
	}

	@Override
	public synchronized InkObjectState getObject(String id, Context context) throws ObjectLoadingException{
		ElementDescriptor<D> desc = elements.get(id);
		if(desc!=null){
			try{
				InkReader<D> reader = createReader();
				InkObjectState result = reader.read(desc.getRawData(), context);
				if(reader.containsErrors()){
					List<ParseError> errors = reader.getErrors();
					desc.setParsingErrors(errors);
					System.out.println("============================================Load Error=====================================================================================");
					for(ParseError er : errors){
						System.out.println("Object '" +id+"':" + er.getDescription());
					}
					System.out.println("=================================================================================================================================");
					throw new ObjectLoadingException(result, null, errors, desc.getResource(), id);
				}else if( shouldValidateResult(result)&&!result.validate(vc)){
					List<ValidationMessage> errors = vc.getMessages();
					desc.setValidationErrorMessages(errors);
					System.out.println("============================================Load Error=====================================================================================");
					for(ValidationMessage er : errors){
						System.out.println("Object '" +id+"':" + er.getFormattedMessage());
					}
					System.out.println("=================================================================================================================================");
					throw new ObjectLoadingException(result, errors, null,desc.getResource(), id);
				}
				return result;
			}finally{
				vc.reset();
			}
		}
		return null;
	}

	private boolean shouldValidateResult(InkObjectState result){
		Mirror mirror = result.reflect();
		ClassMirror cMirror = mirror.getClassMirror();
		if(!cMirror.isCoreObject() && result.reflect().getClassMirror().getDescriptor().containsErrors()){
			return false;
		}
		Mirror superObject = mirror.getSuper();
		if(superObject!=null && !superObject.isCoreObject()){
			return !superObject.getDescriptor().containsErrors() ;
		}
		return true;
	}

	@Override
	public void scan(DslFactory ownerFactory) {
		try {
			loadElements(ownerFactory);
		} catch (IOException e) {
			//TODO - log error
		}
		List<String> startupObjects = locateStartupObjects(ownerFactory.getDslPackage());
		List<InkObjectState> objects = new ArrayList<InkObjectState>();
		ClassMirror tc = getContext().getObject(CoreNotations.Ids.TRAIT).reflect();
		InkObjectState o = null;
		for(String id : startupObjects){
			try {
				o  = getObject(id, ownerFactory.getAppContext());
				objects.add(o);
			} catch (ObjectLoadingException e) {
				e.printStackTrace();
			}
		}
		InkClass cls;
		for(ListIterator<InkObjectState> iter=objects.listIterator();iter.hasNext();){
			o = iter.next();
			cls = o.getMeta();
			if(((ClassMirror)cls.reflect()).isSubClassOf(tc)
					&& ((TraitClass)cls).getKind().isDetachable()){
				iter.remove();
				ownerFactory.registerTrait((TraitState)o);
			}
		}
		for(ListIterator<InkObjectState> iter=objects.listIterator();iter.hasNext();){
			o = iter.next();
			ownerFactory.register(o);
		}
	}

	private InkReader<D> createReader(){
		return readerCls.newInstance(ownerFactory.getAppContext()).getBehavior();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private synchronized void loadElements(DslFactory ownerFactory) throws IOException {
		this.ownerFactory = ownerFactory;
		folder = VMConfig.instance().getInstantiationStrategy().getDslResourcesLocation(ownerFactory);
		//TODO - the reader should be a property on DSLoader
		readerCls = ownerFactory.getObject(CoreNotations.Ids.INK_READER.toString());
		InkReader<D> reader = createReader();
		vc = ((InkClass)ownerFactory.getObject(CoreNotations.Ids.VALIDATION_CONTEXT.toString())).newInstance().getBehavior();
		List<ElementDescriptor<D>> fileElements;
		String id;
		for(File f : getInkFiles()){
			fileElements = reader.extractRawData(f, ownerFactory.getAppContext());
			if(!reader.containsErrors()){
				for(ElementDescriptor desc : fileElements){
					id = desc.getId();
					if(id!=null){
						if(id.indexOf(InkNotations.Path_Syntax.NAMESPACE_DELIMITER_C) <0){
							id = ownerFactory.getNamespace() +InkNotations.Path_Syntax.NAMESPACE_DELIMITER_C+id;
						}
						elements.put(id, desc);
					}else{
						//TODO - log error
					}
				}
			}else{
				for(ParseError pe : reader.getErrors()){
					//TODO - should refactor this
					System.out.println("Error while parsing ink file '" + f.getAbsolutePath() +"', line number " + pe.getLineNumber() +";" + pe.getDescription());
				}
			}
		}
	}

	private String findSuperId(String id){
		String result = null;
		ElementDescriptor<D> superElem = elements.get(id);
		if(superElem!=null){
			result = superElem.getSuperId();
		}else{
			InkObjectState ios = ownerFactory.getState(id, false);
			if(ios!=null){
				Mirror superObject = ios.reflect().getSuper();
				if(superObject!=null){
					result = superObject.getId();
				}
			}else{
				System.out.println("Could not locate object with id " + id);
			}
		}
		return result;
	}


	private List<String> locateStartupObjects(String dslPackage) {
		List<String> result = new ArrayList<String>();
		for(ElementDescriptor<D> elem : elements.values()){
			String superId = findSuperId(elem.getClassId());
			while(superId!=null){
				if(superId.equals(CoreNotations.Ids.TRAIT)){
					result.add(elem.getId());
					break;
				}
				superId = findSuperId(superId);
			}
		}
		return result;
	}

	@Override
	public Iterator<String> iterator() {
		return elements.keySet().iterator();
	}

	@Override
	public List<File> getInkFiles() {
		return FileUtils.listInkFiles(folder);
	}

	@Override
	public List<InkErrorDetails> collectErrors() {
		List<InkErrorDetails> result = new ArrayList<InkErrorDetails>();
		ElementDescriptor<D> elem;
		InkErrorDetails err;
		for(Map.Entry<String, ElementDescriptor<D>> en : elements.entrySet()){
			elem = en.getValue();
			if(elem.containsErrors()){
				if(elem.getParsingErrors()!=null){
					for(ParseError pe : elem.getParsingErrors()){
						err = new InkErrorDetails(en.getKey(), pe.getLineNumber(), pe.getDescription(), elem.getResource());
						result.add(err);
					}
				}
				if(elem.getValidationErrorMessages()!=null){
					for(ValidationMessage vm : elem.getValidationErrorMessages()){
						err = new InkErrorDetails(en.getKey(), vm.getErrorPath(), vm.getFormattedMessage(), elem.getResource());
						result.add(err);
					}
				}
				elem.clearErrors();
			}
		}
		return result;
	}

	@Override
	public ElementDescriptor<?> getDescriptor(String id) {
		return elements.get(id);
	}

}
