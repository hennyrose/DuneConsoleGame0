# Dune Console Game 🏜️

Welcome to the Dune Console Game, a turn-based strategy game inspired by Frank Herbert's *Dune* series. Choose your house, manage your resources, and respond to dynamic scenarios as you conquer the universe!

## 🚀 Features
- Three Houses: Atreides, Harkonnen, and Corrino.
- Turn-based gameplay: Manage cards and resources to succeed.
- Logical challenges: Exercise your strategic thinking.

---

## 🛠️ Requirements
To run the game, ensure you have the following:
1. Java Development Kit (JDK) version 20 or higher.
2. (Optional) [JavaFX](https://openjfx.io/) SDK if required by your platform (for UI-based versions).

---

## 📦 How to Launch the Game
Follow these steps to run the `.jar` file:

### 1. Ensure Java is Installed
Verify that you have the required Java version installed:
```sh
java -version
```
If you don't have it, download and install JDK 20 from [Oracle JDK Downloads](https://www.oracle.com/java/technologies/javase/jdk20-archive-downloads.html).

### 2. **Download JavaFX** (if required)
If your project requires JavaFX, download it from [JavaFX Downloads](https://openjfx.io/).  
- Select your system (e.g., Windows, macOS, Linux) and download the latest stable version.
- Unzip the downloaded file and note the **`lib` directory path**.

### 3. **Download the JAR File**
Clone the repository or download the `.jar` file:
```sh
git clone https://github.com/yourusername/DuneConsoleGame.git
cd DuneConsoleGame
```

### 4. **Run the JAR File**
Run the game in a terminal or command prompt:

#### If JavaFX is NOT required:
```sh
java -jar DuneConsoleGame.jar
```

#### If JavaFX is required:
Add the JavaFX module path:
```sh
java --module-path /path-to-javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -jar DuneConsoleGame.jar
```

Replace `/path-to-javafx-sdk/lib` with the directory path to your JavaFX `lib` folder.

---

## 🎯 Building the JAR File (Optional)
If you want to build the JAR yourself:
1. Use your favorite IDE (e.g., IntelliJ IDEA) to compile the code.
2. Package the compiled `.class` files into a JAR file using the following command:
   ```sh
   jar --create --file DuneConsoleGame.jar -C out .
   ```

To run your custom-built `.jar`, use the instructions from the section above.

---

## ✨ Future Improvements
- Add customizable difficulty levels.
- Include more cards and events.
- Add to Telegram Mini Apps.

Feel free to contribute, open issues, or suggest ideas!

---

## 👨‍💻 Author
Igor Rozovetskiy 
Inspired by the legendary world of Dune!

---

Enjoy the game and let me know your feedback! 🌟
