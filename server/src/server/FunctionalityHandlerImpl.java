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
    String sqlstatement;
    MariaDBConnection dbconn = new MariaDBConnection();

    FunctionalityHandlerImpl() throws RemoteException {
    }

    @Override
    public void setmitarbeiterID(int mitarbeiterID) throws RemoteException {
        //this.mitarbeiterID = mitarbeiterID;
    }

    @Override
    public boolean login(int mitarbeiterID, String pwHash) throws RemoteException {
        boolean success = false;
        String name = null;
        int anzUrlaubstage = 0;
        int abtID = 0;


        sqlstatement = "SELECT Vorname, Nachname, GesamtUrlaubstage, ABTID FROM Mitarbeiter WHERE Mitarbeiter.MitarbeiterID=? AND Mitarbeiter.Passwort=?";
        try {
            PreparedStatement ps = dbconn.dbconn().prepareStatement(sqlstatement);
            ps.setInt(1, mitarbeiterID);
            ps.setString(2, pwHash);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                success = true;
                name = String.format("%s %s", rs.getString("Vorname"), rs.getString("Nachname"));
                anzUrlaubstage = rs.getInt("GesamtUrlaubstage");
                abtID = rs.getInt("ABTID");

            }
            else {
                success = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (success) {
            sqlstatement = "SELECT 1 FROM Abteilung WHERE Abteilung.AL=?";
            try {
                PreparedStatement ps = dbconn.dbconn().prepareStatement(sqlstatement);
                ps.setInt(1, mitarbeiterID);
                ResultSet rs = ps.executeQuery();
                if (rs.next())
                    client = new Abteilungsleiter(mitarbeiterID, name, anzUrlaubstage, abtID);
                else
                    client = new Mitarbeiter(mitarbeiterID, name, anzUrlaubstage, abtID);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return success;
    }

    @Override
    public void logout() throws RemoteException {
        //TODO: Datenbankverbindung schließen
    }

    @Override
    public String urlaubEintragen(Date antrag_beginn, Date antrag_ende) throws RemoteException {
        String message = null;
        boolean urlaubgueltig = false;
        long milli_ende;
        long milli_beginn;

        // Eingabemöglichkeit um Urlaub einzugeben
        long milli_antrag_beginn = antrag_beginn.getTime();
        long milli_antrag_ende = antrag_ende.getTime();

        long milli_eingabe_differenz = milli_antrag_ende - milli_antrag_beginn;
        long diff_eingabe = TimeUnit.DAYS.convert(milli_eingabe_differenz, TimeUnit.MILLISECONDS);
        int diff_eingabeInt = Math.toIntExact(diff_eingabe);


        // Überprüfung, wie viel Urlaub bereits genommen wurde
        sqlstatement = "SELECT * FROM Urlaub WHERE Urlaub.MitarbeiterID=?";
        try {
            PreparedStatement ps = dbconn.dbconn().prepareStatement(sqlstatement);
            ps.setInt(1, client.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Date beginn = rs.getDate("Urlaub.Beginn");
                Date ende = rs.getDate("Urlaub.Ende");
                long diffInMillies = Math.abs(ende.getTime() - beginn.getTime());
                long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                int intdiff = Math.toIntExact(diff);
                client.setResturlaub(client.getResturlaub() - intdiff);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Überprüfung, ob Vertretung vorhanden
        if (client.getResturlaub() > diff_eingabeInt) {
            sqlstatement = "SELECT * FROM Mitarbeiter LEFT JOIN Urlaub ON Mitarbeiter.MitarbeiterID = Urlaub.MitarbeiterID WHERE Mitarbeiter.ABTID=? AND NOT Mitarbeiter.MitarbeiterID =?";
            try {
                PreparedStatement ps = dbconn.dbconn().prepareStatement(sqlstatement);
                ps.setInt(1, client.getAbtID());
                ps.setInt(2, client.getId());
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {

                    milli_beginn = 0;
                    milli_ende = 0;
                    Date beginn = rs.getDate("Beginn");
                    if (beginn != null) {
                        milli_beginn = beginn.getTime();
                    }
                    Date ende = rs.getDate("Ende");
                    if (ende != null) {
                        milli_ende = ende.getTime();
                    }

                    if (milli_ende == 0 || milli_beginn == 0 || (milli_ende < milli_antrag_beginn && milli_beginn > milli_antrag_ende)) {
                        message = "Die Vertretung ist: " + rs.getString("Nachname");
                        urlaubgueltig = true;
                        break;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            message = "Der diesjährige Urlaub wurde leider schon aufgebraucht";
            return message;
        }
        // Urlaub in DB eintragen
        if (!urlaubgueltig) {
            message = "Keine Vertretung vorhanden!";
            return message;
        } else {
            sqlstatement = "INSERT INTO Urlaub(Beginn, Ende, MitarbeiterID, Genehmigt) VALUES(?,?,?,?)";
            try {
                PreparedStatement ps = dbconn.dbconn().prepareStatement(sqlstatement);
                ps.setDate(1, antrag_beginn);
                ps.setDate(2, antrag_ende);
                ps.setInt(3, client.getId());
                ps.setBoolean(4, false);
                ps.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        message = "Urlaub beantragt! " + message;
        return message;
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
