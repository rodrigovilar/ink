package org.ink.core.vm.factory;

import java.util.List;

import org.ink.core.vm.lang.InheritanceConstraints;
import org.ink.core.vm.lang.InkClassState;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;
import org.ink.core.vm.lang.internal.annotations.CoreField;
import org.ink.core.vm.lang.internal.annotations.CoreListField;
import org.ink.core.vm.mirror.ClassMirrorState;

/**
 * @author Lior Schachter
 */
@CoreClassSpec(traitsClass=DslFactoryPersonalityState.class, mirrorClass=ClassMirrorState.class, finalValuesLocation={InkClassState.p_component_type}, finalValues={"Pure_Component"})
public interface DslFactoryState extends InkClassState{
	
	@CoreField(valuePropagationStrategy=InheritanceConstraints.Instance_Must_Override_Inherited_Value)
	public static final byte p_namespace = p_personality + 1;
	@CoreField(valuePropagationStrategy=InheritanceConstraints.Instance_Must_Override_Inherited_Value)
	public static final byte p_loader = p_namespace + 1;
	@CoreField(valuePropagationStrategy=InheritanceConstraints.Instance_Must_Override_Inherited_Value, mandatory=false)
	public static final byte p_repository = p_loader + 1;
	@CoreListField(itemName="import")
	public static final byte p_imports = p_repository + 1;
	@CoreField(valuePropagationStrategy=InheritanceConstraints.Instance_Must_Override_Inherited_Value)
	public static final byte p_dsl_package = p_imports + 1;
	@CoreField(valuePropagationStrategy=InheritanceConstraints.Instance_Must_Override_Inherited_Value)
	public static final byte p_java_package = p_dsl_package + 1;
	
	//TODO - dsl factory should have features field e.g. should have owner functionality
//	public static final byte p_features = 16;
	
	
	public static final byte t_app_context = 2;
	public static final byte t_event_dispatcher = 3;
	
	public String getNamespace();
	public void setNamespace(String value);
	
	public DslLoader getLoader();
	public void setLoader(DslLoaderState value);
	
	public DslRepository getRepository();
	public void setRepository(DslRepositoryState value);
	
	public List<? extends DslFactory> getImports();
	public void setImports(List<? extends DslFactoryState> value);
	
	public String getDslPackage();
	public void setDslPackage(String value);
	
	public String getJavaPackage();
	public void setJavaPackage(String value);
	
	// already implements org.ink.core.vm.lang.InkObjectState.getContext ???
	public Context getAppContext();
	
	public class Data extends InkClassState.Data implements DslFactoryState{

		Context context;
		
		@Override
		public void afterPropertiesSet() {
			super.afterPropertiesSet();
			if(!isAbstract()){
				context = asTrait(t_app_context);
			}
		}
		
		@Override
		public Context getAppContext(){
			return context;
		}
		
		@Override
		public String getNamespace() {
			return (String)getValue(p_namespace);
		}

		@Override
		public void setNamespace(String value) {
			setValue(p_namespace, value);
		}

		@Override
		public DslLoader getLoader() {
			return (DslLoader) getValue(p_loader);
		}

		@Override
		public DslRepository getRepository() {
			return (DslRepository) getValue(p_repository);
		}

		@Override
		public void setLoader(DslLoaderState value) {
			setValue(p_loader, value);
		}

		@Override
		public void setRepository(DslRepositoryState value) {
			setValue(p_repository, value);
		}

		@SuppressWarnings("unchecked")
		public List<? extends DslFactory> getImports(){
			return (List<? extends DslFactory>)getValue(p_imports);
		}
		
		public void setImports(List<? extends DslFactoryState> value){
			setValue(p_imports, value);
		}

		@Override
		public String getJavaPackage() {
			return (String)getValue(p_java_package);
		}

		@Override
		public void setJavaPackage(String value) {
			setValue(p_java_package, value);
		}

		@Override
		public String getDslPackage() {
			return (String)getValue(p_dsl_package);
		}

		@Override
		public void setDslPackage(String value) {
			setValue(p_dsl_package, value);
		}
		
	}

}
