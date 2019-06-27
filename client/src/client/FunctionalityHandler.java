package src.client;

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
    String urlaubGenehmigen(int urlaubsID) throws RemoteException;
}
