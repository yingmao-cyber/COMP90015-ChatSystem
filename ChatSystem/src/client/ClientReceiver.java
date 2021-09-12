package client;

import client_command.AskAckCommand;
import client_command.ClientCommand;
import client_command.MessageRelayCommand;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientReceiver extends Thread{
    private Socket socket;
    private ChatClient chatClient;
    private CommandFactory commandFactory;
    private BufferedReader reader;
    private boolean connection_alive;
    private PrintWriter writer;
    private Gson gson;

    public ClientReceiver(ChatClient chatClient) throws IOException {
        this.gson = new Gson();
        this.connection_alive = true;
        this.chatClient = chatClient;
        this.socket = chatClient.getSocket();
        this.commandFactory = new CommandFactory();
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF8"));
        this.writer = new PrintWriter(this.socket.getOutputStream(), true);
    }

    public void setConnection_alive(boolean connection_alive) {
        this.connection_alive = connection_alive;
    }

    public void close() throws IOException {
        this.connection_alive = false;
        this.reader.close();
        this.writer.close();
    }

    /**
     * Receives messages from server
     */
    public void run(){
        while (connection_alive) {
            try {
                String str = reader.readLine();
                String type = gson.fromJson(str, JsonObject.class).get("type").getAsString();
                if (type.equals("ack")){
                    AskAckCommand askAckCommand = gson.fromJson(str, AskAckCommand.class);
                    String returnCommand = askAckCommand.executeAckCommand(chatClient);
                    this.writer.println(returnCommand);
                } else if (str != null){
                    ClientCommand command = commandFactory.convertServerMessageToCommand(str);
//                    System.out.println("receive: " + str);
                    if (command != null){
                        command.execute(chatClient);
                    }
                }
            } catch (IOException e) {
                this.connection_alive = false;
                e.printStackTrace();
            }
        }

        try {
            this.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
