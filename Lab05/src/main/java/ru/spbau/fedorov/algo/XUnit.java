package ru.spbau.fedorov.algo;

import org.jetbrains.annotations.NotNull;
import ru.spbau.fedorov.algo.annotations.Test;

import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;

/**
 * Class for testing with support of various annotations.
 */
public class XUnit {
    private static final String USAGE = "Usage: <path to project root : String> <name of the class>";

    /**
     * Main for XUnit application
     * @param args arguments to the application
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String directory = args[0];
        String name = args[1];

        ClassLoader loader = null;

        try {
            loader = new URLClassLoader(new URL[]{Paths.get(directory).toUri().toURL()});
        } catch (MalformedURLException e) {
            System.out.println("No such directory");
            System.out.println(USAGE);
            System.exit(2);
        }

        Class<?> c = null;
        try {
            c = loader.loadClass(name);
        } catch (ClassNotFoundException e) {
            System.out.println("No such class in the directory");
            System.out.println(USAGE);
            System.exit(3);
        }

        try {
            runTests(c, System.out);
        } catch (InvocationException | Classifier.ClassifierException e) {
            System.out.println(e.getMessage());
            System.exit(4);
        }
    }

    /**
     * Runs all tests in a class according to annotations.
     * @param c class which is tested
     * @param out stream to print result
     * @throws Classifier.ClassifierException when fail on classification
     * @throws InvocationException when fail on invocation methods
     */
    public static void runTests(@NotNull Class<?> c, @NotNull PrintStream out) throws Classifier.ClassifierException, InvocationException {
        Classifier classifier = new Classifier(c);

        Object instance = getInstance(c);
        int runned = 0;
        int ignored = 0;
        int failed = 0;

        try {

            for (Method method : classifier.getBeforeClass()) {
                method.invoke(instance);
            }

            for (Method method : classifier.getTests()) {
                if (method.getAnnotation(Test.class).ignore().equals("")) {
                    runned++;

                    for (Method before : classifier.getBefore()) {
                        before.invoke(instance);
                    }

                    Class<?> expected = method.getAnnotation(Test.class).expected();
                    Throwable exception = null;

                    long startTime = System.currentTimeMillis();

                    try {
                        method.invoke(instance);
                    } catch (Exception e) {
                        exception = e.getCause();
                    }

                    long endTime = System.currentTimeMillis();

                    out.print("Test " + method.getName() + " finished with result ");

                    if (exception == null) {
                        if (expected.equals(Test.Default.class)) {
                            out.print("'OK'");
                        } else {
                            failed++;
                            out.print("'no exception was thrown, while " + expected.getName() + " was expected'");
                        }
                    } else {
                        if (exception.getClass().equals(expected)) {
                            out.print("'OK'");
                        } else {
                            failed++;
                            out.print("'");
                            out.print(exception.getClass().getName() + " was thrown, while ");
                            out.print((expected.equals(Test.Default.class) ? "no exception" : expected.getName()));
                            out.print(" was expected");
                            out.print("'");
                        }
                    }

                    out.println(" in time " + (endTime - startTime) / 1000.0);

                    for (Method after : classifier.getAfter()) {
                        after.invoke(instance);
                    }
                } else {
                    ignored++;
                    out.println("Test " + method.getName() + " was ignored due to reason: " + method.getAnnotation(Test.class).ignore());
                }
            }

            for (Method method : classifier.getAfterClass()) {
                method.invoke(instance);
            }

        } catch (Exception e) {
            InvocationException exception = new InvocationException("Can't invoke method in the class");
            exception.addSuppressed(e);
            throw exception;
        }

        out.println("Tests passed: " + (runned - failed) + "/" + runned);
        out.println("Tests total: " + (runned + ignored) + "(" + ignored + " was(were) ignored)");
    }

    /**
     * Get an object of the class using default constructor
     * @param c class of the object
     * @return object of necessary class
     * @throws InvocationException when fail while invoking default constructor
     */
    private static Object getInstance(@NotNull Class<?> c) throws InvocationException {
        try {
            Constructor<?> constructor = c.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            InvocationException exception = new InvocationException("Can't construct an instance of the class");
            exception.addSuppressed(e);
            throw exception;
        }
    }

    /**
     * Exception for fails while invocation of methods.
     */
    public static final class InvocationException extends Exception {
        public InvocationException(String message) {
            super(message);
        }
    }
}
