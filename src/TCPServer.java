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
    static Client client = new Client();

    public static void main(String[] args) {

        System.out.println("=============SERVER==============");
        try {
            ServerSocket serverSocket = new ServerSocket(5656);

            while (true) {
                System.out.println("Trying to connect to client");
                checkActiveClients();
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
                if (username.matches("(?=.{1,12}$)[a-zA-Z0-9_-]+") && !username.matches("[' ']") && !usernames.contains(username)) {
                    break;
                }
                msgToSend = "J_ER username not accepted: Only 12 chars long and only letters, digits, hyphen and underscore are allowed";
                output.write(msgToSend.getBytes());

            }
            while (!username.matches("(?=.{1,12}$)[a-zA-Z0-9_-]+") || username.matches("[' ']") || usernames.contains(username));

            String validUsername = "J_OK";
            output.write(validUsername.getBytes());

            long timestamp = System.currentTimeMillis();

            //Create client and add to active clients
            client = new Client(socket, clientIp, username, input, output, timestamp);
            activeClients.add(client);
            usernames.add(username);

            String newUser = "LIST " + usernames;
            for (Client client : activeClients) {
                client.getOutput().write(newUser.getBytes());
            }

            //Create thread with client
            Thread thread = new Thread(client);
            thread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void checkActiveClients() {
        Thread checkActiveClients = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(100000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (Client client : activeClients) {
                    long timestampNow = System.currentTimeMillis();
                    long timeAlive = (timestampNow - client.getImAlive()) / 1000;
                    if (timeAlive > 100) {
                        try {
                            removeUser(client.getUsername());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        checkActiveClients.start();
    }

    public static void sendMessageToAll(String msgToSend, String username) {
        Thread sendMsgToAll = new Thread(() -> {
            try {
                //Send message to all clients but it self
                for (Client client : activeClients) {
                    if (!username.equals(client.getUsername())) {
                        client.getOutput().write(msgToSend.getBytes());
                    }
                }
            } catch (IOException e) {
            }
        });
        sendMsgToAll.start();
    }

    public static void checkImAlive(long timestamp, String username) {
        for (Client client : activeClients) {
            if (client.getUsername().equals(username)) {
                long tempTime = client.getImAlive();
                long secondsAlive = (timestamp - tempTime) / 1000;
                if (secondsAlive < 100) {
                    client.setImAlive(timestamp);
                }
            }
        }
    }

    public static void removeUser(String username) throws IOException {
        System.out.println("Linje 149");
        for (Client client : activeClients) {
            System.out.println("Linje 151");
            if (username.equals(client.getUsername())) {
                activeClients.remove(client);
                System.out.println("linje154");
                System.out.println(usernames);
            }
        }
        System.out.println(usernames);
        for (String user : usernames) {
            System.out.println("linje 158");
            if (username.equals(user)) {
                System.out.println("linje 160");
                usernames.remove(user);
            }

        }
        for (Client client : activeClients) {
            System.out.println("Linje 166");
            String msgToSend = "LIST" + usernames;
            client.getOutput().write(msgToSend.getBytes());
        }

    }


}