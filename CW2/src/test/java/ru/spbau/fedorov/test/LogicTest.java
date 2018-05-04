package ru.spbau.fedorov.test;

import javafx.application.Application;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.junit.Test;
import ru.spbau.fedorov.algo.logic.Logic;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

public class LogicTest {
    private static final int BOARD_SIZE = 2;

    @Test
    public void testPushRelease() {
        Logic logic = new Logic(BOARD_SIZE);
        Button buttons[][] = new Button[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                buttons[i][j] = new Button();
            }
        }

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                assertTrue(logic.push(buttons[i][j], i, j));
                assertTrue(logic.release(buttons[i][j], i, j));
            }
        }

        assertFalse(logic.isWin());
    }

    @Test
    public void testIncorrectMoves() {
        Logic logic = new Logic(BOARD_SIZE);
        Button buttons[][] = new Button[BOARD_SIZE][BOARD_SIZE];

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                buttons[i][j] = new Button();
            }
        }

        int board[][] = new int[BOARD_SIZE][BOARD_SIZE];

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = logic.get(i, j);
            }
        }

        for (int x1 = 0; x1 < BOARD_SIZE; x1++) {
            for (int y1 = 0; y1 < BOARD_SIZE; y1++) {
                for (int x2 = 0; x2 < BOARD_SIZE; x2++) {
                    for (int y2 = 0; y2 < BOARD_SIZE; y2++) {
                        if (board[x1][y1] != board[x2][y2]) {
                            while (!logic.push(buttons[x1][y1], x1, y1)) ;
                            while (!logic.push(buttons[x2][y2], x1, y1)) ;
                        }
                    }
                }
            }
        }

        assertFalse(logic.isWin());
    }

    @Test
    public void testCorrectMoves() {
        Logic logic = new Logic(BOARD_SIZE);
        Button buttons[][] = new Button[BOARD_SIZE][BOARD_SIZE];

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                buttons[i][j] = new Button();
            }
        }

        int board[][] = new int[BOARD_SIZE][BOARD_SIZE];

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = logic.get(i, j);
            }
        }

        for (int x1 = 0; x1 < BOARD_SIZE; x1++) {
            for (int y1 = 0; y1 < BOARD_SIZE; y1++) {
                for (int x2 = 0; x2 < BOARD_SIZE; x2++) {
                    for (int y2 = 0; y2 < BOARD_SIZE; y2++) {
                        if (board[x1][y1] == board[x2][y2]) {
                            while (!logic.push(buttons[x1][y1], x1, y1)) ;
                            while (!logic.push(buttons[x2][y2], x1, y1)) ;
                        }
                    }
                }
            }
        }

        assertTrue(logic.isWin());
    }
}
