package ru.spbau.fedorov.tictactoe.bot;

/**
 * bot for tic-tac-toe game
 */
public interface Bot {

    /**
     * id = row * 3 + column
     * @return id of cell to click
     */
    int getMove();
}
