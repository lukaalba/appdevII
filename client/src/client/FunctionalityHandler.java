package client;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Jens Henning Lehmann 213172003
 */
public interface FunctionalityHandler extends Remote {
    String login(int mitarbeiterID, String pwHash) throws RemoteException;
    void logout() throws RemoteException;
    String urlaubEintragen() throws RemoteException;
    String urlaubGenehmigen(int urlaubsID) throws RemoteException;
}
