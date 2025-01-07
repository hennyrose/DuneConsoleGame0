package main.java.game;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.scene.text.TextFlow;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List; // Імпорт для списків
import java.util.ArrayList; // Імпорт для реалізації списків
import java.util.Map;
import java.util.Random;
import java.io.InputStream; // Імпорт для роботи з InputStream

public class GameGUI extends Application {

    private Player player;
    private Player enemy;
    private TextFlow gameConsole;
    private VBox actionButtons;
    private VBox resourcePanel; // Panel to display player's resources
    private int playerSpiceProduction = 160; // Видобуток спецій для гравця
    private int playerWaterConsumption = 30; // Витрати води для гравця

    private int enemySpiceProduction = 150; // Видобуток спецій для ворога
    private int enemyWaterConsumption = 25; // Витрати води для ворога

    private Card[] deck; // Deck of cards
    private int turn; // Current turn counter
    private static final int MAX_TURNS = 7;

    private List<Card> availableCards; // Список усіх карт
    private List<Card> playerUsedCards = new ArrayList<>(); // Використані гравцем карти
    private List<Card> enemyUsedCards = new ArrayList<>(); // Використані ворогом карти

    private List<Card> playerDeck; // Колода карт для гравця
    private List<Card> enemyDeck;  // Колода карт для ворога

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Dune Adventure Game");

        showInstructionWindow(primaryStage);
    }

    private void showInstructionWindow(Stage primaryStage) {
        // Створюємо VBox для вмісту інструкції
        VBox instructionBox = new VBox();
        instructionBox.setPadding(new Insets(10));
        instructionBox.setSpacing(15);
        instructionBox.setAlignment(Pos.CENTER);
        instructionBox.setStyle("-fx-background-color: #F5DEB3;"); // Пісочний стиль

        // Логотип гри
        ImageView logoView = null;
        try {
            // Завантажуємо логотип
            Image logoImage = new Image(getClass().getResourceAsStream("/logo.png")); // Завантажуємо зображення
            logoView = new ImageView(logoImage);

            // Налаштування розмірів логотипу
            logoView.setFitWidth(300); // Встановлюємо ширину
            logoView.setPreserveRatio(true); // Зберігаємо пропорції зображення
        } catch (Exception e) {
            System.err.println("Could not load logo image: " + e.getMessage());
        }

        // Текст привітання
        Label titleLabel = new Label("Welcome to Dune Adventure Game!");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Інструкції до гри
        Label instructions = new Label(
                "Rules of the game:\n" +
                        "- Choose your house to begin your journey.\n" +
                        "- Manage resources like credits, spices, water, and troops wisely to ensure survival.\n" +
                        "- Handle events strategically by selecting actions based on available resources.\n" +
                        "- If you run out of any resource (Credits, Spices, Water, or Troops), the game will restart automatically.\n" +
                        "- Balance between production, defense, and resource management to gain an edge over your enemy.\n" +
                        "- Compete against the enemy by maximizing your resource coefficient across 7 turns.\n" +
                        "- The final winner is determined based on the combined coefficient of all resources: \n" +
                        "   (Credits × 0.25) + (Spices × 0.5) + (Troops × 0.75) + (Water × 1.0).\n" +
                        "- Explore new strategies like trading resources or countering events to turn the tide in your favor.\n" +
                        "- Good luck, and may your house claim ultimate victory!"
        );
        instructions.setStyle("-fx-font-size: 14px; -fx-text-alignment: center;");
        instructions.setWrapText(true);

        // Кнопка для початку гри
        Button startGameButton = new Button("Start Game");
        startGameButton.setStyle("-fx-font-size: 14px;");
        startGameButton.setOnAction(e -> {
            // Завантажуємо головну сцену гри
            primaryStage.setScene(createGameScene(primaryStage));
            initializeGame();
        });

        // Додаємо зображення, текст та кнопку у VBox
        if (logoView != null) {
            instructionBox.getChildren().add(logoView);
        }
        instructionBox.getChildren().addAll(titleLabel, instructions, startGameButton);

        // Створюємо сцену інструкцій та показуємо її
        Scene instructionScene = new Scene(instructionBox, 800, 600);
        primaryStage.setScene(instructionScene);
        primaryStage.show();
    }

    private Scene createGameScene(Stage primaryStage) {
        // Root layout
        BorderPane root = new BorderPane();

        // Встановлюємо світло-пісочний фон для головного компонента
        root.setStyle("-fx-background-color: #F5DEB3;"); // Пісочний колір

        // Create the console
        gameConsole = new TextFlow();
        gameConsole.setStyle("-fx-background-color: #F7E7CE; " // Кремовий фон
                + "-fx-padding: 10;");
        ScrollPane scrollPane = new ScrollPane(gameConsole);  // Додаємо скролл, якщо потрібно
        scrollPane.setFitToWidth(true);
        root.setCenter(scrollPane);

        // Create the action buttons
        actionButtons = new VBox();
        actionButtons.setPadding(new Insets(10));
        actionButtons.setSpacing(10);
        actionButtons.setAlignment(Pos.CENTER);
        actionButtons.setStyle("-fx-background-color: #F5DEB3;"); // Теж пісочний фон секції
        root.setBottom(actionButtons);

        // Create the player's resource panel
        resourcePanel = new VBox();
        resourcePanel.setPadding(new Insets(10));
        resourcePanel.setSpacing(5);
        resourcePanel.setAlignment(Pos.TOP_RIGHT);
        root.setRight(resourcePanel);

        // Initialize the game
        initializeGame();

        // Create and return the scene
        return new Scene(root, 800, 600);
    }

    private void initializeGame() {
        turn = 1; // Скидаємо лічильник ходів
        //printToConsoleBold("Welcome to the game 'Dune Adventure'!");

        askForPlayerName(); // Починаємо зі вводу імені
        initializeDeck();   // Гарантовано створюємо колоду карт
    }

    private void initializeDeck() {
        availableCards = new ArrayList<>();

        availableCards.add(new Card(1, "Worm attacks the harvester! Repairing costs 500 credits, using troops reduces 50 troops, but ignoring it halves spice production, affecting future credit gains.",
                new String[]{"Repair for 500 credits", "Use troops (-50)", "Ignore"},
                new int[]{500, 50, 0}, new int[]{0, 0, -50}));
        availableCards.add(new Card(2, "Drought reduces your house's water reserves. Importing water costs 300 credits, reorganizing troops loses 30, and ignoring increases troop losses and causes water shortages.",
                new String[]{"Import water for 300 credits", "Reorganize troops (-30)", "Ignore"},
                new int[]{300, 30, 0}, new int[]{100, 0, -100}));

        availableCards.add(new Card(3, "Saboteurs attack! Sending reinforcements costs 20 troops, ignoring causes a morale drop (-30 troops), and countering with spies costs 15 credits.",
                new String[]{"Send reinforcements (-20 troops)", "Ignore (-30 troops)", "Counter with spies (-50 credits)"},
                new int[]{0, 0, 50}, new int[]{-20, -30, 0}));

        availableCards.add(new Card(4, "Market fluctuation affects spice prices. Selling spices gains 200 credits, holding them has no impact, and buying spices reduces credits by 100.",
                new String[]{"Sell spices (+200 credits)", "Hold spices (no change)", "Buy spices (-100 credits)"},
                new int[]{200, 0, 100}, new int[]{0, 0, 0}));

        availableCards.add(new Card(5, "Sandstorm hits your base! Protecting costs 300 credits, relocating troops loses 40 of them, and ignoring impacts production and troops (-50 troops).",
                new String[]{"Protect base (-300 credits)", "Relocate troops (-40 troops)", "Ignore the storm"},
                new int[]{300, 40, 0}, new int[]{50, 0, -50}));

        availableCards.add(new Card(6, "Rebellion among your troops. Increasing wages costs 200 credits, reorganizing loses 20 troops, and ignoring causes massive water shortages (-50 water).",
                new String[]{"Increase wages (-200 credits)", "Reorganize troops (-20 troops)", "Ignore (-50 water)"},
                new int[]{200, 20, 0}, new int[]{0, 0, -50}));

        availableCards.add(new Card(7, "Discovery of a new spice vein! Expanding production costs 200 credits, relocating troops loses 20 of them, and ignoring gives an advantage to the enemy.",
                new String[]{"Expand production (-200 credits)", "Ignore", "Relocate troops (-20 troops)"},
                new int[]{0, 0, 20}, new int[]{-200, 0, 0}));

        // Створюємо окремі колоди для гравця та ворога
        playerDeck = new ArrayList<>(availableCards);
        enemyDeck = new ArrayList<>(availableCards);
    }

    private void askForPlayerName() {
        if (player != null) return; // Уникаємо повторної ініціалізації гравця

        actionButtons.getChildren().clear();

        VBox nameBox = new VBox();
        Label nameLabel = new Label("Enter your name:");
        TextField nameField = new TextField();
        Button submitButton = new Button("Submit");

        submitButton.setOnAction(e -> {
            String playerName = nameField.getText().trim();
            if (playerName.isEmpty()) {
                printToConsole("Please enter a valid name.");
            } else {
                createPlayer(playerName);
            }
        });

        nameBox.getChildren().addAll(nameLabel, nameField, submitButton);
        nameBox.setSpacing(10);
        nameBox.setAlignment(Pos.CENTER);

        actionButtons.getChildren().add(nameBox);
    }

    private void createPlayer(String playerName) {
        actionButtons.getChildren().clear(); // Очищаємо попередню панель дій

        Label instructionLabel = new Label("Choose your house:");
        VBox houseSelectionBox = new VBox();
        houseSelectionBox.setSpacing(10);
        houseSelectionBox.setAlignment(Pos.CENTER);

        for (House house : House.getAllHouses()) {
            Button houseButton = new Button(house.getName() + " - " + house.getAbility());
            houseButton.setOnAction(e -> {
                player = new Player(playerName, house); // Створюємо гравця після вибору дому
                createEnemy(player.getHouse());        // Створюємо ворога
                updateResourcePanel();                 // Оновлюємо панель ресурсів
                startTurn();                           // Починаємо перший хід
            });
            houseSelectionBox.getChildren().add(houseButton);
        }

        houseSelectionBox.getChildren().add(0, instructionLabel);
        actionButtons.getChildren().add(houseSelectionBox); // Додаємо вибір дому до панелі
    }

    private void createEnemy(House playerHouse) {
        if (enemy != null) return; // Перевірка, щоб уникнути повторного виклику

        House[] houses = House.getAllHouses();
        Random random = new Random();

        House enemyHouse;
        do {
            enemyHouse = houses[random.nextInt(houses.length)];
        } while (enemyHouse.getName().equals(playerHouse.getName()));

        enemy = new Player("Feyd", enemyHouse);

        // Інформуємо лише про ворога
        printToConsole("Your enemy is " + enemy.getName() + " of " + enemy.getHouse().getName() + ".");

        updateResourcePanel(); // Оновлення ресурсів гравця та ворога
    }

    private void startTurn() {
        if (turn > MAX_TURNS) {
            determineWinner();
            return;
        }

        printToConsoleBold("\nTurn " + turn + " begins.");

        // Оновлюємо ресурси гравця
        Map<String, Integer> playerHouseResources = player.getHouse().getResources();
        playerHouseResources.put("Spices", playerHouseResources.get("Spices") + playerSpiceProduction);
        playerHouseResources.put("Water", playerHouseResources.get("Water") - playerWaterConsumption);

        // Оновлюємо ресурси ворога
        Map<String, Integer> enemyHouseResources = enemy.getHouse().getResources();
        enemyHouseResources.put("Spices", enemyHouseResources.get("Spices") + enemySpiceProduction);
        enemyHouseResources.put("Water", enemyHouseResources.get("Water") - enemyWaterConsumption);

        printToConsoleBold(player.getName() + "'s production: +" + playerSpiceProduction +
                " Spices, -" + playerWaterConsumption + " Water.");
        printToConsole(enemy.getName() + "'s production: +" + enemySpiceProduction +
                " Spices, -" + enemyWaterConsumption + " Water.");

        // Гравець і ворог отримують карти
        Card playerCard = drawCard(playerDeck); // Карта для гравця
        Card enemyCard = drawCard(enemyDeck);   // Карта для ворога
        printToConsole("Enemy's card: " + enemyCard.getDescription()); // Повідомлення про карту ворога

        // Хід гравця
        playerTurn(playerCard, enemyCard);
        turn++;
    }

    private void playerTurn(Card playerCard, Card enemyCard) {
        printToConsoleBold("Your card: " + playerCard.getDescription());

        String[] options = playerCard.getOptions();
        showActionButtons(options, choice -> {
            // Дія гравця
            applyCardEffect(player, playerCard, choice);

            // Завершення ходу ворога
            enemyTurn(enemyCard);
            updateResourcePanel();
            startTurn(); // Наступний хід
        });
    }

    private Card drawCard(List<Card> deck) {
        if (deck.isEmpty()) {
            throw new IllegalStateException("No more cards available in this deck.");
        }

        Random random = new Random();
        int index = random.nextInt(deck.size());
        return deck.remove(index); // Вибір та видалення карти з колоди
    }

    private void enemyTurn(Card enemyCard) {
        Random random = new Random();
        int choice = random.nextInt(enemyCard.getOptions().length);

        printToConsole("Enemy takes action: " + enemyCard.getOptions()[choice]);
        applyCardEffect(enemy, enemyCard, choice);
        updateResourcePanel();
    }

    private void handleFinalTurn() {
        printToConsole("Final turn! You may trade resources.");

        String[] options = {
                "Trade 2 credits for 1 water",
                "Trade 2 spices for 1 troop"
        };

        showActionButtons(options, choice -> {
            if (choice == 0) {
                tradeCreditsForWater(player);
            } else if (choice == 1) {
                tradeSpicesForTroops(player);
            }
            updateResourcePanel();
            determineWinner();
        });
    }

    private void determineWinner() {
        // Calculate the coefficient for the player
        double playerCoefficient = calculateCoefficient(player);
        double enemyCoefficient = calculateCoefficient(enemy);

        printToConsole("\nGame Over!");
        printToConsole(player.getName() + " (" + playerCoefficient + ") vs " + enemy.getName() + " (" + enemyCoefficient + ")");

        if (playerCoefficient > enemyCoefficient) {
            printToConsoleBold("Congratulations, you win!");
        } else if (playerCoefficient < enemyCoefficient) {
            printToConsoleBold("You lose! Try again next time.");
        } else {
            printToConsoleBold("It's a tie!");
        }

        // Offer a restart button
        Button restartButton = new Button("Start Again");
        restartButton.setOnAction(e -> restartGame());

        // Clear action buttons and add the restart button
        actionButtons.getChildren().clear();
        actionButtons.getChildren().add(restartButton);
    }

    private int evaluateScore(Player player) {
        Map<String, Integer> resources = player.getHouse().getResources();
        return resources.get("Troops") + resources.get("Water");
    }

    private void applyCardEffect(Player player, Card card, int choice) {
        Map<String, Integer> resources = player.getHouse().getResources();

        switch (card.getId()) {
            case 1: // Worm attacks the harvester
                if (choice == 0) {
                    if (resources.get("Credits") < 500) {
                        printToConsoleBold("Insufficient credits to repair the harvester. You lost the game!");
                        restartGame();
                        return;
                    }
                    resources.put("Credits", resources.get("Credits") - 500);
                    printToConsoleBold(player.getName() + " repaired the harvester for 500 credits.");
                } else if (choice == 1) {
                    if (resources.get("Troops") < 50) {
                        printToConsoleBold("Insufficient troops to stop the worm. You lost the game!");
                        restartGame();
                        return;
                    }
                    resources.put("Troops", resources.get("Troops") - 50);
                    printToConsoleBold(player.getName() + " used troops to stop the worm (-50 troops).");
                } else if (choice == 2) {
                    if (player == this.player) {
                        playerSpiceProduction /= 2;
                        printToConsoleBold(player.getName() + " ignored the worm. Spice production halved.");
                    } else {
                        enemySpiceProduction /= 2;
                        printToConsoleBold(player.getName() + " ignored the worm. Enemy production halved.");
                    }
                }
                break;

            case 2: // Drought event
                if (choice == 0) {
                    if (resources.get("Credits") < 300) {
                        printToConsoleBold("Insufficient credits to import water. You lost the game!");
                        restartGame();
                        return;
                    }
                    resources.put("Credits", resources.get("Credits") - 300);
                    printToConsoleBold(player.getName() + " imported water for 300 credits.");
                } else if (choice == 1) {
                    if (resources.get("Troops") < 30) {
                        printToConsoleBold("Insufficient troops to reorganize. You lost the game!");
                        restartGame();
                        return;
                    }
                    resources.put("Troops", resources.get("Troops") - 30);
                    printToConsoleBold(player.getName() + " reorganized troops (-30 troops).");
                } else if (choice == 2) {
                    resources.put("Troops", resources.get("Troops") - 100);
                    printToConsoleBold(player.getName() + " ignored the drought. Troops suffered (-100 troops).");
                }
                break;

            case 3: // Saboteurs attack
                if (choice == 0) {
                    if (resources.get("Troops") < 20) {
                        printToConsoleBold("Insufficient troops to send reinforcements. You lost the game!");
                        restartGame();
                        return;
                    }
                    resources.put("Troops", resources.get("Troops") - 20);
                    printToConsoleBold(player.getName() + " sent reinforcements to counter the saboteurs (-20 troops).");
                } else if (choice == 1) {
                    resources.put("Troops", resources.get("Troops") - 30);
                    printToConsoleBold(player.getName() + " ignored the saboteurs (-30 troops).");
                } else if (choice == 2) {
                    if (resources.get("Credits") < 50) {
                        printToConsoleBold("Insufficient credits to counter the saboteurs. You lost the game!");
                        restartGame();
                        return;
                    }
                    resources.put("Credits", resources.get("Credits") - 50);
                    printToConsoleBold(player.getName() + " countered the saboteurs with spies (-50 credits).");
                }
                break;

            case 4: // Market fluctuation
                if (choice == 0) {
                    resources.put("Credits", resources.get("Credits") + 200);
                    printToConsoleBold(player.getName() + " sold spices (+200 credits).");
                } else if (choice == 1) {
                    printToConsoleBold(player.getName() + " held on to their spices (no change).");
                } else if (choice == 2) {
                    if (resources.get("Credits") < 100) {
                        printToConsoleBold("Insufficient credits to buy spices. You lost the game!");
                        restartGame();
                        return;
                    }
                    resources.put("Credits", resources.get("Credits") - 100);
                    printToConsoleBold(player.getName() + " bought spices (-100 credits).");
                }
                break;

            case 5: // Sandstorm hits your base
                if (choice == 0) {
                    if (resources.get("Credits") < 300) {
                        printToConsoleBold("Insufficient credits to protect the base. You lost the game!");
                        restartGame();
                        return;
                    }
                    resources.put("Credits", resources.get("Credits") - 300);
                    printToConsoleBold(player.getName() + " protected the base (-300 credits).");
                } else if (choice == 1) {
                    if (resources.get("Troops") < 40) {
                        printToConsoleBold("Insufficient troops to relocate. You lost the game!");
                        restartGame();
                        return;
                    }
                    resources.put("Troops", resources.get("Troops") - 40);
                    printToConsoleBold(player.getName() + " relocated troops (-40 troops).");
                } else if (choice == 2) {
                    if (player == this.player) {
                        resources.put("Troops", resources.get("Troops") - 50);
                        playerSpiceProduction -= 50;
                        printToConsoleBold(player.getName() + " ignored the storm: -50 troops and spice production decreased.");
                    } else {
                        resources.put("Troops", resources.get("Troops") - 50);
                        enemySpiceProduction -= 50;
                        printToConsoleBold(player.getName() + " ignored the storm: -50 troops and spice production decreased.");
                    }
                }
                break;

            case 6: // Rebellion among your troops
                if (choice == 0) {
                    if (resources.get("Credits") < 200) {
                        printToConsoleBold("Insufficient credits to increase wages. You lost the game!");
                        restartGame();
                        return;
                    }
                    resources.put("Credits", resources.get("Credits") - 200);
                    printToConsoleBold(player.getName() + " increased wages for troops (-200 credits).");
                } else if (choice == 1) {
                    if (resources.get("Troops") < 20) {
                        printToConsoleBold("Insufficient troops to reorganize. You lost the game!");
                        restartGame();
                        return;
                    }
                    resources.put("Troops", resources.get("Troops") - 20);
                    printToConsoleBold(player.getName() + " reorganized troops (-20 troops).");
                } else if (choice == 2) {
                    if (resources.get("Water") < 50) {
                        printToConsoleBold("Insufficient water to proceed. You lost the game!");
                        restartGame();
                        return;
                    }
                    resources.put("Water", resources.get("Water") - 50);
                    printToConsoleBold(player.getName() + " ignored the rebellion (-50 water).");
                }
                break;

            case 7: // Discovery of a new spice vein
                if (choice == 0) {
                    if (resources.get("Credits") < 200) {
                        printToConsoleBold("Insufficient credits to expand production. You lost the game!");
                        restartGame();
                        return;
                    }
                    resources.put("Credits", resources.get("Credits") - 200);
                    if (player == this.player) {
                        playerSpiceProduction += 50;
                        printToConsoleBold(player.getName() + " expanded production: -200 credits, spice production increased.");
                    } else {
                        enemySpiceProduction += 50;
                        printToConsoleBold(player.getName() + " expanded production: -200 credits, enemy spice production increased.");
                    }
                } else if (choice == 1) {
                    printToConsoleBold(player.getName() + " ignored the discovery (enemy gains potential advantage).");
                } else if (choice == 2) {
                    if (resources.get("Troops") < 20) {
                        printToConsoleBold("Insufficient troops to relocate. You lost the game!");
                        restartGame();
                        return;
                    }
                    resources.put("Troops", resources.get("Troops") - 20);
                    printToConsoleBold(player.getName() + " relocated troops (-20 troops).");
                }
                break;

            default:
                printToConsoleBold("No specific action defined for this card.");
                break;
        }

        updateResourcePanel();
    }
    private double calculateCoefficient(Player player) {
        Map<String, Integer> resources = player.getHouse().getResources();
        int credits = resources.getOrDefault("Credits", 0);
        int spices = resources.getOrDefault("Spices", 0);
        int troops = resources.getOrDefault("Troops", 0);
        int water = resources.getOrDefault("Water", 0);

        // Assign weights to each resource (adjust as needed for balance)
        double weightedCredits = credits * 0.25;
        double weightedSpices = spices * 0.5;
        double weightedTroops = troops * 0.75;
        double weightedWater = water * 1.0;

        // Calculate total coefficient
        return weightedCredits + weightedSpices + weightedTroops + weightedWater;
    }

    private void tradeCreditsForWater(Player player) {
        Map<String, Integer> resources = player.getHouse().getResources();
        resources.put("Credits", resources.get("Credits") - 2);
        resources.put("Water", resources.get("Water") + 1);
        printToConsoleBold("You traded credits for water.");
    }

    private void tradeSpicesForTroops(Player player) {
        Map<String, Integer> resources = player.getHouse().getResources();
        resources.put("Spices", resources.get("Spices") - 2);
        resources.put("Troops", resources.get("Troops") + 1);
        printToConsoleBold("You traded spices for troops.");
    }

    private void updateResourcePanel() {
        resourcePanel.getChildren().clear();

        Label title = new Label("Player Resources:");
        Map<String, Integer> resources = player.getHouse().getResources();

        VBox resourcesBox = new VBox();
        resourcesBox.setSpacing(5);

        for (Map.Entry<String, Integer> entry : resources.entrySet()) {
            resourcesBox.getChildren().add(new Label(entry.getKey() + ": " + entry.getValue()));
        }

        // Додаємо ресурси на панель
        resourcePanel.getChildren().addAll(title, resourcesBox);
    }



    private void showActionButtons(String[] options, ChoiceHandler handler) {
        actionButtons.getChildren().clear(); // Очищуємо старі кнопки

        for (int i = 0; i < options.length; i++) {
            Button button = new Button(options[i]);
            int index = i;
            button.setOnAction(e -> handler.onChoice(index)); // Викликаємо дію на вибір
            actionButtons.getChildren().add(button); // Додаємо кнопку до панелі
        }
    }

    private void printToConsole(String message) {
        Text text = new Text(message + "\n");
        text.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 14;"); // Загальний стиль
        gameConsole.getChildren().add(text);
    }

    private void printToConsoleBold(String boldMessage) {
        Text boldText = new Text(boldMessage + "\n");
        boldText.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 14; -fx-font-weight: bold;"); // Додаємо жирний шрифт
        gameConsole.getChildren().add(boldText);
    }

    @FunctionalInterface
    interface ChoiceHandler {
        void onChoice(int choice);
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void restartGame() {
        // Очищуємо історію подій у консолі
        gameConsole.getChildren().clear();

        // Очищуємо панель ресурсів
        resourcePanel.getChildren().clear();

        // Очищуємо стан гравця та ворога
        player = null;
        enemy = null;
        turn = 1;

        // Перевизначаємо змінні для нової гри
        playerSpiceProduction = 160;
        playerWaterConsumption = 30;
        enemySpiceProduction = 150;
        enemyWaterConsumption = 25;

        // Скидаємо ресурси для кожного будинку
        for (House house : House.getAllHouses()) {
            Map<String, Integer> resources = house.getResources();
            resources.put("Credits", 1000); // Початковий баланс
            resources.put("Spices", 500);
            resources.put("Water", 200);
            resources.put("Troops", 100);
        }

        // Очищаємо та перевизначаємо колоди карт
        initializeDeck();

        // Перезапускаємо гру: повертаємося до початку
        initializeGame();
    }
}
