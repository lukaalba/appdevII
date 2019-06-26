package client;

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

            FunctionalityHandler stub = (FunctionalityHandler) Naming.lookup("rmi://localhost:1666/stub");
            Scanner eingabe = new Scanner(System.in).useDelimiter("\\n");
            System.out.println("Login: ");
            id = eingabe.nextInt();
            stub.setmitarbeiterID(id);
            System.out.println("Passwort: ");
            password = eingabe.next();
            if (stub.login(id, password)) {
                System.out.println("Login erfolgreich!");
                System.out.println("Was möchten Sie machen?");
                System.out.println("1: Urlaub beantragen");
                System.out.println("2: Urlaub genehmigen");
                System.out.println("3: Beenden");
                wahl = eingabe.nextInt();
            }
            else {
                System.exit(0);
            }
            switch(wahl) {
                case 1: System.out.println("Beginn des Urlaubs (Format YYYY-MM-DD): ");
                        beginn = Date.valueOf(eingabe.next());
                        System.out.println("Ende des Urlaubs: (Format YYYY-MM-DD): ");
                        ende = Date.valueOf(eingabe.next());
                        System.out.println(stub.urlaubEintragen(beginn, ende));
                        break;
                case 2: stub.urlaubGenehmigen(1); //TODO: Urlaub genehmigen implementieren
                        break;
                case 3: System.exit(0);
            }




//            Registry registry = LocateRegistry.getRegistry(null);

//            FunctionalityHandler handerStub = (FunctionalityHandler) registry.lookup("FunctionalityHandler");



        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
