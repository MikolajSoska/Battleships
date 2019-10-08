package game;

import graphics.Board;
import graphics.controllers.MainScreen;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MainGame extends Thread {
    private final MainScreen mainScreen;
    private Socket socket;
    private Board playerBoard;
    private Board enemyBoard;
    private boolean endOfTheGame;

    public MainGame(MainScreen mainScreen, Socket socket, Board playerBoard, Board enemyBoard){
        this.mainScreen = mainScreen;
        this.socket = socket;
        this.playerBoard = playerBoard;
        this.enemyBoard = enemyBoard;
        endOfTheGame = false;
    }

    private boolean canMove() throws IOException, ClassNotFoundException {
        return (boolean) readFromServer();
    }

    private void writeToServer(Object object) throws IOException {
        ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
        os.writeObject(object);
    }

    private Object readFromServer() throws IOException, ClassNotFoundException {
        ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
        return is.readObject();
    }

    private char numberToChar(int number) {
        char c = 'A';
        while (c < number + 'A')
            c++;
        return c;
    }

    @Override
    public void run() {
        while (!endOfTheGame) {
            try {
                if (canMove()) {
                    mainScreen.selectingModeON();
                    synchronized (mainScreen) {
                        mainScreen.wait();
                    }
                    mainScreen.selectingModeOFF();
                    int x = enemyBoard.getSelectedX();
                    int y = enemyBoard.getSelectedY();
                    String move = "SELECTED FIELD: " + Character.toString(numberToChar(y)) + Integer.toString(x+1);
                    PlayerMove playerMove = new PlayerMove(x,y,false);
                    writeToServer(playerMove);
                    MoveAnswers answer = (MoveAnswers) readFromServer();
                    if (answer.equals(MoveAnswers.MISS)) {
                        enemyBoard.lockFieldAsMiss(x,y);
                        move += " - MISS";
                    }
                    else {
                        enemyBoard.lockFieldAsHit(x, y);
                        if(answer.equals(MoveAnswers.HIT))
                            move += " - HIT";
                        else {
                            enemyBoard.markShipAsDestroyed(x,y);
                            move += " - HIT AND SINK";
                            if(answer.equals(MoveAnswers.END)) {
                                endOfTheGame = true;
                                mainScreen.gameWon();
                            }
                        }
                    }
                    mainScreen.writePlayerMoveToList(move);
                }
                else {
                    mainScreen.selectingModeOFF();
                    PlayerMove playerMove = (PlayerMove) readFromServer();
                    int x = playerMove.getX();
                    int y = playerMove.getY();
                    String move = "SELECTED FIELD: " + Character.toString(numberToChar(y)) + Integer.toString(x+1);
                    if(playerBoard.isFieldEmpty(x,y)) {
                        playerBoard.lockFieldAsEnemyMiss(x,y);
                        mainScreen.resultIsMiss();
                        move += " - MISS";
                        writeToServer(MoveAnswers.MISS);
                    }
                    else {
                        playerBoard.lockFieldAsEnemyHit(x,y);
                        mainScreen.resultIsHit();
                        if(playerBoard.isShipDestroyed(x,y)) {
                            move += " - HIT AND SINK";
                            if(playerBoard.isAllShipsDestroyed()) {
                                writeToServer(MoveAnswers.END);
                                endOfTheGame = true;
                                mainScreen.gameLost();
                            }
                            else
                                writeToServer(MoveAnswers.DESTROYED);
                        }
                        else {
                            move += " - HIT";
                            writeToServer(MoveAnswers.HIT);
                        }
                    }
                    mainScreen.writeEnemyMoveToList(move);
                    Thread.sleep(500);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
