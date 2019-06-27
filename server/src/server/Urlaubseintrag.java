package src.server;

//TODO: entfernen??

import java.time.LocalDate;

public class Urlaubseintrag {

    // Attribute
    private int id;
    private int mitarbeiterId;
    private LocalDate beginn;
    private LocalDate ende;
    private boolean genehmigt;

    //Konstruktor
    public Urlaubseintrag(int id, int mitarbeiterId, LocalDate beginn, LocalDate ende) {
        this.id = id;
        this.mitarbeiterId = mitarbeiterId;
        this.beginn = beginn;
        this.ende = ende;
    }

    // getter-Methoden
    public int getId() {
        return id;
    }

    public int getMitarbeiterId() {
        return mitarbeiterId;
    }

    public LocalDate getBeginn() {
        return beginn;
    }

    public LocalDate getEnde() {
        return ende;
    }

    public boolean istGenehmigt() {
        return genehmigt;
    }

    // setter-Methoden
    public void setGenehmigt(boolean genehmigt) {
        this.genehmigt = genehmigt;
    }
}
