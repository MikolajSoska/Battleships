package communication;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;

public class Server  {

    public static void main(String args[]) {
        try {
            ServerSocket serverSocket = new ServerSocket(88,2);
            System.out.println("Server ON");
            System.out.println(Inet4Address.getLocalHost());

            Socket socket1 = serverSocket.accept();
            Socket socket2 = serverSocket.accept();

            ServerThread serverThread = new ServerThread(socket1, socket2);
            serverThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
