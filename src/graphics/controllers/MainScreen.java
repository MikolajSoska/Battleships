package graphics.controllers;

import game.MainGame;
import graphics.Board;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.net.Socket;
import java.util.ArrayList;

public class MainScreen {
    public GridPane playerPane;
    public GridPane enemyPane;
    public ListView<String> playerMovesList;
    public ListView<String> enemyMovesList;
    public Label playerNameLabel;
    public Label enemyNameLabel;
    public Label commandLabel;
    public Label moveResultLabel;
    public Label errorLabel;
    public Button selectButton;

    private MainGame mainGame;
    private Board playerBoard;
    private Board enemyBoard;
    private ArrayList<String> playerMoves;
    private ArrayList<String> enemyMoves;

    private void init(){
        playerBoard.setBoard(playerPane);
        enemyBoard = new Board(enemyPane);
        playerMoves = new ArrayList<>();
        enemyMoves = new ArrayList<>();
    }

    public void createGame(Socket socket, String playerName, String enemyName, Board playerBoard){
        Platform.runLater(() -> {
            playerNameLabel.setText(playerName + "'s board");
            enemyNameLabel.setText(enemyName + "'s board");
        });

        this.playerBoard = playerBoard;
        init();
        mainGame = new MainGame(this, socket, playerBoard, enemyBoard);
        mainGame.start();
    }

    public void setCommand(String command) {
        Platform.runLater(() -> commandLabel.setText(command));
    }

    public void resultIsHit() {
        Platform.runLater(() -> {
            moveResultLabel.setText("ENEMY HIT");
            moveResultLabel.setTextFill(Color.RED);
            moveResultLabel.setVisible(true);
        });
    }

    public void resultIsMiss() {
        Platform.runLater(() -> {
            moveResultLabel.setText("ENEMY MISS");
            moveResultLabel.setTextFill(Color.GREEN);
            moveResultLabel.setVisible(true);
        });
    }

    public void selectingModeON() {
        enemyBoard.selectingModeON();
        selectButton.setOnAction(event -> getSelectedField());
        moveResultLabel.setVisible(false);
        setCommand("YOUR MOVE. Select the field");
    }

    public void selectingModeOFF() {
        enemyBoard.selectingModeOFF();
        selectButton.setOnAction(null);
        setCommand("ENEMY'S MOVE");
    }

    public void getSelectedField() {
        if(enemyBoard.isSelected()) {
            errorLabel.setVisible(false);
            synchronized (this) {
                notify();
            }
        }
        else
            errorLabel.setVisible(true);
    }

    public void writePlayerMoveToList(String move) {
        playerMoves.add(move);
        playerMovesList.setItems(FXCollections.observableList(playerMoves));
    }

    public void writeEnemyMoveToList(String move) {
        enemyMoves.add(move);
        enemyMovesList.setItems(FXCollections.observableList(enemyMoves));
    }

    public void gameLost() {
        setCommand("GAME OVER - YOU LOST");
        commandLabel.setTextFill(Color.RED);
        endGame();
    }

    public void gameWon() {
        setCommand("CONGRATULATIONS - YOU WIN");
        commandLabel.setTextFill(Color.GREEN);
        endGame();
    }

    private void endGame() {
        Platform.runLater(() -> selectButton.setText("PRESS TO CLOSE THE GAME"));
        selectButton.setOnAction(event -> Platform.exit());
        moveResultLabel.setVisible(false);
    }
}
