import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author Janni on 27. sep. 2018
 */
public class TCPServer {
    public static void main(String[] args) {

        System.out.println("=============SERVER==============");

        try {
            ServerSocket serverSocket = new ServerSocket(5656);
            Socket socket;

            while (true) {
                System.out.println("Trying to connect to client");
                acceptClient(serverSocket);
                /*DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

                //

                //Create new client
                Client client = new Client();*/
            }
        }catch (Exception e){

        }
    }

    public static void acceptClient(ServerSocket serverSocket){
        Socket socket;
        String clientIp;
        String username;
        ArrayList<Client> activeClients = new ArrayList<>();

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

            //Create username max 12 chars, only letters, digits, hyphen (-) and underscore (_) allowed
            //As long as it
            do {
                byte[] usernameIn = new byte[1024];
                input.read(usernameIn);
                username = new String(usernameIn);
                username = username.trim();
            }while (!username.matches("(?=.{1,12}$)[a-åA-Å0-9_]+(-?)") || username.matches("[' ']") /*|| activeClients.contains(username)*/);

            //Create client and add to active clients
            Client client = new Client(socket, clientIp, username, input, output);
            activeClients.add(client);


            //Create thread with client
            Thread thread = new Thread(client);
            thread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}