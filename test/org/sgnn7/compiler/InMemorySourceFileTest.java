package org.sgnn7.compiler;

import static org.junit.Assert.*;

import java.util.UUID;

import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;

import org.junit.Test;

public class InMemorySourceFileTest {
	@Test
	public void onceCreatedTheContentIsEqualToPassedInParameter() throws Exception {
		String name = UUID.randomUUID().toString();
		String content = UUID.randomUUID().toString();
		JavaFileObject testObject = new InMemorySourceFile(name, content);

		assertEquals(content, testObject.getCharContent(false));
		assertEquals(content, testObject.getCharContent(true));
		assertEquals(content, InMemorySourceFile.class.cast(testObject).getContent());
	}

	@Test
	public void onceCreatedTheUriIsEqualToPassedInParameter() throws Exception {
		String name = UUID.randomUUID().toString();
		String content = UUID.randomUUID().toString();
		JavaFileObject testObject = new InMemorySourceFile(name, content);

		assertEquals(name + ".java", testObject.getName());
	}

	@Test
	public void kindIsDefaultedToSource() throws Exception {
		String name = UUID.randomUUID().toString();
		String content = UUID.randomUUID().toString();
		JavaFileObject testObject = new InMemorySourceFile(name, content);

		assertEquals(Kind.SOURCE, testObject.getKind());
	}
}
