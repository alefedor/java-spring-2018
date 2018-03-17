package ru.spbau.fedorov.algo;

/**
 * Exception which is returned when Runtime error was thrown on execution of task
 * thread during execution.
 */
public class LightExecutionException extends Exception{
    /**
     * Creates LightExecutionException
     * @param message string to be stored in Exception
     */
    public LightExecutionException(String message) {
        super(message);
    }

    /**
     * Creates LightExecutionException
     * @param throwable exception that occured in thread
     */
    public LightExecutionException(Throwable throwable) {
        super(throwable);
    }
}
