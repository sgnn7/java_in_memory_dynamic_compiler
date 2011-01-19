package org.sgnn7.compiler;

import static org.junit.Assert.*;

import java.io.OutputStream;
import java.util.UUID;

import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class InMemoryClassFileTest {
	@Test
	public void openInputStreamGetsContentPlacedIntoItThroughOutputStream() throws Exception {
		String name = UUID.randomUUID().toString();
		String content = UUID.randomUUID().toString();
		JavaFileObject testObject = new InMemoryClassFile(name);

		OutputStream outputStream = testObject.openOutputStream();
		IOUtils.write(content.getBytes(), outputStream);

		assertEquals(content, new String(IOUtils.toByteArray(testObject.openInputStream())));
	}

	@Test
	public void getBytesGetsContentPlacedIntoItThroughOutputStream() throws Exception {
		String name = UUID.randomUUID().toString();
		String content = UUID.randomUUID().toString();
		JavaFileObject testObject = new InMemoryClassFile(name);

		OutputStream outputStream = testObject.openOutputStream();
		IOUtils.write(content.getBytes(), outputStream);

		assertEquals(content, new String(InMemoryClassFile.class.cast(testObject).getBytes()));
	}

	@Test
	public void onceCreatedTheUriIsEqualToPassedInParameter() throws Exception {
		String name = UUID.randomUUID().toString();
		JavaFileObject testObject = new InMemoryClassFile(name);

		assertEquals(name, testObject.getName());
	}

	@Test
	public void kindIsDefaultedToSource() throws Exception {
		String name = UUID.randomUUID().toString();
		JavaFileObject testObject = new InMemoryClassFile(name);

		assertEquals(Kind.CLASS, testObject.getKind());
	}
}
