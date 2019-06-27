package src.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Date;
import java.util.Scanner;

public class Main {
    private static int id;
    private static String password;
    private static Date beginn;
    private static Date ende;
    public static void main(String[] args) {
        try {
            //TODO: Passwort hashen
            int wahl = 0;
            boolean success = false;

            FunctionalityHandler stub = (FunctionalityHandler) Naming.lookup("rmi://localhost:1666/stub");
            InputStream in = System.in;
            InputStreamReader inReader = new InputStreamReader(in);
            BufferedReader input = new BufferedReader(inReader);

            for (int i =0; i < 3; i++) {
                System.out.println("Login: ");
                try {
                    id = Integer.parseInt(input.readLine());
                    System.out.println("Passwort: ");
                    password = input.readLine();
                    if (stub.login(id, password)) {
                        System.out.println("Login erfolgreich!");
                        System.out.println("Was möchten Sie machen?");
                        System.out.println("1: Urlaub beantragen");
                        System.out.println("2: Urlaub genehmigen");
                        System.out.println("3: Beenden");
                        wahl = Integer.parseInt(input.readLine());
                        success = true;
                        break;

                    }
                    else {
                        System.out.println("Falsche MitarbeiterID oder falsches Passwort");
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }


            }

            if (success) {
                switch(wahl) {
                    case 1:
                        try {
                            System.out.println("Beginn des Urlaubs (Format YYYY-MM-DD): ");
                            beginn = Date.valueOf(input.readLine());
                            System.out.println("Ende des Urlaubs: (Format YYYY-MM-DD): ");
                            ende = Date.valueOf(input.readLine());
                            System.out.println(stub.urlaubEintragen(beginn, ende));
                            break;
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        }

                    case 2:
                        stub.urlaubGenehmigen(1); //TODO: Urlaub genehmigen implementieren
                        break;
                    case 3:
                        System.exit(0);
                }
            }
            else {
                System.exit(403);
            }


        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
