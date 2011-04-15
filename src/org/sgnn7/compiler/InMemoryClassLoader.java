package org.sgnn7.compiler;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.tools.JavaFileObject.Kind;

import org.apache.commons.io.FileUtils;

public class InMemoryClassLoader extends ClassLoader {
	protected final Map<String, InMemoryClassFile> classMap = new HashMap<String, InMemoryClassFile>();
	protected final Map<String, Class<?>> definedClassMap = new HashMap<String, Class<?>>();

	public InMemoryClassLoader() {
		super(InMemoryClassLoader.class.getClassLoader());
	}

	@Override
	public Class<?> findClass(String name) {
		System.out.println("Loading " + name + Kind.CLASS.extension);
		Class<?> clazz = null;
		byte[] byteContent = null;
		if (classMap.containsKey(name)) {
			System.out.println("->Found in RAM");
			byteContent = classMap.get(name).getBytes();
		} else {
			System.out.println("->Trying FS");
			try {
				byteContent = FileUtils.readFileToByteArray(new File(name.replace(".", "/") + Kind.CLASS.extension));
			} catch (IOException e) {
				System.err.println("Cannot find class " + name);
				throw new RuntimeException(new ClassNotFoundException());
			}
		}

		// XXX Can be improved by checking this map first
		if (definedClassMap.containsKey(name)) {
			clazz = definedClassMap.get(name);
		} else {
			clazz = defineClass(name, byteContent, 0, byteContent.length);
			definedClassMap.put(name, clazz);
		}

		return clazz;
	}

	public void addClass(String name, InMemoryClassFile classCode) {
		classMap.put(name, classCode);
	}
}
