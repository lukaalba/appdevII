package server;


import client.FunctionalityHandler;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Main extends FunctionalityHandlerImpl{
    public static void main(String[] args) {
        try {
            FunctionalityHandlerImpl obj = new FunctionalityHandlerImpl();

            FunctionalityHandler stub = (FunctionalityHandler) UnicastRemoteObject.exportObject(obj, 0);

            Registry registry = LocateRegistry.getRegistry();

            registry.bind("FunctionalityHandler", stub);
            System.out.println("Server steht bereit.");
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }
    }
}
