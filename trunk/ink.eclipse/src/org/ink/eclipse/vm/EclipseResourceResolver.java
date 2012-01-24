package org.ink.eclipse.vm;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.ink.core.vm.factory.DslFactory;
import org.ink.core.vm.factory.resources.CoreResourceResolver;
import org.ink.core.vm.factory.resources.JavaClassDescription;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.eclipse.utils.EclipseUtils;

public class EclipseResourceResolver extends CoreResourceResolver {

	@Override
	public File[] getDslResourcesLocation(DslFactory factory) {
		File dslConfFile = factory.getConfigurationFile();
		File loc1 = new File(dslConfFile.getParentFile().getAbsolutePath() + File.separatorChar + "src" + File.separatorChar + "main" + File.separatorChar + "dsl" + File.separatorChar + factory.getDslPackage().replace('.', File.separatorChar));
		File loc2 = new File(dslConfFile.getParentFile().getAbsolutePath() + File.separatorChar + "src" + File.separatorChar + "test" + File.separatorChar + "dsl" + File.separatorChar + factory.getDslPackage().replace('.', File.separatorChar));
		return new File[]{loc1, loc2};
	}

	private JavaClassDescription getClassDetails(ClassMirror cm, IJavaElement je, String shortClassName) {
		IType t;
		if (je instanceof IClassFile) {
			t = ((IClassFile) je).getType();
		} else {
			t = ((ICompilationUnit) je).getType(shortClassName);
		}
		try {
			ITypeHierarchy hier = t.newSupertypeHierarchy(null);
			JavaClassDescription result = new JavaClassDescription();
			IType superClass = hier.getSuperclass(t);
			if (superClass != null) {
				result.setSuperClass(superClass.getFullyQualifiedName());
			}
			IType[] interfaces = hier.getSuperInterfaces(t);
			if (interfaces != null && interfaces.length > 0) {
				Set<String> s = new HashSet<String>(interfaces.length);
				for (IType i : interfaces) {
					s.add(i.getFullyQualifiedName());
				}
				result.setInterfaces(s);
			}
			return result;
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public JavaClassDescription getBehaviorClassDescription(ClassMirror cm) {
		IJavaElement je = EclipseUtils.getJavaBehaviorElement(cm);
		if (je != null) {
			return getClassDetails(cm, je, getBehaviorShortClassName(cm));
		}
		return null;
	}

	@Override
	public JavaClassDescription getInterfaceDescription(ClassMirror cm) {
		IJavaElement je = EclipseUtils.getJavaInterfaceElement(cm);
		if (je != null) {
			return getClassDetails(cm, je, getInterfaceClassShortName(cm));
		}
		return null;
	}

}
