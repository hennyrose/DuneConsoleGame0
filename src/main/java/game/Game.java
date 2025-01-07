package main.java.game;

import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class Game {
    private Player player;
    private Player enemy;
    private Card[] deck;
    private static final int NORMAL_SPICE_PRODUCTION = 100;
    private static final int WATER_CONSUMPTION = 50;

    public Game() {
        this.deck = new Card[]{
                new Card(
                        "Напад хробака на харвестер! Цього ходу видобуток прянощів зменшується вдвічі.",
                        new String[]{"Провести ремонт за 500 кредитів", "Використати війська: -50 солдатів", "Не ремонтувати"},
                        new int[]{500, 50, 0},  
                        new int[]{0, 0, -50}    
                ),
                new Card(
                        "Посуха зменшує кількість води у вашому домі.",
                        new String[]{"Імпортувати воду за 300 кредитів", "Перегрупувати війська для економії (-30 війська)", "Проігнорувати"},
                        new int[]{300, 30, 0},
                        new int[]{100, 0, -100}
                ),
                new Card(
                        "Міські безлади вимагать стабільності!",
                        new String[]{"Стабілізувати ситуацію за 400 кредитів", "Використати війська для придушення: -40 солдатів", "Пропустити"},
                        new int[]{400, 40, 0},
                        new int[]{0, 0, -50}
                ),
                new Card(
                        "Несподівана торгова можливість! Ви можете обміняти воду на кредит.",
                        new String[]{"Обміняти воду: -100 води, отримати 200 кредитів", "Відмовитися від угоди"},
                        new int[]{100, 0},
                        new int[]{200, 0}
                ),
                new Card(
                        "Великий землетрус пошкодив ваші запаси!",
                        new String[]{"Відновити запаси за 600 кредитів", "Створити тимчасове резервування: -30 води", "Проігнорувати"},
                        new int[]{600, 30, 0},
                        new int[]{0, 0, -100}
                ),
        };
    }


    public void start() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Ласкаво просимо у гру 'Dune'!");
        player = createPlayer(scanner);
        enemy = createEnemy(player.getHouse());

        System.out.println("Гра починається!");
        System.out.println(player.getName() + " з дому " + player.getHouse().getName()
                + " проти " + enemy.getName() + " з дому " + enemy.getHouse().getName());

        displayResources(player);

        // Game loop: 7 turns
        for (int turn = 1; turn <= 7; turn++) {
            System.out.println("\nХід номер " + turn + " із 7.");

            // Add resources and consume water
            handleTurnResources(turn);

            if (turn <= 6) {
                // Player's Turn
                Card playerCard = drawCard();
                takePlayerTurn(scanner, playerCard);

                // Enemy's Turn
                Card enemyCard = drawCard();
                takeEnemyTurn(enemyCard, turn);
            } else {
                // Special Final Turn
                takeFinalTurn(scanner);
            }

        
            System.out.println("Ресурси після ходу:");
            displayResources(player);
        }

   
        determineWinner();
    }


    private void handleTurnResources(int turn) {
        adjustHouseResources(player.getHouse(), NORMAL_SPICE_PRODUCTION, -WATER_CONSUMPTION);
        adjustHouseResources(enemy.getHouse(), NORMAL_SPICE_PRODUCTION, -WATER_CONSUMPTION);
    }


    private void adjustHouseResources(House house, int spiceDelta, int waterDelta) {
        Map<String, Integer> resources = house.getResources();

        if (spiceDelta > 0 && house.getName().contains("скорочення")) {
            spiceDelta /= 2;
        }
        resources.put("Spices", resources.get("Spices") + spiceDelta);

        resources.put("Water", resources.get("Water") + waterDelta);
    }

    private void takeFinalTurn(Scanner scanner) {
        System.out.println("\nЦе останній хід. Ви можете обрати між обміном ресурсів:");

        System.out.println("1. Обміняти 2 кредити на 1 воду.");
        System.out.println("2. Обміняти 2 прянощі на 1 солдата.");

        int choice = -1;
        while (choice < 1 || choice > 2) {
            System.out.print("Введіть свій вибір (1 або 2): ");
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        tradeCreditsForWater(player);
                        break;
                    case 2:
                        tradeSpicesForTroops(player);
                        break;
                    default:
                        System.out.println("Неправильний вибір, спробуйте знову.");
                }
            } else {
                System.out.println("Будь ласка, введіть число.");
                scanner.next();
            }
        }
    }

    private void tradeCreditsForWater(Player player) {
        Map<String, Integer> resources = player.getHouse().getResources();
        int credits = resources.get("Credits");
        int waterToAdd = credits / 2;

        resources.put("Credits", credits % 2);
        resources.put("Water", resources.get("Water") + waterToAdd);

        System.out.println("Ви обміняли кредити на " + waterToAdd + " води.");
    }

    private void tradeSpicesForTroops(Player player) {
        Map<String, Integer> resources = player.getHouse().getResources();
        int spices = resources.get("Spices");
        int troopsToAdd = spices / 2;

        resources.put("Spices", spices % 2); // Update remaining spices
        resources.put("Troops", resources.get("Troops") + troopsToAdd);

        System.out.println("Ви обміняли прянощі на " + troopsToAdd + " солдатів.");
    }

    // Create player
    private Player createPlayer(Scanner scanner) {
        String playerName;

        // Loop to get and confirm the player's name
        while (true) {
            System.out.print("Введіть своє ім'я: ");
            playerName = scanner.nextLine().trim();

            boolean confirmed = false;

            while (!confirmed) {
                System.out.print("Ви хочете підтвердити ім'я " + playerName + "? (Y/N): ");
                String confirmName = scanner.nextLine().trim().toUpperCase();

                if (confirmName.equals("Y")) {
                    confirmed = true;  // Confirmed, exit inner loop
                } else if (confirmName.equals("N")) {
                    System.out.println("Будь ласка, введіть нове ім'я.");
                    break;  // Exit inner loop to enter a new name
                } else {
                    System.out.println("Будь ласка, введіть Y або N.");
                }
            }

   
            if (confirmed) {
                break;
            }
        }

        // Choose a house
        House selectedHouse = selectHouse(scanner);

        // Create and return the player
        return new Player(playerName, selectedHouse);
    }

    // Select house for player
    private House selectHouse(Scanner scanner) {
        House[] allHouses = House.getAllHouses();

        while (true) {
            System.out.println("Оберіть дім, ввівши номер:");
            for (int i = 0; i < allHouses.length; i++) {
                System.out.println((i + 1) + ". " + allHouses[i].getName() + " - " + allHouses[i].getAbility());
            }

            int houseChoice = -1;

            while (houseChoice < 1 || houseChoice > allHouses.length) {
                System.out.print("Введіть номер дому (1-" + allHouses.length + "): ");
                if (scanner.hasNextInt()) {
                    houseChoice = scanner.nextInt();
                    if (houseChoice < 1 || houseChoice > allHouses.length) {
                        System.out.println("Невірний номер дому, спробуйте ще раз.");
                    }
                } else {
                    System.out.println("Будь ласка, введіть коректне число.");
                    scanner.next(); // Clear invalid input
                }
            }

            House selectedHouse = allHouses[houseChoice - 1];
            scanner.nextLine(); // To prevent input issues after nextInt()

            System.out.print("Ви обрали " + selectedHouse.getName() + ". Підтвердити вибір? (Y/N): ");
            String confirmHouse = scanner.nextLine().trim().toUpperCase();

            if (confirmHouse.equals("Y")) {
                return selectedHouse; // Confirmed
            } else if (confirmHouse.equals("N")) {
                System.out.println("Оберіть дім знову."); // Repeat house selection
            } else {
                System.out.println("Будь ласка, введіть Y або N.");
            }
        }
    }

    // Create enemy
    private Player createEnemy(House playerHouse) {
        House[] allHouses = House.getAllHouses();
        Random random = new Random();

        House enemyHouse;
        while (true) {
            enemyHouse = allHouses[random.nextInt(allHouses.length)];
            if (!enemyHouse.getName().equals(playerHouse.getName())) {
                break; // Ensure enemy's house is different from the player's house
            }
        }

        // Return the enemy player
        return new Player("Ворог", enemyHouse);
    }

    // Draw a random card for a given turn
    private Card drawCard() {
        Random random = new Random();
        return this.deck[random.nextInt(deck.length)];
    }

    // Handle player's turn
    private void takePlayerTurn(Scanner scanner, Card card) {
        System.out.println("\nВам випала карта: " + card.getDescription());
        for (int i = 0; i < card.getOptions().length; i++) {
            System.out.println(i + ": " + card.getOptions()[i]);
        }

        int choice;
        while (true) {
            System.out.print("Ваш вибір (0-" + (card.getOptions().length - 1) + "): ");
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                if (choice >= 0 && choice < card.getOptions().length) {
                    break;
                }
            }
            System.out.println("Некоректний вибір. Спробуйте ще раз.");
        }
        applyCardEffect(player, card, choice);
    }

    // Handle enemy's turn
    private void takeEnemyTurn(Card card, int turn) {
        System.out.println("\nХід ворога з дому " + enemy.getHouse().getName()
                + " (" + turn + "/11) - Карта: " + card.getDescription());

        int choice = determineEnemyChoice(card);
        System.out.println("Ворог обрав: " + card.getOptions()[choice]);
        applyCardEffect(enemy, card, choice);
    }

    // Determine enemy's decision with AI logic
    private int determineEnemyChoice(Card card) {
        // Enemy AI decision-making
        Random random = new Random();
        return random.nextInt(card.getOptions().length); // Random choice
    }

    // Apply card effects based on choice
    private void applyCardEffect(Player player, Card card, int choice) {
        Map<String, Integer> resources = player.getHouse().getResources();

        // Apply penalties (deductions)
        if (card.getPenalties()[choice] > 0) {
            resources.put("Credits", resources.get("Credits") - card.getPenalties()[choice]);
        }

        // Apply benefits (additions)
        if (card.getBenefits()[choice] > 0) {
            resources.put("Water", resources.get("Water") + card.getBenefits()[choice]);
        }
    }

    // Display player's resources
    private void displayResources(Player player) {
        System.out.println("Ресурси гравця " + player.getName() + ":");
        Map<String, Integer> resources = player.getHouse().getResources();
        for (Map.Entry<String, Integer> resource : resources.entrySet()) {
            System.out.println(resource.getKey() + ": " + resource.getValue());
        }
    }

    // Determine the winner
    private void determineWinner() {
        int playerScore = evaluateScore(player);
        int enemyScore = evaluateScore(enemy);

        if (playerScore > enemyScore) {
            System.out.println("Вітаємо, " + player.getName() + "! Ви перемогли!");
        } else {
            System.out.println("На жаль, ворог з дому " + enemy.getHouse().getName() + " здобув перемогу.");
        }
    }

    // Calculate score based on water and troops
    private int evaluateScore(Player player) {
        Map<String, Integer> resources = player.getHouse().getResources();
        return resources.get("Troops") + resources.get("Water");
    }
}
