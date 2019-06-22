package server;

import client.FunctionalityHandler;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class FunctionalityHandlerImpl extends UnicastRemoteObject implements FunctionalityHandler {
    private Mitarbeiter client;
    int mitarbeiterID;
    String sqlstatement;
    MariaDBConnection dbconn = new MariaDBConnection();

    FunctionalityHandlerImpl() throws RemoteException {
    }

    @Override
    public void setmitarbeiterID(int mitarbeiterID) throws RemoteException {
        this.mitarbeiterID = mitarbeiterID;
    }

    @Override
    public boolean login(int mitarbeiterID, String pwHash) throws RemoteException {
        boolean success = false;

        sqlstatement = "SELECT MitarbeiterID, Passwort FROM Mitarbeiter WHERE Mitarbeiter.MitarbeiterID=? AND Mitarbeiter.Passwort=?";
        try {
            PreparedStatement ps = dbconn.dbconn().prepareStatement(sqlstatement);
            ps.setInt(1, mitarbeiterID);
            ps.setString(2, pwHash);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                success = true;
            }
            else {
                success = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //TODO: client initialisieren

        return success;
    }

    @Override
    public void logout() throws RemoteException {
        //TODO: Datenbankverbindung schließen
    }

    @Override
    public String urlaubEintragen(Date antrag_beginn, Date antrag_ende) throws RemoteException {
        // TODO: Urlaub wird noch nicht in DB eingetragen
        int urlaub_counter = 0;
        int abteilungsid = 0;
        boolean urlaubgueltig;


        // Eingabemöglichkeit um Urlaub einzugeben
        long milli_antrag_beginn = antrag_beginn.getTime();
        long milli_antrag_ende = antrag_ende.getTime();


        sqlstatement = "SELECT gesamtUrlaubstage, ABTID FROM Mitarbeiter WHERE MitarbeiterID=?";
        try {
            PreparedStatement ps = dbconn.dbconn().prepareStatement(sqlstatement);
            ps.setInt(1, mitarbeiterID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                urlaub_counter = rs.getInt("gesamtUrlaubsTage");
                abteilungsid = rs.getInt("ABTID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        sqlstatement = "SELECT * FROM Urlaub WHERE Urlaub.MitarbeiterID=?";
        try {
            PreparedStatement ps = dbconn.dbconn().prepareStatement(sqlstatement);
            ps.setInt(1, mitarbeiterID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int anzahlUrlaubstage = rs.getInt("Mitarbeiter.gesamtUrlaubsTage");
                Date beginn = rs.getDate("Urlaub.Beginn");
                Date ende = rs.getDate("Urlaub.Ende");
                long diffInMillies = Math.abs(ende.getTime() - beginn.getTime());
                long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                int intdiff = Math.toIntExact(diff);
                urlaub_counter = urlaub_counter - intdiff;

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (urlaub_counter > 0) {
            sqlstatement = "SELECT * FROM Mitarbeiter, Urlaub WHERE Mitarbeiter.MitarbeiterID=Urlaub.MitarbeiterID AND Mitarbeiter.ABTID =?";
            try {
                PreparedStatement ps = dbconn.dbconn().prepareStatement(sqlstatement);
                ps.setInt(1, abteilungsid);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Date beginn = rs.getDate("Beginn");
                    long milli_beginn = beginn.getTime();
                    Date ende = rs.getDate("Ende");
                    long milli_ende = ende.getTime();
                    if (milli_ende < milli_antrag_beginn && milli_beginn > milli_antrag_ende) {
                        System.out.println("Vertretung gefunden: " + rs.getString("Name"));
                        urlaubgueltig = true;
                        break;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            return ("Der diesjährige Urlaub wurde leider schon aufgebraucht");
        }
        if (urlaubgueltig = true) {
            sqlstatement = "INSERT INTO Urlaub(Beginn, Ende, MitarbeiterID) VALUES(?,?,?)";
            try {
                PreparedStatement ps = dbconn.dbconn().prepareStatement(sqlstatement);
                ps.setDate(1, antrag_beginn);
                ps.setDate(2, antrag_ende);
                ps.setInt(3, mitarbeiterID);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            return "Keine Vertretung vorhanden!";
        }
        return "Urlaub beantragt!";
    }

    @Override
    public String urlaubGenehmigen(int urlaubsID) throws RemoteException {
        if (client instanceof Abteilungsleiter) {
            //TODO: Auf der Datenbank den entsprechenden Urlaub als genehmigt markieren
        }
        else throw new RemoteException();

        return null;
    }
}
