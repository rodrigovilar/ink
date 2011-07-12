package org.ink.eclipse.vm;

import java.io.File;

import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.ink.core.vm.factory.DslFactory;
import org.ink.core.vm.factory.resources.ClassStructure;
import org.ink.core.vm.factory.resources.CoreResourceResolver;
import org.ink.core.vm.lang.InkClassState;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.eclipse.utils.EclipseUtils;

public class EclipseResourceResolver extends CoreResourceResolver{

	@Override
	public File getDslResourcesLocation(DslFactory factory) {
		File dslConfFile = factory.getConfigurationFile();
		File result = new File(dslConfFile.getParentFile().getAbsolutePath() +
				File.separatorChar + "src" + File.separatorChar +"main" + File.separatorChar +"dsl"
				+ File.separatorChar +factory.getDslPackage().replace('.', File.separatorChar));
		return result;
	}

	@Override
	public ClassStructure getClassDetails(InkClassState cls) {
		ClassMirror cm = cls.reflect();
		IJavaElement je = EclipseUtils.getJavaElement(cm);
		IType t;
		if(je instanceof IClassFile){
			t = ((IClassFile)je).getType();
		}else{
			t = ((ICompilationUnit)je).getType(getBehaviorShortClassName(cls));
		}
		ClassStructure result = new ClassStructure();
		result.setSuperClass(t.getFullyQualifiedName());
//		try{
//			String[] interfaces = t.getSuperInterfaceTypeSignatures();
//		}catch(JavaModelException e){
//			e.printStackTrace();
//		}
		return null;
	}

}
