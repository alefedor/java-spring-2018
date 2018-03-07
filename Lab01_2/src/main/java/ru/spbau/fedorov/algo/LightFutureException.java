package ru.spbau.fedorov.algo;

/**
 * Exception which is returned when Runtime error was thrown on execution of task
 * thread during execution.
 */
public class LightFutureException extends Exception{
    /**
     * Creates LightFutureException
     * @param message string to be stored in Exception
     */
    public LightFutureException(String message) {
        super(message);
    }

    /**
     * Creates LightFutureException
     * @param throwable exception that occured in thread
     */
    public LightFutureException(Throwable throwable) {
        super(throwable);
    }
}
