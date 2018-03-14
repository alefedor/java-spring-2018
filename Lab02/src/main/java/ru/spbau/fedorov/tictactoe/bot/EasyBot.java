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

    /**
     * Constructs a bot.
     * @param model game model for which the bot is created
     */
    public EasyBot(@NotNull Model model) {
        this.model = model;
    }

    @Override
    public int getMove() {
        int move = ((rnd.nextInt() % 9) + 9) % 9;
        while (!model.canMakeMove(move)) {
            move = ((rnd.nextInt() % 9) + 9) % 9;
        }
        return move;
    }
}
