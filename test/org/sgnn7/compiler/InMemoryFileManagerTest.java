package org.sgnn7.compiler;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Random;
import java.util.UUID;

import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class InMemoryFileManagerTest {
	@Mock
	private JavaFileManager fileManager;
	@Mock
	private InMemoryClassLoader inMemoryClassLoader;

	private InMemoryFileManager testObject;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		testObject = new InMemoryFileManager(fileManager, inMemoryClassLoader);
	}

	@Test
	public void getJavaFileForOutputReturnsAnInMemoryClassFileWithCorrectParametersSet() {
		String className = UUID.randomUUID().toString();

		JavaFileObject outputFileObject = testObject.getJavaFileForOutput(null, className, null, null);

		assertTrue(outputFileObject instanceof InMemoryClassFile);
		assertEquals(className, outputFileObject.getName());
	}

	@Test
	public void getJavaFileForOutputStoresTheClassInTheInMemoryClassLoader() {
		String className = UUID.randomUUID().toString();
		Kind kind = Kind.values()[new Random().nextInt(Kind.values().length)];

		JavaFileObject outputFileObject = testObject.getJavaFileForOutput(null, className, kind, null);

		ArgumentCaptor<InMemoryClassFile> classCaptor = ArgumentCaptor.forClass(InMemoryClassFile.class);
		verify(inMemoryClassLoader).addClass(eq(className), classCaptor.capture());

		assertEquals(outputFileObject, classCaptor.getValue());
	}
}
