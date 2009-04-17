package edu.cmu.cs.fusion.test.constraint.operations;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IPackageBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;

public class StubTypeBinding implements ITypeBinding {
	private String qualifiedName;
	
	public StubTypeBinding(String qualifiedName) {
		this.qualifiedName = qualifiedName;
	}

	public String getQualifiedName() {
		return qualifiedName;
	}

	public ITypeBinding createArrayType(int dimension) {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public String getBinaryName() {
		throw new UnsupportedOperationException("stub not complete");
	}

	public ITypeBinding getBound() {
		throw new UnsupportedOperationException("stub not complete");
	}

	public ITypeBinding getComponentType() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public IVariableBinding[] getDeclaredFields() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public IMethodBinding[] getDeclaredMethods() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public int getDeclaredModifiers() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public ITypeBinding[] getDeclaredTypes() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public ITypeBinding getDeclaringClass() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public IMethodBinding getDeclaringMethod() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public int getDimensions() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public ITypeBinding getElementType() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public ITypeBinding getErasure() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public ITypeBinding[] getInterfaces() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public int getModifiers() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public String getName() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public IPackageBinding getPackage() {
		throw new UnsupportedOperationException("stub not complete");
		
	}


	public ITypeBinding getSuperclass() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public ITypeBinding[] getTypeArguments() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public ITypeBinding[] getTypeBounds() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public ITypeBinding getTypeDeclaration() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public ITypeBinding[] getTypeParameters() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public ITypeBinding getWildcard() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public boolean isAnnotation() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public boolean isAnonymous() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public boolean isArray() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public boolean isAssignmentCompatible(ITypeBinding variableType) {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public boolean isCapture() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public boolean isCastCompatible(ITypeBinding type) {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public boolean isClass() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public boolean isEnum() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public boolean isFromSource() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public boolean isGenericType() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public boolean isInterface() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public boolean isLocal() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public boolean isMember() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public boolean isNested() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public boolean isNullType() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public boolean isParameterizedType() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public boolean isPrimitive() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public boolean isRawType() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public boolean isSubTypeCompatible(ITypeBinding type) {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public boolean isTopLevel() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public boolean isTypeVariable() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public boolean isUpperbound() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public boolean isWildcardType() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public IAnnotationBinding[] getAnnotations() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public IJavaElement getJavaElement() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public String getKey() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public int getKind() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public boolean isDeprecated() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public boolean isEqualTo(IBinding binding) {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public boolean isRecovered() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

	public boolean isSynthetic() {
		throw new UnsupportedOperationException("stub not complete");
		
	}

}
