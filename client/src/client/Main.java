package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {
    private static int id;
    private static String password;
    private static Date beginn;
    private static Date ende;
    private static final String host = "localhost";
    private static final int port = 1666;

    public static void main(String[] args) {
        try {
            //TODO: Passwort hashen
            int wahl = 0;
            boolean success = false;

            FunctionalityHandler stub = (FunctionalityHandler) Naming.lookup(String.format("rmi://%s:%d/stub", host, port));
            stub.connect();
            Thread infoStreamPrinter = new InfoStreamPrinter(host, port);

            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

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
                        try {
                            System.out.println("Urlaub genehmigen gewählt!");
                            System.out.println("Bitte geben Sie die Mitarbeiternummer der Person ein, dessen Urlaub sie genehmigen wollen");
                            int personalnr = Integer.parseInt(input.readLine());
                            ResultSet rset = stub.getNichtGenehmigteUrlaubsTage(personalnr);
                            boolean running= true;
                            if (rset!= null){
                                while (rset.next() && running){
                                    System.out.println("Der Mitarbeiter möchte von "+rset.getDate("Beginn")+" bis "+rset.getDate("Ende")+" freinehmen");
                                    System.out.println("Möchten Sie diesen genehmigen? (J/N/S). Drücken sie E u um die Abfrage zu verlassen!");
                                    String antwort = input.readLine();
                                    switch (antwort){
                                        case "J":
                                            System.out.println(stub.urlaubGenehmigen(rset.getInt("MitarbeiterID"),rset.getDate("Beginn"),rset.getDate("Ende")));
                                            break;
                                        case "N":
                                            System.out.println(stub.urlaubLoeschen(rset.getInt("MitarbeiterID"),rset.getDate("Beginn"),rset.getDate("Ende")));
                                            break;
                                        case "S":
                                            break;
                                        case "E":
                                            running=false;
                                            break;
                                        default:
                                            System.out.println("Nicht korrekte Taste eingegeben");
                                            break;
                                    }


                                }
                            }
                            else
                                System.out.println("Der Mitarbeiter mit der Nr: "+personalnr+" hat keine nicht genehmigten Urlaubstage!");
                            break;

                        } catch (IOException ioe){
                            ioe.printStackTrace();
                        }
                          catch (SQLException sqle){
                            sqle.printStackTrace();
                        }

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
