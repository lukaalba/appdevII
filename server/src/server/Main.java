package server;


import client.FunctionalityHandler;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Main {
    private static int port = 1666;
    public static void main(String[] args) {
        try {
            FunctionalityHandlerImpl obj = new FunctionalityHandlerImpl();
            Registry reg;
            reg = LocateRegistry.createRegistry(port);
            String url = "rmi://localhost:" + port + "/stub";
            System.out.println("Dienst erreichbar unter: " + url);
            Naming.bind(url, obj);

//            FunctionalityHandler stub = (FunctionalityHandler) UnicastRemoteObject.exportObject(obj, port);

//            Registry registry = LocateRegistry.getRegistry();

//            registry.bind("FunctionalityHandler", stub);
            System.out.println("Server steht bereit.");
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            System.out.println("Ein Fehler ist aufgetreten. Die Fehlerbeschreibung lautet: " + e.getStackTrace().toString());
        }
    }
}
