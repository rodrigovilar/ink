package org.ink.core.vm.factory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.ink.core.vm.constraints.ResourceType;
import org.ink.core.vm.constraints.Severity;
import org.ink.core.vm.constraints.ValidationContext;
import org.ink.core.vm.constraints.ValidationMessage;
import org.ink.core.vm.exceptions.ObjectLoadingException;
import org.ink.core.vm.factory.internal.CoreNotations;
import org.ink.core.vm.lang.InkClass;
import org.ink.core.vm.lang.InkObjectImpl;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.LifeCycleState;
import org.ink.core.vm.lang.internal.MirrorAPI;
import org.ink.core.vm.messages.Message;
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
public class DslLoaderImpl<S extends DslLoaderState, D> extends InkObjectImpl<S> implements DslLoader {

	private final Map<String, ElementDescriptor<D>> elements = new HashMap<String, ElementDescriptor<D>>(100);
	private static final Map<String, List<String>> file2Elements = new ConcurrentHashMap<String, List<String>>(1000);
	private DslFactory ownerFactory = null;
	private InkClass readerCls = null;
	private final Map<String, InkObjectState> serializationContext = new HashMap<String, InkObjectState>();
	private int counter = 0;
	ValidationContext vc = null;
	private File[] folders = null;

	@Override
	public void init() {
		elements.clear();
		serializationContext.clear();
		file2Elements.clear();
		readerCls = null;
		vc = null;
		folders = null;
	}

	@Override
	public synchronized InkObjectState getObject(String id, Context applicationContext) throws ObjectLoadingException {
		InkObjectState result = serializationContext.get(id);
		if(result!=null){
			return result;
		}
		ElementDescriptor<D> desc = elements.get(id);
		if (desc != null) {
			try {
				Mirror clsMirror = null;
				if (desc.getClassId() != null) {
					InkObjectState clsState = applicationContext.getState(desc.getClassId(), false);
					if (clsState != null) {
						clsMirror = clsState.reflect();
					}
				}
				Mirror superMirror = null;
				if(desc.getSuperId()!=null){
					InkObjectState superState = applicationContext.getState(desc.getSuperId(), false);
					if(superState!=null){
						superMirror = superState.reflect();
					}
				}
				
				if ((clsMirror == null || clsMirror.isValid()) && (superMirror==null || superMirror.isValid())) {
					InkReader<D> reader = createReader();
					if(serializationContext.isEmpty()){
						counter = 1;
					}else{
						counter++;
					}
					result = reader.read(desc.getRawData(), applicationContext, serializationContext);
					if(!reader.containsErrors()){
						if(result.reflect().isClass()){
							compile(result, desc, reader);
						}
						counter--;
						if(counter==0){
							for(InkObjectState s : serializationContext.values()){
								compile(s, (ElementDescriptor<D>) s.reflect().getDescriptor(), reader);
							}
							serializationContext.clear();
							
						}
					}else{
						List<ParseError> errors = reader.getErrors();
						if(result!=null){
							result.reflect().setLifeCycleState(LifeCycleState.INVALID);
						}
						desc.setInvalid();
						desc.setParsingErrors(errors);
						if (!errors.isEmpty()) {
							System.out.println("============================================Load Error=====================================================================================");
							for (ParseError er : errors) {
								System.out.println("Object '" + id + "':" + er.getDescription());
							}
							System.out.println("=================================================================================================================================");
						}
						serializationContext.clear();
						throw new ObjectLoadingException(result, null, errors, desc.getResource(), id);
					}
					
					return result;
				} else {
					desc.setInvalid();
				}
			} finally {
				vc.reset();
			}
		}
		return null;
	}

	private void compile(InkObjectState state, ElementDescriptor<D> desc, InkReader<D> reader) throws ObjectLoadingException {
		Mirror m = state.reflect();
		String id = m.getId();
		try{
			if(m.getLifeCycleState()!=LifeCycleState.READY){
				String superId = ((MirrorAPI)state).getSuperId();
				InkObjectState superObject = null;
				if(superId!=null && (superObject=serializationContext.get(superId))!=null){
					Mirror superMirror = superObject.reflect();
					if(superMirror.getLifeCycleState()!=LifeCycleState.READY){
						ElementDescriptor<D> superDesc = elements.get(superId);
						compile(superObject, superDesc, reader);
						if(!m.isValid()){
							desc.setInvalid();
							m.setLifeCycleState(LifeCycleState.INVALID);
						}
					}
					
				}
				m.edit().compile();
			}
			if (desc.isValid() && shouldValidateResult(state) && !state.validate(vc)) {
				List<ValidationMessage> errors = vc.getMessages();
				desc.setValidationErrorMessages(errors);
				if (!errors.isEmpty()) {
					System.out.println("============================================Load Error=====================================================================================");
					for (ValidationMessage er : errors) {
						System.out.println("Object '" + id + "':" + er.getFormattedMessage());
					}
					System.out.println("=================================================================================================================================");
				}
				// todo -this is a hack
				if (vc.containsMessage(Severity.INK_ERROR)) {
					desc.setInvalid();
					m.setLifeCycleState(LifeCycleState.INVALID);
					throw new ObjectLoadingException(state, errors, null, desc.getResource(), id);
				}
			}
			
		}catch(ObjectLoadingException e){
			throw e;
		}catch(Exception e){
			ParseError error = new ParseError(desc.getLineNumber(), 0, e.getMessage(), null);
			throw new ObjectLoadingException(state, null, Arrays.asList(error), desc.getResource(), id);
		}finally{
			vc.reset();
		}
	}

	private boolean shouldValidateResult(InkObjectState result) {
		Mirror mirror = result.reflect();
		ClassMirror cMirror = mirror.getClassMirror();
		if (!cMirror.isCoreObject() && !result.reflect().getClassMirror().getDescriptor().isValid()) {
			return false;
		}
		Mirror superObject = mirror.getSuper();
		if (superObject != null && !superObject.isCoreObject()) {
			return superObject.getDescriptor().isValid();
		}
		return true;
	}

	@Override
	public void scan(DslFactory ownerFactory) {
		try {
			loadElements(ownerFactory);
		} catch (IOException e) {
			// TODO - log error
		}
		List<String> startupObjects = locateStartupObjects(ownerFactory.getDslPackage());
		List<InkObjectState> objects = new ArrayList<InkObjectState>();
		ClassMirror tc = getContext().getObject(CoreNotations.Ids.TRAIT).reflect();
		InkObjectState o = null;
		for (String id : startupObjects) {
			try {
				ElementDescriptor<D> ed = elements.get(id);
				if (ed.isValid()) {
					o = getObject(id, ownerFactory.getAppContext());
					objects.add(o);
				}
			} catch (ObjectLoadingException e) {
				e.printStackTrace();
			}
		}
		InkClass cls;
		for (ListIterator<InkObjectState> iter = objects.listIterator(); iter.hasNext();) {
			o = iter.next();
			cls = o.getMeta();
			if (((ClassMirror) cls.reflect()).isSubClassOf(tc) && ((TraitClass) cls).getKind().isDetachable()) {
				iter.remove();
				ownerFactory.registerTrait((TraitState) o);
			}
		}
		for (ListIterator<InkObjectState> iter = objects.listIterator(); iter.hasNext();) {
			o = iter.next();
			ownerFactory.register(o);
		}
	}

	private InkReader<D> createReader() {
		return readerCls.newInstance(ownerFactory.getAppContext()).getBehavior();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private synchronized void loadElements(DslFactory ownerFactory) throws IOException {
		this.ownerFactory = ownerFactory;
		folders = VMConfig.instance().getResourceResolver().getDslResourcesLocation(ownerFactory);
		// TODO - the reader should be a property on DSLoader
		readerCls = ownerFactory.getObject(CoreNotations.Ids.INK_READER.toString());
		InkReader<D> reader = createReader();
		vc = ((InkClass) ownerFactory.getObject(CoreNotations.Ids.VALIDATION_CONTEXT.toString())).newInstance().getBehavior();
		List<ElementDescriptor<D>> fileElements;
		String id;
		try {
			for (File f : getInkFiles()) {
				fileElements = reader.extractRawData(f, ownerFactory.getAppContext());
				if (!reader.containsErrors()) {
					List<String> ids = new ArrayList<String>(25);
					file2Elements.put(f.getAbsolutePath(), ids);
					ElementDescriptor existingDesc;
					for (ElementDescriptor desc : fileElements) {
						id = desc.getId();
						if (id != null) {
							if (id.indexOf(InkNotations.Path_Syntax.NAMESPACE_DELIMITER_C) < 0) {
								id = ownerFactory.getNamespace() + InkNotations.Path_Syntax.NAMESPACE_DELIMITER_C + id;
							}
							if ((existingDesc = elements.get(id)) != null) {
								String postfix = "";
								if (existingDesc.getResource().equals(desc.getResource())) {
									postfix = "file " + existingDesc.getResource().getName();
								} else {
									postfix = "the following files: " + existingDesc.getResource().getName() + "," + desc.getResource().getName();
								}
								vc.add(null, getMessage(), Severity.INK_ERROR, ResourceType.INK, false, id, postfix);
								List<ValidationMessage> errors = vc.getMessages();
								existingDesc.setValidationErrorMessages(errors);
								vc.reset();
								continue;
							}
							elements.put(id, desc);
							ids.add(id);
						} else {
							// TODO - log error
						}
					}
				} else {
					for (ParseError pe : reader.getErrors()) {
						// TODO - should refactor this
						System.out.println("Error while parsing ink file '" + f.getAbsolutePath() + "', line number " + pe.getLineNumber() + ";" + pe.getDescription());
					}
				}
			}
		} finally {
			vc.reset();
		}
	}

	private Message getMessage() {
		// this is to solve eclipse bootsrapping problem. need to fixed using metaclass
		return VMMain.getCoreFactory().getObject(CoreNotations.Ids.DUPLICATE_ID);
	}

	private String findSuperId(String id) {
		String result = null;
		ElementDescriptor<D> superElem = elements.get(id);
		if (superElem != null) {
			result = superElem.getSuperId();
		} else {
			InkObjectState ios = ownerFactory.getState(id, false);
			if (ios != null) {
				Mirror superObject = ios.reflect().getSuper();
				if (superObject != null) {
					result = superObject.getId();
				}
			} else {
				System.out.println("Could not locate object with id " + id);
			}
		}
		return result;
	}

	private List<String> locateStartupObjects(String dslPackage) {
		List<String> result = new ArrayList<String>();
		for (ElementDescriptor<D> elem : elements.values()) {
			String superId = findSuperId(elem.getClassId());
			while (superId != null) {
				if (superId.equals(CoreNotations.Ids.TRAIT)) {
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
		List<File> result = new ArrayList<File>();
		for(File f : folders){
			result.addAll(FileUtils.listInkFiles(f));
		}
		return result;
	}

	@Override
	public List<InkErrorDetails> collectErrors() {
		List<InkErrorDetails> result = new ArrayList<InkErrorDetails>();
		ElementDescriptor<D> elem;
		InkErrorDetails err;
		for (Map.Entry<String, ElementDescriptor<D>> en : elements.entrySet()) {
			elem = en.getValue();
			if (elem.getParsingErrors() != null) {
				for (ParseError pe : elem.getParsingErrors()) {
					err = new InkErrorDetails(en.getKey(), pe.getLineNumber(), pe.getDescription(), elem.getResource(), ResourceType.INK);
					result.add(err);
				}
			}
			if (elem.getValidationErrorMessages() != null) {
				for (ValidationMessage vm : elem.getValidationErrorMessages()) {
					err = new InkErrorDetails(en.getKey(), vm.getErrorPath(), vm.getFormattedMessage(), elem.getResource(), vm.getResourceType());
					result.add(err);
				}
			}
		}
		return result;
	}

	@Override
	public ElementDescriptor<?> getDescriptor(String id) {
		return elements.get(id);
	}

	@Override
	public List<String> getElements(String filepath) {
		return file2Elements.get(new File(filepath).getAbsolutePath());
	}
	
	@Override
	public List<String> getElementsIds(){
		return new ArrayList<String>(elements.keySet());
	}

}
