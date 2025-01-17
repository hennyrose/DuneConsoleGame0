package main.java.game;

public class Card {
    private int id;
    private String description;
    private String[] options;
    private int[] penalties;
    private int[] benefits;

    public Card(int id, String description, String[] options, int[] penalties, int[] benefits) {
        this.id = id;
        this.description = description;
        this.options = options;
        this.penalties = penalties;
        this.benefits = benefits;
    }

    public Card(String description, String[] options, int[] penalties, int[] benefits) {
        this.description = description;
        this.options = options;
        this.penalties = penalties;
        this.benefits = benefits;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String[] getOptions() {
        return options;
    }

    public int[] getPenalties() {
        return penalties;
    }

    public int[] getBenefits() {
        return benefits;
    }
}
