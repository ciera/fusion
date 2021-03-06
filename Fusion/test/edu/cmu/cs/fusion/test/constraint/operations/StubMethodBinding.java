package edu.cmu.cs.fusion.test.constraint.operations;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

import edu.cmu.cs.fusion.xml.NamedTypeBinding;

public class StubMethodBinding implements IMethodBinding {
	private NamedTypeBinding receiverType;
	private NamedTypeBinding[] paramTypes;
	private String name;

	public StubMethodBinding(String name, NamedTypeBinding receiverType,
			NamedTypeBinding[] paramTypes) {
		this.name = name;
		this.receiverType = receiverType;
		this.paramTypes = paramTypes;
	}


	public StubMethodBinding(NamedTypeBinding receiverType,
			NamedTypeBinding[] paramTypes) {
		this.receiverType = receiverType;
		this.paramTypes = paramTypes;
	}

	public ITypeBinding getDeclaringClass() {
		return receiverType;
	}

	public ITypeBinding[] getParameterTypes() {
		return paramTypes;
	}

	public Object getDefaultValue() {
		throw new UnsupportedOperationException("stub not complete");
	}

	public ITypeBinding[] getExceptionTypes() {
		throw new UnsupportedOperationException("stub not complete");
	}

	public IMethodBinding getMethodDeclaration() {
		throw new UnsupportedOperationException("stub not complete");
	}

	public String getName() {
		return name;
	}

	public IAnnotationBinding[] getParameterAnnotations(int paramIndex) {
		throw new UnsupportedOperationException("stub not complete");

	}


	public ITypeBinding getReturnType() {
		throw new UnsupportedOperationException("stub not complete");

	}

	public ITypeBinding[] getTypeArguments() {
		throw new UnsupportedOperationException("stub not complete");

	}

	public ITypeBinding[] getTypeParameters() {
		throw new UnsupportedOperationException("stub not complete");

	}

	public boolean isAnnotationMember() {
		throw new UnsupportedOperationException("stub not complete");
	}

	public boolean isConstructor() {
		throw new UnsupportedOperationException("stub not complete");
	}

	public boolean isDefaultConstructor() {
		throw new UnsupportedOperationException("stub not complete");
	}

	public boolean isGenericMethod() {
		throw new UnsupportedOperationException("stub not complete");
	}

	public boolean isParameterizedMethod() {
		throw new UnsupportedOperationException("stub not complete");

	}

	public boolean isRawMethod() {
		throw new UnsupportedOperationException("stub not complete");
	}

	public boolean isSubsignature(IMethodBinding otherMethod) {
		throw new UnsupportedOperationException("stub not complete");
	}

	public boolean isVarargs() {
		throw new UnsupportedOperationException("stub not complete");
	}

	public boolean overrides(IMethodBinding method) {
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

	public int getModifiers() {
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
