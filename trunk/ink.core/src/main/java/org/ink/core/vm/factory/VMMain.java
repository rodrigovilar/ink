package org.ink.core.vm.factory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ikayzo.sdl.SDLParseException;
import org.ikayzo.sdl.Tag;
import org.ink.core.utils.sdl.SdlParser;
import org.ink.core.vm.exceptions.InkBootException;
import org.ink.core.vm.factory.internal.CoreNotations;
import org.ink.core.vm.lang.InkClass;
import org.ink.core.vm.serialization.InkReader;
import org.ink.core.vm.utils.InkNotations;
import org.ink.core.vm.utils.file.FileUtils;

/**
 * @author Lior Schachter
 */
public class VMMain {
	
	private static DslFactory factory;
	
	public static void restart(){
		stop();
		start();
	}
	
	
	public static void stop(){
		//vm.destroy();
	}
	
	public static DslFactory getDefaultFactory(){
		if(factory==null){
			throw new InkBootException("Error in intialization. The method start() should be called first.");
		}
		return factory;
	}
	
	public static void start(){
		if(factory==null){
			synchronized (VMMain.class) {
				if(factory==null){
					String pathArgument = System.getProperty("ink.classpath");
					if(pathArgument!=null){
						List<String> pathsList = new ArrayList<String>();
						StringBuilder builder = new StringBuilder(40);
						for(char c : pathArgument.toCharArray()){
							if(c==','){
								pathsList.add(builder.toString());
								builder = new StringBuilder(40);
							}
							else if(builder.length()==0 && c==' '){
								continue;
							}else{
								builder.append(c);
							}
						}
						if(builder.length()>0){
							pathsList.add(builder.toString());
						}
						if(pathsList.size()>0){
							String[] paths = pathsList.toArray(new String[]{});
							loadApplication(paths);
						}
					}else{
						loadApplication(null);
					}
					
				}
			}
		}
	}
	
	private static void loadApplication(String[] sourcePaths) {
		loadFactories(sourcePaths);
	}
	
	private static void loadFactories(String[] sourcePaths) {
		if(sourcePaths==null){
			factory = loadCoreFactory();
		}else{
			DslFactory coreFactory = loadCoreFactory();
			List<DslFactory> factories = new ArrayList<DslFactory>();
			for(String p : sourcePaths){
				factories.addAll(collectFactories(p, coreFactory));
			}
			Collections.sort(factories);
			if(factories.size()==1){
				factory = factories.get(0);
			}else if(factories.size()>1){
				if(factories.get(0).compareTo(factories.get(1))==0){
					String defaultFactoryNS = System.getProperty("default.factory");
					if(defaultFactoryNS==null){
						System.out.println("More than one possible default factory was found. Using the factory '" + factories.get(0).getNamespace() +"' as default.");
					}
					factory = factories.get(0);
					System.out.println("More than one possible default factory was found");
				}else{
					
				}
			}else{
				System.out.println("No DSL factory found on classpath. Using core factory as default factory.");
			}
		}
	}
	
	private static List<DslFactory> collectFactories(String path, DslFactory coreFactory){
		List<DslFactory> result = new ArrayList<DslFactory>();
		File inkFile = new File(path);
		if(!inkFile.exists()){
			//TODO log warning
			return result;
		}
		if(inkFile.isFile()){
			result.add(locateFactory(inkFile, coreFactory));
		}else{
			result.addAll(loadDirectory(inkFile, coreFactory));
		}
		return result;
	}
	

	private static List<DslFactory> loadDirectory(File dir, DslFactory coreFactory) {
		List<DslFactory> result = new ArrayList<DslFactory>();
		for(File f : FileUtils.listInkFiles(dir)){
			result.add(locateFactory(f, coreFactory));
		}
		return result;
	}


	private static DslFactory locateFactory(File inkFile, DslFactory coreFactory) {
		try {
			DslFactory result = null;
			Tag content = SdlParser.parse(inkFile);
			List<Tag> elements = content.getChildren();
			String classId;
			InkClass readerCls = coreFactory.getObject(CoreNotations.Ids.INK_READER.toString());
			InkReader<Tag> reader = readerCls.newInstance(coreFactory.getAppContext(), false, true).getBehavior(); 
			for(Tag elem : elements){
				try{
					classId = (String)elem.getAttribute(InkNotations.Path_Syntax.CLASS_ATTRIBUTE);
					if(classId!=null){
						//TODO need to check also if subclass of
						if(classId.equals(CoreNotations.Ids.DSL_FACTORY)){
							long start = System.currentTimeMillis();
							result = reader.read(elem, coreFactory.getAppContext()).getBehavior();
							System.out.println("DSL factory '" + result.getNamespace() + "' loaded in " + (System.currentTimeMillis()-start) +" millis.");
						}
					}
				}catch(ClassCastException e){
					e.printStackTrace();
					//TODO log error. no need to throw an exception, this is not the place (InkReader should do that...
				}
			}
			return result;
		} catch (IOException e) {
			throw new InkBootException("Could not open the file '"+inkFile.getAbsolutePath() +"'.", e);
		} catch (SDLParseException e) {
			throw new InkBootException("Could not parse the file '"+inkFile.getAbsolutePath() +"'.", e);
		} 
	}

	private static DslFactory loadCoreFactory() {
		long start = System.currentTimeMillis();
		DslFactoryImpl<DslFactoryState> factory = new DslFactoryImpl<DslFactoryState>();
		factory.boot();
		System.out.println("DSL factory '" + factory.getNamespace() + "' loaded in " + (System.currentTimeMillis()-start) +" millis.");
		return factory;
	}

	
	public static void main(String[] args) {
	}
	
}
