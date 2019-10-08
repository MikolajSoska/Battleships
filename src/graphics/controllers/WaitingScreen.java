package graphics.controllers;

import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class WaitingScreen extends Thread {
    public Circle firstDot;
    public Circle secondDot;
    public Circle thirdDot;

    private WaitingThread waitingThread;
    private final int SLEEPING_TIME = 500;

    public void initialize() {
        waitingThread = new WaitingThread();
        waitingThread.start();
    }

    public void end() {
        waitingThread.interrupt();
        ((Stage) firstDot.getScene().getWindow()).close();
    }

    private class WaitingThread extends Thread {
        @Override
        public void run() {
            for (;;) {
                try {
                    sleep(SLEEPING_TIME);
                    firstDot.setVisible(false);
                    secondDot.setVisible(false);
                    thirdDot.setVisible(false);
                    showDot(firstDot);
                    showDot(secondDot);
                    showDot(thirdDot);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }

        private void showDot(Circle dot) throws InterruptedException {
            sleep(SLEEPING_TIME);
            dot.setVisible(true);
        }
    }
}
