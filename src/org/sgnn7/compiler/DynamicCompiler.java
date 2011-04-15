package org.sgnn7.compiler;

import java.util.Arrays;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;

public class DynamicCompiler {
	private final JavaCompiler compiler;
	private final InMemoryClassLoader inMemoryClassLoader;

	public DynamicCompiler(JavaCompiler compiler, InMemoryClassLoader inMemoryClassLoader) {
		this.compiler = compiler;
		this.inMemoryClassLoader = inMemoryClassLoader;
	}

	public CompilationResult compile(JavaFileObject sourceFileObject) throws Exception {
		if (compiler == null) {
			throw new RuntimeException("Error! Compiler was not found");
		}

		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(sourceFileObject);
		DiagnosticCollector<JavaFileObject> diagnosticsCollector = new DiagnosticCollector<JavaFileObject>();
		InMemoryFileManager inMemoryFileManager = new InMemoryFileManager(compiler.getStandardFileManager(
				diagnosticsCollector, null, null), inMemoryClassLoader);

		CompilationTask task = compiler.getTask(null, inMemoryFileManager, diagnostics, null, null, compilationUnits);

		boolean success = task.call();
		String errorMessage = "";
		for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
			System.out.println(diagnostic.getCode());
			System.out.println(diagnostic.getKind());
			System.out.println(diagnostic.getPosition());
			System.out.println(diagnostic.getStartPosition());
			System.out.println(diagnostic.getEndPosition());
			System.out.println(diagnostic.getSource());
			System.out.println(diagnostic.getMessage(null));
			errorMessage += "Error: " + diagnostic.getMessage(null) + "\n";
		}
		System.out.println("Success: " + success);

		return new CompilationResult(success, null, errorMessage);
	}
}
