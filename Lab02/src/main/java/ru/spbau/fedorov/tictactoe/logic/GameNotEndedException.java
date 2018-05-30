package ru.spbau.fedorov.tictactoe.logic;

/**
 * Exception which is returned when tried to get final game result while game not ended.
 */
public class GameNotEndedException extends Exception {
    /**
     * Creates GameNotEndedException
     * @param message string to be stored in Exception
     */
    public GameNotEndedException(String message) {
        super(message);
    }
}
