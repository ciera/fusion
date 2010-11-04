package edu.cmu.cs.fusion.constraint;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jdt.core.CompletionRequestor;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.ICompletionRequestor;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IInitializer;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.IWorkingCopy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;

import edu.cmu.cs.crystal.util.Pair;

public class XMLContext implements IType {

	private String fileName;
	private Map<String, String> quals;
	
	public XMLContext(String fileName) {
		this.fileName = fileName;
		quals = new HashMap<String, String>();
	}
	
	public void addType(String typeName) {
		Pair<String, String> name = splitTypeName(typeName);
		assert (quals.get(name.snd()) == null);
		quals.put(name.snd(), typeName);
	}
	
	public String getFullyQualifiedName() {
		return "XMLContext for file " + fileName;
	}

	public String[][] resolveType(String typeName) throws JavaModelException {
		String qualType = quals.containsKey(typeName) ? quals.get(typeName) : typeName;
		
		Pair<String, String> name = splitTypeName(qualType);
		return new String[][] {new String[] {name.fst(), name.snd()}};	
	}

	public Pair<String, String> splitTypeName(String typeName) {
		int lastDot = typeName.lastIndexOf('.');
		String name, qual;
		if (lastDot != -1) {
			name = typeName.substring(lastDot + 1);
			qual = typeName.substring(0, lastDot);
		}
		else {
			name = typeName;
			qual = "";
		}
		return new Pair<String, String>(qual, name);
	}

	public void codeComplete(char[] snippet, int insertion, int position,
			char[][] localVariableTypeNames, char[][] localVariableNames,
			int[] localVariableModifiers, boolean isStatic,
			ICompletionRequestor requestor) throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public void codeComplete(char[] snippet, int insertion, int position,
			char[][] localVariableTypeNames, char[][] localVariableNames,
			int[] localVariableModifiers, boolean isStatic,
			CompletionRequestor requestor) throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public void codeComplete(char[] snippet, int insertion, int position,
			char[][] localVariableTypeNames, char[][] localVariableNames,
			int[] localVariableModifiers, boolean isStatic,
			ICompletionRequestor requestor, WorkingCopyOwner owner)
			throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public void codeComplete(char[] snippet, int insertion, int position,
			char[][] localVariableTypeNames, char[][] localVariableNames,
			int[] localVariableModifiers, boolean isStatic,
			CompletionRequestor requestor, WorkingCopyOwner owner)
			throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public IField createField(String contents, IJavaElement sibling,
			boolean force, IProgressMonitor monitor) throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public IInitializer createInitializer(String contents,
			IJavaElement sibling, IProgressMonitor monitor)
			throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public IMethod createMethod(String contents, IJavaElement sibling,
			boolean force, IProgressMonitor monitor) throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public IType createType(String contents, IJavaElement sibling,
			boolean force, IProgressMonitor monitor) throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public IMethod[] findMethods(IMethod method) {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public IJavaElement[] getChildrenForCategory(String category)
			throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public String getElementName() {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public IField getField(String name) {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public IField[] getFields() throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public String getFullyQualifiedName(char enclosingTypeSeparator) {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public String getFullyQualifiedParameterizedName()
			throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public IInitializer getInitializer(int occurrenceCount) {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public IInitializer[] getInitializers() throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public String getKey() {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public IMethod getMethod(String name, String[] parameterTypeSignatures) {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public IMethod[] getMethods() throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public IPackageFragment getPackageFragment() {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public String[] getSuperInterfaceNames() throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public String[] getSuperInterfaceTypeSignatures() throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public String getSuperclassName() throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public String getSuperclassTypeSignature() throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public IType getType(String name) {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public ITypeParameter getTypeParameter(String name) {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public String[] getTypeParameterSignatures() throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public ITypeParameter[] getTypeParameters() throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public String getTypeQualifiedName() {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public String getTypeQualifiedName(char enclosingTypeSeparator) {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public IType[] getTypes() throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public boolean isAnnotation() throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public boolean isAnonymous() throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public boolean isClass() throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public boolean isEnum() throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public boolean isInterface() throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public boolean isLocal() throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public boolean isMember() throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public boolean isResolved() {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public ITypeHierarchy loadTypeHierachy(InputStream input,
			IProgressMonitor monitor) throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public ITypeHierarchy newSupertypeHierarchy(IProgressMonitor monitor)
			throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public ITypeHierarchy newSupertypeHierarchy(
			ICompilationUnit[] workingCopies, IProgressMonitor monitor)
			throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public ITypeHierarchy newSupertypeHierarchy(IWorkingCopy[] workingCopies,
			IProgressMonitor monitor) throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public ITypeHierarchy newSupertypeHierarchy(WorkingCopyOwner owner,
			IProgressMonitor monitor) throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public ITypeHierarchy newTypeHierarchy(IProgressMonitor monitor)
			throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public ITypeHierarchy newTypeHierarchy(IJavaProject project,
			IProgressMonitor monitor) throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public ITypeHierarchy newTypeHierarchy(ICompilationUnit[] workingCopies,
			IProgressMonitor monitor) throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public ITypeHierarchy newTypeHierarchy(IWorkingCopy[] workingCopies,
			IProgressMonitor monitor) throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public ITypeHierarchy newTypeHierarchy(WorkingCopyOwner owner,
			IProgressMonitor monitor) throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public ITypeHierarchy newTypeHierarchy(IJavaProject project,
			WorkingCopyOwner owner, IProgressMonitor monitor)
			throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public String[][] resolveType(String typeName, WorkingCopyOwner owner)
			throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public String[] getCategories() throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public IClassFile getClassFile() {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public ICompilationUnit getCompilationUnit() {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public IType getDeclaringType() {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public int getFlags() throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public ISourceRange getJavadocRange() throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public ISourceRange getNameRange() throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public int getOccurrenceCount() {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public IType getType(String name, int occurrenceCount) {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public ITypeRoot getTypeRoot() {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public boolean isBinary() {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public boolean exists() {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public IJavaElement getAncestor(int ancestorType) {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public String getAttachedJavadoc(IProgressMonitor monitor)
			throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");

	}

	public IResource getCorrespondingResource() throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");

	}

	public int getElementType() {
		throw new UnsupportedOperationException("Stub type for XML file");

	}

	public String getHandleIdentifier() {
		throw new UnsupportedOperationException("Stub type for XML file");

	}

	public IJavaModel getJavaModel() {
		throw new UnsupportedOperationException("Stub type for XML file");

	}

	public IJavaProject getJavaProject() {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public IOpenable getOpenable() {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public IJavaElement getParent() {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public IPath getPath() {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public IJavaElement getPrimaryElement() {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public IResource getResource() {
		throw new UnsupportedOperationException("Stub type for XML file");

	}

	public ISchedulingRule getSchedulingRule() {
		throw new UnsupportedOperationException("Stub type for XML file");

	}

	public IResource getUnderlyingResource() throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");

	}

	public boolean isReadOnly() {
		throw new UnsupportedOperationException("Stub type for XML file");

	}

	public boolean isStructureKnown() throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");

	}

	public Object getAdapter(Class adapter) {
		throw new UnsupportedOperationException("Stub type for XML file");

	}

	public String getSource() throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public ISourceRange getSourceRange() throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public void copy(IJavaElement container, IJavaElement sibling,
			String rename, boolean replace, IProgressMonitor monitor)
			throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public void delete(boolean force, IProgressMonitor monitor)
			throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public void move(IJavaElement container, IJavaElement sibling,
			String rename, boolean replace, IProgressMonitor monitor)
			throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public void rename(String name, boolean replace, IProgressMonitor monitor)
			throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public IJavaElement[] getChildren() throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");

	}

	public boolean hasChildren() throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");

	}

	public IAnnotation getAnnotation(String name) {
		throw new UnsupportedOperationException("Stub type for XML file");

	}

	public IAnnotation[] getAnnotations() throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");

	}

	public void codeComplete(char[] snippet, int insertion, int position,
			char[][] localVariableTypeNames, char[][] localVariableNames,
			int[] localVariableModifiers, boolean isStatic,
			CompletionRequestor requestor, IProgressMonitor monitor)
			throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

	public void codeComplete(char[] snippet, int insertion, int position,
			char[][] localVariableTypeNames, char[][] localVariableNames,
			int[] localVariableModifiers, boolean isStatic,
			CompletionRequestor requestor, WorkingCopyOwner owner,
			IProgressMonitor monitor) throws JavaModelException {
		throw new UnsupportedOperationException("Stub type for XML file");
	}

}
