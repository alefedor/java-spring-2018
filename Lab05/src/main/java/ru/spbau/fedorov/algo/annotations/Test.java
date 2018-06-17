package ru.spbau.fedorov.algo.annotations;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for test that should run by XUnit.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Test {
    /**
     * For case when some exception is expected.
     * When set to non Default class then test will fail if no exception is thrown.
     * @return class for exception expected
     */
    @NotNull Class<? extends Throwable> expected() default Default.class;

    /**
     * Test is ignored if ignore is set to non empty string. Empty string by default.
     * @return ignore reason or empty string
     */
    @NotNull String ignore() default "";

    class Default extends Throwable {}
}
