package org.sgnn7.compiler;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileFilter;

import javax.tools.JavaFileObject.Kind;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class InMemoryClassLoaderTest {
	private InMemoryClassLoader testObject;
	private File testDataDirectory;
	private FileFilter classFileFilter;
	private String testFileName;
	private String testClassName;
	private File testClassFile;
	private File currentDirectory;
	private String testPackageName;

	@Before
	public void setUp() {
		currentDirectory = new File(".");
		testDataDirectory = new File(currentDirectory, "test-data/class-loader-test/");
		testPackageName = getClass().getPackage().getName();
		testFileName = testDataDirectory.listFiles()[0].getName();
		testClassFile = new File(testDataDirectory, testFileName);
		testClassName = testPackageName + "." + testFileName.substring(0, testFileName.indexOf("."));

		classFileFilter = new AbstractFileFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(Kind.CLASS.extension);
			}
		};

		assertEquals(0, currentDirectory.listFiles(classFileFilter).length);

		testObject = new InMemoryClassLoader();
	}

	@After
	public void tearDown() {
		File testFolder = new File(currentDirectory, testPackageName.replace(".", "/"));
		for (File classFile : testFolder.listFiles(classFileFilter)) {
			FileUtils.deleteQuietly(classFile);
		}
	}

	@Test
	public void findClassTriesTheHashMapFirst() throws Exception {
		assertStandardClassLoaderCannotFindClass(testClassName);
		final byte[] loadedClassContent = FileUtils.readFileToByteArray(testClassFile);

		InMemoryClassFile classFileObject = new InMemoryClassFile(testClassName) {
			@Override
			public byte[] getBytes() {
				return loadedClassContent;
			};
		};

		testObject.addClass(testClassName, classFileObject);

		Class<?> clazz = testObject.findClass(testClassName);
		assertNotNull(clazz);
	}

	@Test
	public void findClassDoesNotTryToRedefineClassesTwice() throws Exception {
		assertStandardClassLoaderCannotFindClass(testClassName);
		final byte[] loadedClassContent = FileUtils.readFileToByteArray(testClassFile);

		InMemoryClassFile classFileObject = new InMemoryClassFile(testClassName) {
			@Override
			public byte[] getBytes() {
				return loadedClassContent;
			};
		};

		testObject.addClass(testClassName, classFileObject);

		testObject.findClass(testClassName);
		testObject.findClass(testClassName);
	}

	@Test
	public void findClassTriesFilesystemSecond() throws Exception {
		File folderHierarchy = new File(currentDirectory, testPackageName.replace(".", "/"));
		folderHierarchy.mkdirs();

		assertStandardClassLoaderCannotFindClass(testClassName);

		FileUtils.copyFile(testClassFile, new File(folderHierarchy, testFileName));

		Class<?> clazz = testObject.findClass(testClassName);
		assertNotNull(clazz);
	}

	@Test
	public void ifClassCannotBeFoundAtAllThrowARuntimeExceptionWrappedClassNotFoundException() throws Exception {
		try {
			testObject.findClass(testClassName);
			fail("Should have thrown a runtime exception");
		} catch (RuntimeException e) {
		}
	}

	private void assertStandardClassLoaderCannotFindClass(String className) {
		try {
			testObject.findClass(className);
			fail("Should not be able to find this class in standard ClassLoader");
		} catch (Exception e) {
		}
	}
}
