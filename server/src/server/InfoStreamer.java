package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class InfoStreamer {
    private Socket clientSocket;
    private ServerSocket serverSocket;
    private PrintWriter writer;

    public InfoStreamer(int port) {
        try {
            this.serverSocket = new ServerSocket(port);
            this.clientSocket = serverSocket.accept();
            System.out.println("Client verbunden!");
            this.writer = new PrintWriter(clientSocket.getOutputStream());
            send("Verbindung mit Server erfolgreich.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String message) {
        if (clientSocket.isConnected()) {
            writer.println(message);
            writer.flush();
        } else {
            System.err.println("Senden nicht möglich; Client ist nicht verbunden!");
        }
    }

    public void close() {
        writer.close();
        try {
            serverSocket.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Die Serververbindung wurde getrennt.");
    }
}
