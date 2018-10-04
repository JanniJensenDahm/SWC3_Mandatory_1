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

            //get join method

            byte[] usernameIn = new byte[1024];
            input.read(usernameIn);
            username = new String(usernameIn);
            int endUsername = username.indexOf(",");
            username = username.substring(5, endUsername);
            username = username.trim();
            System.out.println(username);

            while (!username.matches("(?=.{1,12}$)[a-åA-Å0-9_-]+") || username.matches("[' ']")) {
                String msgToSend = "J_ER username not accepted: Only 12 chars long and only letters, digits, hyphen and underscore are allowed";
                byte[] dataToSend = msgToSend.getBytes();
                output.write(dataToSend);
            }

            String validUsername = "J_OK";
            byte[] usernameValid = validUsername.getBytes();
            output.write(usernameValid);

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