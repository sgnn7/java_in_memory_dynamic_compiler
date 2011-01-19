package org.sgnn7.compiler;

public class CompilationResult {
	Class<?> clazz;
	boolean compilationResult;

	public CompilationResult(boolean compilationResult, Class<?> clazz) {
		this.compilationResult = compilationResult;
		this.clazz = clazz;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public boolean isCompilationResult() {
		return compilationResult;
	}
}
