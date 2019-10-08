package communication;

import game.MoveAnswers;
import game.PlayerMove;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerThread extends Thread {
    private Socket playerOne;
    private Socket playerTwo;
    private boolean endOfTheGame;

    ServerThread(Socket playerOne, Socket playerTwo) {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        endOfTheGame = false;
    }

    @Override
    public void run() {
        try {
            String playerOneName = (String) readMessage(playerOne);
            String playerTwoName = (String) readMessage(playerTwo);

            writeMessage(playerOne, playerTwoName);
            writeMessage(playerTwo, playerOneName);

            while (!endOfTheGame) {
                gameMode(playerOne, playerTwo);
                gameMode(playerTwo, playerOne);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Object readMessage(Socket socket) throws IOException, ClassNotFoundException {
        ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
        return is.readObject();
    }

    private void writeMessage(Socket socket, Object object) throws IOException {
        ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
        os.writeObject(object);
    }

    private void gameMode(Socket playerOne, Socket playerTwo) throws IOException, ClassNotFoundException {
        if (!endOfTheGame) {
            writeMessage(playerOne, true);
            writeMessage(playerTwo, false);

            PlayerMove playerMove = (PlayerMove) readMessage(playerOne);
            writeMessage(playerTwo, playerMove);
            MoveAnswers answer = (MoveAnswers) readMessage(playerTwo);
            if (answer.equals(MoveAnswers.MISS))
                writeMessage(playerOne, answer);
            else {
                writeMessage(playerOne, answer);
                if (answer.equals(MoveAnswers.END))
                    endOfTheGame = true;
                else {
                    gameMode(playerOne, playerTwo);
                }
            }
        }
    }
}
