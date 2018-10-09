import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author Janni on 27. sep. 2018
 */
public class TCPServer {
    static ArrayList<Client> activeClients = new ArrayList<>();
    static ArrayList<String> usernames = new ArrayList<>();
    static Client client;


    public static void main(String[] args) {

        System.out.println("=============SERVER==============");
        try {
            ServerSocket serverSocket = new ServerSocket(5656);

            while (true) {
                System.out.println("Trying to connect to client");
                acceptClient(serverSocket);
            }
        } catch (Exception e) {

        }
    }

    public static void acceptClient(ServerSocket serverSocket) {
        Socket socket;
        String clientIp;
        String username;
        String msgToSend;

        try {

            System.out.println("Server starting...\n");

            //Accepting client
            socket = serverSocket.accept();
            System.out.println("Client connected");

            //Client IP address
            clientIp = socket.getInetAddress().getHostAddress();
            System.out.println("IP: " + clientIp);
            System.out.println("PORT: " + socket.getPort());

            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();

            //get join method

            do {
                byte[] usernameIn = new byte[1024];
                input.read(usernameIn);
                username = new String(usernameIn);
                int endUsername = username.indexOf(",");
                username = username.substring(5, endUsername);
                username = username.trim();
                System.out.println(username);
                if (username.matches("(?=.{1,12}$)[a-zA-Z0-9_-]+") && !username.matches("[' ']") && !usernames.contains(username)){
                    break;
                }
                msgToSend = "J_ER username not accepted: Only 12 chars long and only letters, digits, hyphen and underscore are allowed";
                output.write(msgToSend.getBytes());

            }while (!username.matches("(?=.{1,12}$)[a-zA-Z0-9_-]+") || username.matches("[' ']") || usernames.contains(username));

            String validUsername = "J_OK";
            output.write(validUsername.getBytes());


            //Create client and add to active clients

            client = new Client(socket, clientIp, username, input, output);
            activeClients.add(client);
            usernames.add(username);

            String newUser = "LIST " + usernames;
            output.write(newUser.getBytes());

            //Create thread with client
            Thread thread = new Thread(client);
            thread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendMessageToAll(String msgToSend, String username){
        Thread sendMsgToAll = new Thread(() -> {
            try {
                //Send message to all clients but it self
                if(!msgToSend.equals("quit") && !msgToSend.equals("IMAV"))
                for (Client client : activeClients) {
                    if (!username.equals(client.getUsername())) {
                        client.getOutput().write(msgToSend.getBytes());
                    }
                }
            }catch (IOException e){}
        });
        sendMsgToAll.start();
    }

    public static void removeUser(String username){
        for(int i = 0; i < activeClients.size(); i++) {
            if (username.equals(client.getUsername())){
                activeClients.remove(client);
                break;
            }
        }
    }

}