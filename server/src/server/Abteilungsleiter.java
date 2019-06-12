package server;

public class Abteilungsleiter extends Mitarbeiter {
    public Abteilungsleiter(int id, String name, int anzUrl, Abteilungsleiter al, Kalender ka) {
        super(id, name, anzUrl, al, ka);
    }

    public void urlaubGenehmigen(int urlID) {

    }
}
