package client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main {
    public static void main(String[] args) {
        try {
//            Registry registry = LocateRegistry.getRegistry(null);

//            FunctionalityHandler handerStub = (FunctionalityHandler) registry.lookup("FunctionalityHandler");
            FunctionalityHandler stub = (FunctionalityHandler) Naming.lookup("rmi://localhost:1666/stub");
            stub.logout();


        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
