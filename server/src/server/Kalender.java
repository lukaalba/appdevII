package server;

//TODO: entfernen??

import jdk.jshell.spi.ExecutionControl.NotImplementedException;
import java.util.ArrayList;
import static server.CustomExceptions.*;

public class Kalender {
    private ArrayList<Urlaubseintrag> urlaubsliste;

    public Kalender(ArrayList<Urlaubseintrag> urlaubsliste) {
        this.urlaubsliste = urlaubsliste;
    }

    public void urlaubEintragen(Urlaubseintrag eintrag) throws UrlaubNichtGenehmigtException, NotImplementedException{
        if (!stellvertreterVorhanden(eintrag)) {
            throw new UrlaubNichtGenehmigtException();
        }
        urlaubsliste.add(eintrag);
    }

    public boolean stellvertreterVorhanden(Urlaubseintrag eintrag) throws NotImplementedException {
        throw new NotImplementedException("kommt noch");
    }
}
