package org.sgnn7.compiler;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;
import java.util.Random;
import java.util.UUID;

import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import org.junit.Before;
import org.junit.Test;

public class SGCompilerTest {
	private SGCompiler testObject;
	private InMemoryClassLoader inMemoryClassLoader;

	@Before
	public void setUp() {
		inMemoryClassLoader = new InMemoryClassLoader();
		testObject = new SGCompiler(ToolProvider.getSystemJavaCompiler(), inMemoryClassLoader);
	}

	@Test
	public void ifCompilerIsNullThrowRuntimeException() {
		testObject = new SGCompiler(null, null);
		try {
			testObject.compile(new InMemorySourceFile(UUID.randomUUID().toString(), UUID.randomUUID().toString()));
			fail("Should have thrown a RuntimeException");
		} catch (NullPointerException e) {
			fail("Wrong exception type thrown");
		} catch (RuntimeException e) {
			assertEquals("Error! Compiler was not found", e.getMessage());
		} catch (Exception e) {
			fail("Wrong exception type thrown");
		}
	}

	@Test
	public void compilerCanCompileHelloWorld() throws Exception {
		StringBuilder helloWorldSource = new StringBuilder();
		int randomExitValue = new Random().nextInt(500);

		helloWorldSource.append("public class HelloWorld {");
		helloWorldSource.append("  public static int main(String args[]) {");
		helloWorldSource.append("    System.out.println(\"Hello World!\");");
		helloWorldSource.append("    return " + randomExitValue + ";");
		helloWorldSource.append("  }");
		helloWorldSource.append("}");

		String className = "HelloWorld";
		JavaFileObject sourceFile = new InMemorySourceFile(className, helloWorldSource.toString());

		assertTrue(testObject.compile(sourceFile).compilationResult);

		Class<?> clazz = inMemoryClassLoader.findClass(className);
		Method mainMethod = clazz.getDeclaredMethod("main", new Class[] { String[].class });
		Object returnedObject = mainMethod.invoke(null, new Object[] { null });
		int returnValue = Integer.class.cast(returnedObject);
		assertEquals(randomExitValue, returnValue);
	}

	@Test
	public void compilerIsUnableToCompileFaultyProgram() throws Exception {
		InMemoryClassLoader mockInMemoryClassLoader = mock(InMemoryClassLoader.class);
		testObject = new SGCompiler(ToolProvider.getSystemJavaCompiler(), mockInMemoryClassLoader);

		StringBuilder helloWorldSource = new StringBuilder();
		int randomExitValue = new Random().nextInt(500);

		helloWorldSource.append("public classsss HelloWorld {");
		helloWorldSource.append("  public static int main(String args[]) {");
		helloWorldSource.append("    System.out.println(\"Hello World!\");");
		helloWorldSource.append("    return " + randomExitValue + ";");
		helloWorldSource.append("  }");
		helloWorldSource.append("}");

		String className = "HelloWorld";
		JavaFileObject sourceFile = new InMemorySourceFile(className, helloWorldSource.toString());

		assertFalse(testObject.compile(sourceFile).compilationResult);
		verify(mockInMemoryClassLoader, never()).addClass(anyString(), any(InMemoryClassFile.class));
	}
}
