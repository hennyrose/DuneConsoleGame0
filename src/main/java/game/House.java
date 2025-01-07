package main.java.game;

import java.util.HashMap;
import java.util.Map;

public class House {
    private String name;
    private String ability;
    private Map<String, Integer> resources; // Resources for the house

    // Список всіх домів
    private static final House[] allHouses = {
            new House("Atreides", "More Water", 1000, 500, 500, 2000),
            new House("Harkonnens", "More Troops", 1000, 500, 2000, 500),
            new House("Corrino", "More Credits", 2000, 500, 500, 1000)
    };

    public House(String name, String ability, int credits, int spices, int troops, int water) {
        this.name = name;
        this.ability = ability;
        this.resources = new HashMap<>();
        this.resources.put("Credits", credits);
        this.resources.put("Spices", spices);
        this.resources.put("Troops", troops);
        this.resources.put("Water", water);
    }

    public String getName() {
        return name;
    }

    public String getAbility() {
        return ability;
    }

    public Map<String, Integer> getResources() {
        return resources;
    }

    // Повертає всі доступні дома
    public static House[] getAllHouses() {
        return allHouses;
    }
}
