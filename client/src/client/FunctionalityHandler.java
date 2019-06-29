package client;

import javax.sql.rowset.CachedRowSet;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.Date;

/**
 * @author Jens Henning Lehmann 213172003
 */
public interface FunctionalityHandler extends Remote {
    boolean login(int mitarbeiterID, String pwHash) throws RemoteException;
    void logout() throws RemoteException;
    String urlaubEintragen(Date antrag_beginn, Date antrag_ende) throws RemoteException;
    String urlaubGenehmigen(int mitarbeiterId, Date begin, Date ende) throws RemoteException;
    CachedRowSet getNichtGenehmigteUrlaubsTage(int mitarbeiterId) throws RemoteException;
    String urlaubLoeschen(int mitarbeiterId, Date begin, Date ende) throws RemoteException;
    void connect();
}
