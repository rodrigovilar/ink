package org.ink.core.vm.factory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.ink.core.vm.constraints.ValidationContext;
import org.ink.core.vm.constraints.ValidationMessage;
import org.ink.core.vm.exceptions.ObjectLoadingException;
import org.ink.core.vm.factory.internal.CoreLoaderState;
import org.ink.core.vm.factory.internal.CoreNotations;
import org.ink.core.vm.lang.InkClass;
import org.ink.core.vm.lang.InkObjectImpl;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.mirror.ClassMirror;
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

	private Map<String, ElementDescriptor<D>> elements = new HashMap<String, ElementDescriptor<D>>(100);
	InkReader<D> reader = null;
	ValidationContext vc = null;
	
	@Override
	public synchronized InkObjectState getObject(String id, Context context) throws ObjectLoadingException{
		ElementDescriptor<D> desc = elements.get(id);
		if(desc!=null){
			try{
				InkObjectState result = reader.read(desc.getRawData(), context);
				if(reader.containsErrors()){
					List<ParseError> errors = reader.getErrors();
					desc.setParsingErrors(errors);
					System.out.println("============================================Load Error=====================================================================================");
					for(ParseError er : errors){
						System.out.println("Object '" +id+"':" + er.getDescription());
					}
					System.out.println("=================================================================================================================================");
					throw new ObjectLoadingException(null, errors, desc.getResource(), id);
				}else if(!result.validate(vc)){
					List<ValidationMessage> errors = vc.getMessages();
					desc.setValidationErrorMessages(errors);
					System.out.println("============================================Load Error=====================================================================================");
					for(ValidationMessage er : errors){
						System.out.println("Object '" +id+"':" + er.getFormattedMessage());
					}
					System.out.println("=================================================================================================================================");
					throw new ObjectLoadingException(errors, null, desc.getResource(), id);
				}
				return result;
			}finally{
				reader.reset();
				vc.reset();
			}
		}
		return null;
	}

	@Override
	public void scan(DslFactory ownerFactory) {
		try {
			loadElements(ownerFactory);
		} catch (IOException e) {
			//TODO - log error
		}
		List<String> startupObjects = scan(ownerFactory.getDslPackage());
		List<InkObjectState> objects = new ArrayList<InkObjectState>();
		ClassMirror tc = getContext().getObject(CoreNotations.Ids.TRAIT).reflect();
		InkObjectState o = null;
		for(String id : startupObjects){
			o  = getObject(id, ownerFactory.getAppContext());
			objects.add(o);
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private synchronized void loadElements(DslFactory ownerFactory) throws IOException {
		File folder = VMConfig.instance().getInstantiationStrategy().getDslResourcesLocation(ownerFactory);
		//TODO - the reader should be a property on DSLoader
		InkClass readerCls = ownerFactory.getObject(CoreNotations.Ids.INK_READER.toString());
		reader = readerCls.newInstance(ownerFactory.getAppContext()).getBehavior();
		vc = ((InkClass)ownerFactory.getObject(CoreNotations.Ids.VALIDATION_CONTEXT.toString())).newInstance().getBehavior();
		List<ElementDescriptor<D>> fileElements;
		String id;
		for(File f : FileUtils.listInkFiles(folder)){
			fileElements = reader.extractRawData(f);
			if(!reader.containsErrors()){
				for(ElementDescriptor desc : fileElements){
					id = desc.getId();
					if(id!=null){
						elements.put(ownerFactory.getNamespace() +InkNotations.Path_Syntax.NAMESPACE_DELIMITER_C+id, desc);
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


	private List<String> scan(String dslPackage) {
		return new ArrayList<String>();
	}

	@Override
	public Iterator<String> iterator() {
		return elements.keySet().iterator();
	}
	
}
