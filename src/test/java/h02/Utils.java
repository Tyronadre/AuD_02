package h02;

import org.junit.jupiter.params.provider.Arguments;
import org.opentest4j.TestAbortedException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Utils {

    /**
     * Invokes the given parameterless method on the given instance and exits tests
     * where this method was called if the invocation failed
     * @param instance   the instance to invoke the given method on
     * @param methodName the name of the method to invoke
     * @param msg        the name of the calling method
     * @throws TestAbortedException if the given method throws any
     * exception (due to a method not working as intended, for example)
     */
    public static void requireTest(Object instance, String methodName, String msg) throws TestAbortedException {
        try {
            Method method = instance.getClass().getMethod(methodName);

            method.setAccessible(true);

            method.invoke(instance);
        } catch (ReflectiveOperationException e) {
            throw new TestAbortedException(msg, e.getCause());
        }
    }

    /**
     * Tries to invoke the given method with the given parameters and throws the actual Throwable
     * that caused the InvocationTargetException
     * @param method   the method to invoke
     * @param instance the instance to invoke the method on
     * @param params   the parameter to invoke the method with
     * @throws Throwable the actual Throwable (Exception)
     */
    public static void getActualException(Method method, Object instance, Object... params) throws Throwable {
        try {
            method.invoke(instance, params);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    /**
     * Returns a 3-element stream of arguments of 0 and two integers between 1 and 999
     * @return the stream
     */
    public static Stream<Arguments> provideRandomIntArguments() {
        return Stream.concat(
                Stream.of(Arguments.of(String.valueOf(0), 0)),
                new Random()
                        .ints(2, 1, 1000)
                        .mapToObj(n -> Arguments.of(String.valueOf(n), n))
        );
    }

    /**
     * Creates a temporary file and performs an action on it
     * @param content the content of the temporary file
     * @param action  the action to perform on the file
     * @throws IOException if the file couldn't be created or it can't be written to or be deleted
     */
    public static void withTempFile(String content, Consumer<Path> action) throws IOException {
        Path path = Files.createTempFile("aud-h02-test-", ".txt");

        Files.writeString(path, content);

        try {
            action.accept(path);
        } finally {
            Files.delete(path);
        }
    }
}
