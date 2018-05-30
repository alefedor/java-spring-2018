package ru.spbau.fedorov.tictactoe.bot;

import org.jetbrains.annotations.NotNull;
import ru.spbau.fedorov.tictactoe.logic.Model;

import java.util.Random;

/**
 * bot for tic-tac-toe with random moves
 */
public class EasyBot implements Bot {

    private final Model model;
    private Random rnd = new Random();
    private final int boardSize;

    /**
     * Constructs a bot.
     * @param model game model for which the bot is created
     */
    public EasyBot(@NotNull Model model) {
        this.model = model;
        boardSize = model.getBoardSize();
    }

    @Override
    public int getMove() {
        int maxMoveNum = boardSize * boardSize;

        int move = ((rnd.nextInt() % maxMoveNum) + maxMoveNum) % maxMoveNum;
        while (!model.canMakeMove(move)) {
            move = ((rnd.nextInt() % maxMoveNum) + maxMoveNum) % maxMoveNum;
        }
        return move;
    }
}
