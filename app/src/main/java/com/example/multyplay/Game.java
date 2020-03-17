package com.example.multyplay;

public class Game implements Comparable<Game> {

    private String gameName;
    private String genre;
    private int thumbnail;
    private boolean isMultyplayer;

    public Game(){
    }

    public Game(String gameName, String genre, boolean isMultyplayer, int thumbnail) {
        setGameName(gameName);
        setGenre(genre);
        setMultyplayer(isMultyplayer);
        setThumbnail(thumbnail);
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public boolean isMultyplayer() {
        return isMultyplayer;
    }

    public void setMultyplayer(boolean multyplayer) {
        isMultyplayer = multyplayer;
    }

    @Override
    public int compareTo(Game game) {
        if(this.getGameName().equalsIgnoreCase(game.getGameName())) {
            return 0;
        } else {
            return -1;
        }
    }
}
