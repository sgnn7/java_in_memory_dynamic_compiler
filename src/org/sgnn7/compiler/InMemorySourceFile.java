package org.sgnn7.compiler;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.tools.SimpleJavaFileObject;

public class InMemorySourceFile extends SimpleJavaFileObject {
	private static final String JAVA_EXTENSION = Kind.SOURCE.extension;
	private final String content;

	public InMemorySourceFile(String name, String content) throws URISyntaxException {
		super(new URI(name + JAVA_EXTENSION), Kind.SOURCE);
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
		return getContent();
	}
}
