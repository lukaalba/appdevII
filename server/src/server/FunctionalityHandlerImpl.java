package server;

import client.FunctionalityHandler;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.concurrent.TimeUnit;

public class FunctionalityHandlerImpl extends UnicastRemoteObject implements FunctionalityHandler {
    private Mitarbeiter client;
    PreparedStatement ps = null;
    String sqlstatement;
    MariaDBConnection dbconn = new MariaDBConnection();
    Connection connection;


    FunctionalityHandlerImpl() throws RemoteException {
    }

    @Override
    public boolean login(int mitarbeiterID, String pwHash) throws RemoteException {
        boolean success = false;
        String name = null;
        int anzUrlaubstage = 0;
        int abtID = 0;




        sqlstatement = "SELECT Vorname, Nachname, GesamtUrlaubstage, ABTID FROM Mitarbeiter WHERE Mitarbeiter.MitarbeiterID=? AND Mitarbeiter.Passwort=?";
        try {
            connection = dbconn.dbconn();
            ps = connection.prepareStatement(sqlstatement);
            ps.setInt(1, mitarbeiterID);
            ps.setString(2, pwHash);
            ResultSet rs = ps.executeQuery();
            System.out.println("Nach der Query");
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
                ps = connection.prepareStatement(sqlstatement);
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
            ps = connection.prepareStatement(sqlstatement);
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
                ps = connection.prepareStatement(sqlstatement);
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
            sqlstatement = "INSERT INTO Urlaub(Beginn, Ende, MitarbeiterID, Genehmigt) VALUES(?,?,?,?)";
            try {
                ps = connection.prepareStatement(sqlstatement);
                ps.setDate(1, antrag_beginn);
                ps.setDate(2, antrag_ende);
                ps.setInt(3, client.getId());
                ps.setBoolean(4, false);
                ps.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            sqlstatement = "INSERT INTO Urlaub(Beginn, Ende, MitarbeiterID, Genehmigt) VALUES(?,?,?,?)";
            try {
                ps = connection.prepareStatement(sqlstatement);
                ps.setDate(1, antrag_beginn);
                ps.setDate(2, antrag_ende);
                ps.setInt(3, client.getId());
                ps.setBoolean(4, true);
                ps.execute();
                message = "Urlaub beantragt! " + message;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        dbconn.closeCon();
        return message;

    }

    @Override
    public String urlaubGenehmigen(int mitarbeiterId, Date begin, Date ende) throws RemoteException {
        if (client instanceof Abteilungsleiter) {
            //TODO: Auf der Datenbank den entsprechenden Urlaub als genehmigt markieren
            sqlstatement="UPDATE urlaub SET Genehmigt=1 WHERE MitarbeiterID=? AND Beginn=? AND Ende=?";
            try {
                ps = connection.prepareStatement(sqlstatement);
                ps.setInt(1,mitarbeiterId);
                ps.setDate(2,begin);
                ps.setDate(3,ende);
                boolean worked = ps.execute();
                if(worked){
                    return "Urlaub wurde genehmigt!";
                }
                else
                    return "Urlaub wurde aufgrund eines Fehlers nicht genehmigt";
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
        else throw new RemoteException();

        return null;
    }

    @Override
    public ResultSet getNichtGenehmigteUrlaubsTage(int mitarbeiterId) throws RemoteException {
        if (client instanceof Abteilungsleiter) {
            ResultSet set = null;
            sqlstatement = "SELECT * FROM Urlaub WHERE Urlaub.MitarbeiterID IN (Select mitarbeiter.MitarbeiterID from mitarbeiter where mitarbeiter.ABTID=? AND mitarbeiter.MitarbeiterID=?) AND urlaub.Genehmigt=0";
            try {
                ps = connection.prepareStatement(sqlstatement);
                ps.setInt(1, client.getAbtID());
                ps.setInt(2, mitarbeiterId);
                set = ps.executeQuery();
                return set;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    @Override
    public String urlaubLoeschen(int mitarbeiterId, Date begin, Date ende) throws RemoteException {
        if (client instanceof Abteilungsleiter) {
            sqlstatement = "DELETE FROM urlaub WHERE MitarbeiterID=? AND Beginn=? AND ENDE=?";
            try {
                ps = connection.prepareStatement(sqlstatement);
                ps.setInt(1, mitarbeiterId);
                ps.setDate(2, begin);
                ps.setDate(3, ende);
                boolean worked = ps.execute();
                if (worked) {
                    return "Urlaubssatz wurde gelöscht";
                } else
                    return "Urlaubssatz wurde aufgrund eines Fehlers nicht gelöscht";

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


}
