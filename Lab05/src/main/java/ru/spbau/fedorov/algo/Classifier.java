package ru.spbau.fedorov.algo;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.spbau.fedorov.algo.annotations.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * XUnit classifier to divide methods in groups according to annotations.
 */
public class Classifier {
    @Getter private final List<Method> after = new ArrayList<>();
    @Getter private final List<Method> before = new ArrayList<>();
    @Getter private final List<Method> afterClass = new ArrayList<>();
    @Getter private final List<Method> beforeClass = new ArrayList<>();
    @Getter private final List<Method> tests = new ArrayList<>();

    /**
     * Constructor for classifier.
     * @param c class from which methods are classified
     * @throws ClassifierException when cannot classify a method
     */
    public Classifier(@NotNull Class<?> c) throws ClassifierException {
        for (Method method : c.getDeclaredMethods()) {
            boolean shouldBeTested = classify(method);

            if (shouldBeTested && method.getParameterCount() != 0) {
                throw new ClassifierException("Method should have no arguments");
            }

            if (shouldBeTested && !method.getReturnType().equals(Void.TYPE)) {
                throw new ClassifierException("Method should have no return value");
            }
        }
    }

    /**
     * Parse method annotations and add to one of groups if necessary.
     * @param method method to classify
     * @return true, if it is method, that can possibly be runned by XUnit while testing
     * @throws ClassifierException when cannot classify a method
     */
    private boolean classify(@NotNull Method method) throws ClassifierException {
        Annotation annotations[] = method.getAnnotations();
        Set<Class<?>> has = new HashSet<>();
        Set<Class<?>> mayBe = new HashSet<>();
        mayBe.add(After.class);
        mayBe.add(AfterClass.class);
        mayBe.add(Before.class);
        mayBe.add(BeforeClass.class);
        mayBe.add(Test.class);


        for (Annotation annotation : annotations) {
            has.add(annotation.annotationType());
        }

        has.retainAll(mayBe);

        if (has.size() == 0) {
            return false;
        }

        if (has.size() > 1) {
            throw new ClassifierException("Method should have no more than one of supported annotations");
        }

        method.setAccessible(true);

        if (has.contains(After.class)) {
            after.add(method);
        } else if (has.contains(AfterClass.class)) {
            afterClass.add(method);
        } else if (has.contains(Before.class)) {
            before.add(method);
        } else if (has.contains(BeforeClass.class)) {
            beforeClass.add(method);
        } else {
            tests.add(method);
        }

        return true;
    }

    /**
     * Exception for fails while classifiing methods.
     */
    public class ClassifierException extends Exception {
        public ClassifierException(String message) {
            super(message);
        }
    }
}
