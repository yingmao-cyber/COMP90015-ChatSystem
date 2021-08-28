package server;

import java.util.ArrayList;

/**
 * ChatManager class is responsible for performing admin tasks including:
 * managing clients (connections), joining room, changing room, etc.
 */
public class ChatManager {
    private ArrayList<ServerSideConnection> clientConnectionList;

    public ChatManager(){
        clientConnectionList = new ArrayList<>();
    }

    public synchronized void addClientConnection(ServerSideConnection connection){
        clientConnectionList.add(connection);
    }

    public  synchronized  void removeClientConnection(ServerSideConnection connection){
        clientConnectionList.remove(connection);
    }

    private synchronized void broadCast(String message, ServerSideConnection ignored){
        for (ServerSideConnection conn: clientConnectionList){
            if (!conn.equals(ignored)){
                conn.sendMessage(message);
            }
        }
    }

}
