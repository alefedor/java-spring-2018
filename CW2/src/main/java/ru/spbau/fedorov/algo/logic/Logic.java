package ru.spbau.fedorov.algo.logic;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.spbau.fedorov.algo.Controller;

/**
 * Logic for Pair game
 */
public class Logic {
    private final Board board;

    private Button firstButton = null;
    private boolean blocked = false;
    private int guessed = 0;
    private final int pairNumber;
    private boolean win = false;

    /**
     * Constructs game logic with board
     * @param boardSize
     */
    public Logic(int boardSize) {
        board = new Board(boardSize);
        pairNumber = boardSize * boardSize / 2;
    }

    /**
     * Pushes a specific button
     * @param b button to push
     * @param x first coordinate
     * @param y second coordinate
     * @return return true, if push successful (not blocked)
     */

    public boolean push(@NotNull Button b, int x, int y) {
        if (blocked) {
            return false; // ignore clicks
        }

        b.setText("" + board.get(x, y));
        if (firstButton == null) {
            firstButton = b;
        } else {
            if (firstButton.getText().equals(b.getText())) {
                firstButton = null;
                guessed++;

                if (guessed == pairNumber) {
                    win = true;
                    Controller.onGameEnded();
                }
            } else {

                blocked = true;

                Task<Void> sleeper = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                        return null;
                    }
                };

                sleeper.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent event) {
                        b.setText("");
                        firstButton.setText("");
                        blocked = false;
                        firstButton = null;
                    }
                });
                new Thread(sleeper).start();

            }
        }

        return true;
    }

    /**
     * Releases a specific button
     * @param b button to push
     * @param x first coordinate
     * @param y second coordinate
     * @return return true, if release successful (not blocked)
     */
    public boolean release(@NotNull Button b, int x, int y) {
        if (blocked) {
            return false; // ignore clicks
        }

        if (firstButton != b) {
            return false; // was already matched
        }

        firstButton.setText("");
        firstButton = null;
        return true;
    }

    /**
     * Get number at cell (x, y)
     * @param x first coordinate
     * @param y second coordinate
     * @return number at the cell
     */
    public int get(int x, int y) {
        return board.get(x, y);
    }

    /**
     * Check for game result
     * @return true, if game ended
     */
    public boolean isWin() {
        return win;
    }
}
