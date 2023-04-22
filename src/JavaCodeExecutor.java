import javax.tools.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Arrays;

public class JavaCodeExecutor {
    private static final String CLASS_NAME = "DynamicClass";

    public static class ExecutionResult {
        private boolean success;
        private String output;
        private ErrorInfo errorInfo;

        public ExecutionResult(boolean success, String output, ErrorInfo errorInfo) {
            this.success = success;
            this.output = output;
            this.errorInfo = errorInfo;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getOutput() {
            return output;
        }

        public ErrorInfo getErrorInfo() {
            return errorInfo;
        }
    }

    public ExecutionResult compileAndRunJavaCode(String javaCode) {
        // Generate the full Java source code with a class and method wrapper
        String fullJavaCode = "public class " + CLASS_NAME + " {\n" +
                "    public static void main(String[] args) {\n" +
                javaCode +
                "    }\n" +
                "}\n";

        // Create an in-memory Java file manager and compiler
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        JavaFileManager fileManager = new InMemoryJavaFileManager(compiler.getStandardFileManager(diagnostics, null, null));

        // Compile the Java code
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null,
                Arrays.asList(new InMemoryJavaFileObject(CLASS_NAME, fullJavaCode)));
        boolean success = task.call();

        // Prepare the result buffer
        ByteArrayOutputStream resultBuffer = new ByteArrayOutputStream();
        PrintStream resultStream = new PrintStream(resultBuffer);

        if (success) {
            // Load the compiled class and invoke the main method
            try {
                byte[] classBytes = ((InMemoryJavaFileManager) fileManager).getClassBytes();
                MemoryClassLoader classLoader = new MemoryClassLoader(classBytes);
                Class<?> dynamicClass = classLoader.loadClass(CLASS_NAME);

                Method mainMethod = dynamicClass.getMethod("main", String[].class);
                PrintStream originalOut = System.out;
                System.setOut(resultStream); // Redirect standard output to capture the result
                mainMethod.invoke(null, (Object) new String[]{});
                System.setOut(originalOut); // Restore standard output

                // Return successful execution result
                return new ExecutionResult(true, resultBuffer.toString(), null);
            } catch (Exception e) {
                // Handle exceptions and capture error information
                ErrorInfo errorInfo = new ErrorInfo(e.getMessage(), -1, e.toString());
                return new ExecutionResult(false, null, errorInfo);
            }
        } else {
            // Handle compilation errors
            String errorMessage = "Compilation failed: " + diagnostics.getDiagnostics();
            return new ExecutionResult(false, null, new ErrorInfo(errorMessage, -1, errorMessage));
        }
    }

    // Additional classes (e.g., MemoryClassLoader, InMemoryJavaFileManager, InMemoryJavaFileObject) go here
}
