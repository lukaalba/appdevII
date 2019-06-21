package server;

import client.FunctionalityHandler;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class FunctionalityHandlerImpl extends UnicastRemoteObject implements FunctionalityHandler {

    private Mitarbeiter client;
    int mitarbeiterID;

    FunctionalityHandlerImpl() throws RemoteException {
    }

    public String login(int mitarbeiterID, String pwHash) throws RemoteException {

        boolean success = true; //TODO: In der Datenbank Zugangsdaten prüfen

        //TODO: client initialisieren

        if (success)
            return "Login erfolgreich!";
        else
            return "ID oder Passwort inkorrekt.";
    }

    @Override
    public void logout() throws RemoteException {
        //TODO: Datenbankverbindung schließen
        System.out.println("Hallo, der RMI-Aufruf funktioniert!");
    }

    @Override
    public String urlaubEintragen() throws RemoteException {
        Date antrag_beginn;
        Date antrag_ende;
        int urlaub_counter = 0;
        String sqlstatement;
        int abteilungsid = 0;
        boolean urlaubgueltig;
        MariaDBConnection dbconn = new MariaDBConnection();

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
            sqlstatement = "SELECT * FROM Mitarbeiter, Urlaub WHERE Mitarbeiter.ID=Urlaub.MitarbeiterID AND Mitarbeiter.ABTID =?";
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
                ps.setDate(0, antrag_beginn);
                ps.setDate(1, antrag_ende);
                ps.setInt(2, mitarbeiterID);
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
