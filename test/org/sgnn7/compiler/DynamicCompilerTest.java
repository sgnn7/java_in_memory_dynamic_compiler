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

public class DynamicCompilerTest {
	private DynamicCompiler testObject;
	private InMemoryClassLoader inMemoryClassLoader;

	@Before
	public void setUp() {
		inMemoryClassLoader = new InMemoryClassLoader();
		testObject = new DynamicCompiler(ToolProvider.getSystemJavaCompiler(), inMemoryClassLoader);
	}

	@Test
	public void ifCompilerIsNullThrowRuntimeException() {
		testObject = new DynamicCompiler(null, null);
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
		int randomExitValue = new Random().nextInt(500) + 2;

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
		testObject = new DynamicCompiler(ToolProvider.getSystemJavaCompiler(), mockInMemoryClassLoader);

		StringBuilder helloWorldSource = new StringBuilder();
		int randomExitValue = new Random().nextInt(500);

		helloWorldSource.append("public class HelloWorld {");
		helloWorldSource.append("  public static int main(String args[]) {");
		helloWorldSource.append("    wrongstuffsSystem.out.println(\"Hello World!\");");
		helloWorldSource.append("    return " + randomExitValue + ";");
		helloWorldSource.append("  }");
		helloWorldSource.append("}");

		String className = "HelloWorld";
		JavaFileObject sourceFile = new InMemorySourceFile(className, helloWorldSource.toString());

		CompilationResult compilationResult = testObject.compile(sourceFile);
		assertFalse(compilationResult.isCompilationSuccessful());
		assertEquals("Error: HelloWorld.java:1: package wrongstuffsSystem does not exist\n",
				compilationResult.getErrorMessage());
		verify(mockInMemoryClassLoader, never()).addClass(anyString(), any(InMemoryClassFile.class));
	}
}
