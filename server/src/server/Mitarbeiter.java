package src.server;

/**
 * @author Jens Henning Lehmann 213172003
 */

import jdk.jshell.spi.ExecutionControl.NotImplementedException;

public class Mitarbeiter {
    private int id;
    private String name;
    private final int anzahlUrlaubstage;
    private int resturlaub;
    private int abtID;
    //private String pwHash;

    public Mitarbeiter (int id, String name, int anzahlUrlaubstage, int abtID) {
        this.id = id;
        this.name = name;
        this.anzahlUrlaubstage = anzahlUrlaubstage;
        this.abtID = abtID;
        this.resturlaub = anzahlUrlaubstage;
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

    public int getAbtID() { return abtID; }

    public int getResturlaub() {
        return resturlaub;
    }

    public void setResturlaub(int resturlaub) {
        this.resturlaub = resturlaub;
    }
}
