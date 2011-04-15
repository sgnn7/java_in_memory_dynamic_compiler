package org.sgnn7.compiler.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.Method;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.sgnn7.compiler.CompilationResult;
import org.sgnn7.compiler.DynamicCompiler;
import org.sgnn7.compiler.InMemoryClassLoader;
import org.sgnn7.compiler.InMemorySourceFile;

import com.sun.java.swing.plaf.nimbus.LoweredBorder;

public class BasicGUI {
	private static final String CLASS_NAME = "HelloWorld";

	private static final int BUTTON_WIDTH = 100;

	private final JavaCompiler javaToolchainCompiler = ToolProvider.getSystemJavaCompiler();
	private InMemoryClassLoader classLoader;

	private final JTextArea codeTextArea;
	private final JTextArea statusTextArea;

	public BasicGUI() {
		JFrame mainFrame = new JFrame("Dynamic Compiler");
		JPanel mainPanel = new JPanel(new GridBagLayout());

		mainFrame.setBounds(new Rectangle(600, 400));
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		codeTextArea = new JTextArea();
		codeTextArea.setBorder(new LoweredBorder());

		JButton compileButton = new JButton("Compile");
		compileButton.setPreferredSize(new Dimension(BUTTON_WIDTH, (int) compileButton.getPreferredSize().getHeight()));
		compileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				compileCode();
			}
		});

		JButton runButton = new JButton("Run");
		runButton.setPreferredSize(new Dimension(BUTTON_WIDTH, (int) compileButton.getPreferredSize().getHeight()));
		runButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					runCode();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});

		statusTextArea = new JTextArea();
		statusTextArea.setBorder(new LoweredBorder());

		mainPanel.add(codeTextArea, getCellConstraints(0, 0, 1, 2, GridBagConstraints.BOTH, 10));
		mainPanel.add(compileButton, getCellConstraints(1, 0, 1, 1, GridBagConstraints.NONE));
		mainPanel.add(runButton, getCellConstraints(1, 1, 1, 1, GridBagConstraints.NONE));
		mainPanel.add(statusTextArea, getCellConstraints(0, 2, 2, 1, GridBagConstraints.BOTH, 10));

		mainFrame.add(mainPanel);

		mainFrame.setVisible(true);
		mainFrame.doLayout();

		populateDefaultCode();
	}

	private void compileCode() {
		classLoader = new InMemoryClassLoader();
		DynamicCompiler compiler = new DynamicCompiler(javaToolchainCompiler, classLoader);

		CompilationResult compilationResult = new CompilationResult(false, null, "Unable to compile source");
		try {
			InMemorySourceFile sourceFile = new InMemorySourceFile(CLASS_NAME, codeTextArea.getText());
			compilationResult = compiler.compile(sourceFile);
		} catch (Exception e) {
			appendStatus(e.getMessage());
			e.printStackTrace();
		} finally {
			appendStatus("Compilation worked: " + compilationResult.isCompilationSuccessful());

			if (!compilationResult.isCompilationSuccessful()) {
				appendStatus(compilationResult.getErrorMessage());
			}
		}
	}

	private void runCode() throws IOException {
		try {
			Class<?> clazz = classLoader.findClass(CLASS_NAME);
			Method mainMethod;
			mainMethod = clazz.getDeclaredMethod("main", new Class[] { String[].class });
			Object returnedObject = mainMethod.invoke(null, new Object[] { null });
			int returnValue = Integer.class.cast(returnedObject);
			appendStatus("Return value is " + returnValue);
		} catch (Exception e) {
			appendStatus("Error encountered: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void appendStatus(String message) {
		statusTextArea.setText(statusTextArea.getText() + "\n" + message);
	}

	private void populateDefaultCode() {
		StringBuilder helloWorldSource = new StringBuilder();
		helloWorldSource.append("public class HelloWorld {\n");
		helloWorldSource.append("  public static int main(String args[]) {\n");
		helloWorldSource.append("    System.out.println(\"Hello World!\");\n");
		helloWorldSource.append("    return " + 5 + ";\n");
		helloWorldSource.append("  }\n");
		helloWorldSource.append("}\n");
		codeTextArea.setText(helloWorldSource.toString());
	}

	private GridBagConstraints getCellConstraints(int x, int y, int width, int height, int fillType) {
		return getCellConstraints(x, y, width, height, fillType, 1);
	}

	private GridBagConstraints getCellConstraints(int x, int y, int width, int height, int fillType, int weightx) {
		GridBagConstraints gridConstraints = new GridBagConstraints();
		gridConstraints.gridwidth = width;
		gridConstraints.gridheight = height;
		gridConstraints.gridx = x;
		gridConstraints.gridy = y;
		gridConstraints.weightx = weightx;
		gridConstraints.weighty = 1;
		gridConstraints.fill = fillType;
		return gridConstraints;
	}
}
