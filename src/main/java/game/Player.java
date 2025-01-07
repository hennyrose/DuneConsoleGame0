package main.java.game;

public class Player {
    private String name;
    private House house;

    public Player(String name, House house) {
        this.name = name;
        this.house = house;
    }

    public String getName() {
        return name;
    }

    public House getHouse() {
        return house;
    }
}
