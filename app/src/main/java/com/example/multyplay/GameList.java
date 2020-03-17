package com.example.multyplay;

import java.util.ArrayList;
import java.util.Comparator;

public class GameList implements Comparator<Game> {

    private ArrayList<Game> games;

    public GameList() {
        setGames(new ArrayList<Game>());
    }

    public GameList(ArrayList<Game> games) {
        setGames(games);
    }

    public int Size() {
        return this.getGames().size();
    }

    public void addGame(Game newGame) {
        ArrayList<Game> newList = getGames();
        if(!getGames().contains(newGame))
            newList.add(newGame);
        setGames(newList);
    }

    public ArrayList<Game> getGames() {
        return games;
    }

    public void setGames(ArrayList<Game> games) {
        this.games = games;
    }

    @Override
    public int compare(Game game, Game t1) {
        return game.compareTo(t1);
    }
}
