package org.sgnn7.compiler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.tools.SimpleJavaFileObject;

public class InMemoryClassFile extends SimpleJavaFileObject {
	private ByteArrayOutputStream outputStream = null;

	public InMemoryClassFile(String name) throws URISyntaxException {
		super(new URI(name), Kind.CLASS);
	}

	@Override
	public InputStream openInputStream() throws IOException {
		return new ByteArrayInputStream(getBytes());
	}

	@Override
	public OutputStream openOutputStream() throws IOException {
		outputStream = new ByteArrayOutputStream();
		return outputStream;
	}

	public byte[] getBytes() {
		return outputStream.toByteArray();
	}
}
