package server;

/**
 * @author Jens Henning Lehmann 213172003
 */

import jdk.jshell.spi.ExecutionControl.NotImplementedException;

public class Mitarbeiter {
    private int id;
    private String name;
    private final int anzahlUrlaubstage;
    private Abteilungsleiter abteilungsleiter;
    //private String pwHash;
    private Kalender kalender;

    public Mitarbeiter (int id, String name, int anzahlUrlaubstage, Abteilungsleiter abteilungsleiter, Kalender kalender) {
        this.id = id;
        this.name = name;
        this.anzahlUrlaubstage = anzahlUrlaubstage;
        this.abteilungsleiter = abteilungsleiter;
        this.kalender = kalender;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAnzahlUrlaubstage() {
        return anzahlUrlaubstage;
    }

    public Abteilungsleiter getAbteilungsleiter() {
        return abteilungsleiter;
    }

    public boolean urlaubBeantragen(Urlaubseintrag eintrag) throws NotImplementedException {
        throw new NotImplementedException("kommt bald");
    }
}
