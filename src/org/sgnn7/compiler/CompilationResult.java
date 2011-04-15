package org.sgnn7.compiler;

public class CompilationResult {
	Class<?> clazz;
	boolean compilationResult;
	private final String errorMessage;

	public CompilationResult(boolean compilationResult, Class<?> clazz, String errorMessage) {
		this.compilationResult = compilationResult;
		this.clazz = clazz;
		this.errorMessage = errorMessage;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public boolean isCompilationSuccessful() {
		return compilationResult;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}
