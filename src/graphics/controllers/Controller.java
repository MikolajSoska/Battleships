package graphics.controllers;

import game.PrepareGame;
import graphics.Board;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public class Controller {
    public GridPane boardPane;
    public Button selectButton;
    public Button resetButton;
    public Label commandLabel;
    public Label errorLabel;
    public Label usernameLabel;

    private Board board;
    private PrepareGame prepareGame;
    private AtomicReference<Integer> shipSize;
    private String playerName;
    private WaitingScreen waitingScreen;

    public void initialize(){
        shipSize = new AtomicReference<>(0);
        board = new Board(boardPane);
        prepareGame = new PrepareGame(shipSize,this);
    }

    private void check(){
        if(board.isCorrect(shipSize.get())) {
            errorLabel.setVisible(false);
            synchronized (this) {
                notify();
            }
        }
        else
            errorLabel.setVisible(true);
    }

    public void startDeployment() {
        board.unlockBoard();
        selectButton.setOnAction(event -> check());
        selectButton.setText("SELECT");
        prepareGame.start();
    }

    public void resetDeployment() {
        prepareGame.interrupt();
        errorLabel.setVisible(false);
        printCommand("To begin deployment, press START");
        board.resetBoard();
        prepareGame = new PrepareGame(shipSize,this);
        selectButton.setOnAction(event -> startDeployment());
    }

    public void printCommand(String text) {
        Platform.runLater(() -> commandLabel.setText(text));
    }

    public void getReady() {
        board.lockBoard();
        printCommand("Press PLAY to start the game");
        Platform.runLater(() -> selectButton.setText("PLAY"));
        selectButton.setOnAction(event -> showWaitingScreen());
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
        usernameLabel.setText(playerName);
    }

    public String getPlayerName() {
        return playerName;
    }

    private void showWaitingScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/graphics/fxml/waitingScreen.fxml"));
            Parent root = loader.load();
            waitingScreen = loader.getController();

            Stage oldStage = (Stage) boardPane.getScene().getWindow();
            oldStage.close();

            Stage waitingStage = new Stage();
            waitingStage.setResizable(false);
            waitingStage.setTitle("Battleship");
            waitingStage.setScene(new Scene(root));

            synchronized (this) {
                notify();
            }

            waitingStage.showAndWait();
            startGame();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeWaitWindow(){
        Platform.runLater(() -> waitingScreen.end());
    }

    private void startGame() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/graphics/fxml/mainScreen.fxml"));
            Parent root = loader.load();
            MainScreen mainScreen = loader.getController();
            mainScreen.createGame(prepareGame.getSocket(), playerName, prepareGame.getEnemyName(), board);
            Stage mainStage = new Stage();

            mainStage.setResizable(false);
            mainStage.setTitle("Battleship");
            mainStage.setScene(new Scene(root));
            mainStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
