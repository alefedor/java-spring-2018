package ru.spbau.fedorov.tictactoe.logic;

import org.junit.Test;
import ru.spbau.fedorov.tictactoe.statistics.GameInfo;

import static org.junit.Assert.assertEquals;

public class ModelTest {
    @Test
    public void testCreate() {
        Model model = new Model();
    }

    @Test
    public void testCorrectMoves() {
        Model model = new Model();
        assertEquals(true, model.makeMove(0, 0, true));
        assertEquals(true, model.makeMove(1, 0, false));
        assertEquals(true, model.makeMove(0, 1, true));
        assertEquals(true, model.makeMove(1, 1, false));
        assertEquals(false, model.gameEnded());
        assertEquals(null, model.getGameResult());
    }

    @Test
    public void testIncorrectMoves() {
        Model model = new Model();
        assertEquals(true, model.makeMove(0, 0, true));
        assertEquals(true, model.makeMove(1, 0, false));
        assertEquals(false, model.makeMove(0, 0, true));
        assertEquals(false, model.makeMove(1, 0, true));
        assertEquals(false, model.gameEnded());
        assertEquals(null, model.getGameResult());
    }

    @Test
    public void testWin() {
        Model model = new Model();
        model.makeMove(0, 0, true);
        model.makeMove(1, 0, false);
        model.makeMove(0, 1, true);
        model.makeMove(1, 1, false);
        model.makeMove(0, 2, true);
        assertEquals(true, model.gameEnded());
        assertEquals(GameInfo.GameResult.Win, model.getGameResult());
    }

    @Test
    public void testLose() {
        Model model = new Model();
        model.makeMove(1, 0, true);
        model.makeMove(0, 0, false);
        model.makeMove(2, 1, true);
        model.makeMove(1, 1, false);
        model.makeMove(0, 2, true);
        model.makeMove(2, 2, false);
        assertEquals(true, model.gameEnded());
        assertEquals(GameInfo.GameResult.Lose, model.getGameResult());
    }

    /**
     * OXX
     * XOO
     * OXX
     */
    @Test
    public void testDraw() {
        Model model = new Model();
        model.makeMove(1, 0, true);
        model.makeMove(0, 0, false);
        model.makeMove(0, 1, true);
        model.makeMove(1, 1, false);
        model.makeMove(0, 2, true);
        model.makeMove(1, 2, false);
        model.makeMove(2, 1, true);
        model.makeMove(2, 0, false);

        assertEquals(false, model.gameEnded());

        model.makeMove(2, 2, true);

        assertEquals(true, model.gameEnded());
        assertEquals(GameInfo.GameResult.Draw, model.getGameResult());
    }

    @Test
    public void testCanMakeMove() {
        Model model = new Model();

        assertEquals(true, model.canMakeMove(3));

        model.makeMove(1, 0, true);

        assertEquals(true, model.canMakeMove(0));

        model.makeMove(0, 0, false);

        assertEquals(true, model.canMakeMove(1));

        model.makeMove(0, 1, true);


        assertEquals(true, model.canMakeMove(4));

        model.makeMove(1, 1, false);

        assertEquals(true, model.canMakeMove(2));

        model.makeMove(0, 2, true);

        assertEquals(true, model.canMakeMove(5));

        model.makeMove(1, 2, false);

        assertEquals(true, model.canMakeMove(7));

        model.makeMove(2, 1, true);

        assertEquals(true, model.canMakeMove(6));

        model.makeMove(2, 0, false);

        assertEquals(true, model.canMakeMove(8));

        model.makeMove(2, 2, true);
    }
}
