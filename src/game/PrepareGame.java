package game;

import graphics.controllers.Controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicReference;

public class PrepareGame extends Thread {
    private AtomicReference<Integer> shipSize;
    private final Controller controller;
    private boolean reset;
    private Socket socket;
    private String enemyName;

    public PrepareGame(AtomicReference<Integer> shipSize, Controller controller){
        this.shipSize = shipSize;
        this.controller = controller;
        reset = false;
    }

    private void createShips(int size, int numberOfRepetitions){
        shipSize.set(size);
        for(int i = 0; i < numberOfRepetitions; i++) {
            controller.printCommand("Select position for a " + size + "-decker number " + (i+1));
            synchronized (controller) {
                try {
                    controller.wait();
                } catch (InterruptedException e) {
                    reset = true;
                    break;
                }
            }
        }
    }

    public String getEnemyName() {
        return enemyName;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {
        try {
            for(int i = 4, j = 1; i > 0; i--, j++) {
                createShips(i, j);
                if(reset)
                    return;
            }
            controller.getReady();
            synchronized (controller) {
                controller.wait();
            }
            socket = new Socket("10.102.102.74", 88);
            ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
            os.writeObject(controller.getPlayerName());
            ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
            enemyName = (String) is.readObject();
            controller.closeWaitWindow();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException ignored) {}

    }
}
