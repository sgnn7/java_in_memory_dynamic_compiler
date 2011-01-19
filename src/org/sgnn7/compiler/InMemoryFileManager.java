package org.sgnn7.compiler;

import java.net.URISyntaxException;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;

public class InMemoryFileManager extends ForwardingJavaFileManager<JavaFileManager> {
	private final InMemoryClassLoader inMemoryClassLoader;

	public InMemoryFileManager(JavaFileManager fileManager, InMemoryClassLoader inMemoryClassLoader) {
		super(fileManager);
		this.inMemoryClassLoader = inMemoryClassLoader;
	}

	@Override
	public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling) {
		// System.out.println("Location: " + location.toString());
		// System.out.println("Class Name: " + className);
		// System.out.println("Kind: " + kind);
		// System.out.println("File Object: " + sibling.getName());

		InMemoryClassFile targetFileObject = null;
		try {
			targetFileObject = new InMemoryClassFile(className);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		inMemoryClassLoader.addClass(className, targetFileObject);
		return targetFileObject;
	}
}
