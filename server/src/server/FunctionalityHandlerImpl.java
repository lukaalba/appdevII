package server;

import client.FunctionalityHandler;

import java.rmi.RemoteException;

public class FunctionalityHandlerImpl implements FunctionalityHandler {

    private Mitarbeiter client;

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
    }

    @Override
    public String urlaubEintragen() throws RemoteException {
        return null;
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
