package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class InfoStreamPrinter extends Thread{
    private String url;
    private int portNr;

    public InfoStreamPrinter(String url, int portNr) {
        this.url = url;
        this.portNr = portNr;
    }

    @Override
    public void run() {
        try {
            Socket clientSocket = new Socket(url, portNr);

            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String message;

            while (!(message = reader.readLine()).toLowerCase().equals("exit")) {
                System.out.println(message);
            }
            System.out.println("Die Serververbindung wurde getrennt.");

            clientSocket.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
