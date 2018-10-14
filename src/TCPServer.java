import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * @author Janni on 27. sep. 2018
 */
public class TCPServer {

    public static void main(String[] args) {
        ArrayList<Client> activeClients = new ArrayList<>();
        ArrayList<String> usernames = new ArrayList<>();
        System.out.println("=============SERVER==============");
        try {
            ServerSocket serverSocket = new ServerSocket(5656);

            while (true) {
                System.out.println("Trying to connect to client");
                checkActiveClients(activeClients, usernames);
                acceptClient(serverSocket, activeClients, usernames);
            }
        } catch (Exception e) {

        }
    }

    public static void acceptClient(ServerSocket serverSocket, ArrayList activeClients, ArrayList usernames) {
        Socket socket;
        String clientIp;

        try {

            System.out.println("Server starting...\n");
            Client client = new Client();

            //Accepting client
            socket = serverSocket.accept();
            System.out.println("Client connected");
            client.setSocket(socket);

            //Client IP address
            clientIp = socket.getInetAddress().getHostAddress();
            System.out.println("IP: " + clientIp);
            System.out.println("PORT: " + socket.getPort());
            client.setClientIp(clientIp);

            Thread createUsername = new Thread(() -> {
                InputStream input = null;
                OutputStream output = null;
                try {
                    input = socket.getInputStream();
                    output = socket.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                client.setInput(input);
                client.setOutput(output);

                byte[] usernameIn = new byte[1024];
                try {
                    input.read(usernameIn);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String username = new String(usernameIn);
                int endUsername = username.indexOf(",");
                username = username.substring(5, endUsername);
                username = username.trim();
                System.out.println(username);

                while (!username.matches("(?=.{1,12}$)[a-zA-Z0-9_-]+") || username.matches("[' ']") || usernames.contains(username)) {
                    usernameIn = new byte[1024];
                    try {
                        input.read(usernameIn);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    username = new String(usernameIn);
                    endUsername = username.indexOf(",");
                    username = username.substring(5, endUsername);
                    username = username.trim();
                    System.out.println(username);
                    if (username.matches("(?=.{1,12}$)[a-zA-Z0-9_-]+") && !username.matches("[' ']") && !usernames.contains(username)) {
                        break;
                    }
                    String msgToSend = "J_ER username not accepted: Only 12 chars long and only letters, digits, hyphen and underscore are allowed";
                    try {
                        output.write(msgToSend.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                client.setUsername(username);
                String validUsername = "J_OK";
                try {
                    output.write(validUsername.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                long timestamp = System.currentTimeMillis();

                //Create client and add to active clients
                activeClients.add(client);
                usernames.add(username);

                sendActiveClientList(activeClients, usernames);

                //Create thread with client
                Thread thread = new Thread(() ->{
                    try {
                        do {
                            //Message received from user
                            byte[] dataIn = new byte[1024];
                            client.getInput().read(dataIn);
                            String msgIn = new String(dataIn);
                            msgIn = msgIn.trim();

                            if (msgIn.substring(0,4).equals("DATA")) {
                                //Message split at ':', check message after ':'
                                int splitMsg = msgIn.indexOf(":");
                                msgIn = msgIn.substring(splitMsg + 2);
                            }


                            //Send message from one user to all users if not 'quit' or 'IMAV'
                            if(!msgIn.equals("QUIT") && !msgIn.equals("IMAV") && msgIn.length() <= 250) {
                                msgIn = client.getUsername() + ": " + msgIn;
                                System.out.println(msgIn);
                                TCPServer.sendMessageToAll(msgIn, client.getUsername(), activeClients);
                            }else if(msgIn.equals("QUIT")){
                                //If message is quit, close socket and break loop.
                                client.getSocket().close();
                                client.getInput().close();
                                client.getOutput().close();
                                TCPServer.removeUser(client.getUsername(), activeClients, usernames);
                                break;
                            }else if(msgIn.equals("IMAV")){
                                long currentTimestamp = System.currentTimeMillis();
                                TCPServer.checkImAlive(currentTimestamp, client.getUsername(), activeClients);
                            }else if(msgIn.length() > 250){
                                msgIn = msgIn.substring(0, 249);
                                TCPServer.sendMessageToAll(msgIn, client.getUsername(), activeClients);
                            }
                        } while (true);

                    }catch (Exception e){}
                });
                thread.start();
            });
            createUsername.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void checkActiveClients(ArrayList activeClients, ArrayList usernames) {
        Thread checkActiveClients = new Thread(() -> {
            ArrayList<Client> temp = activeClients;
            while (true) {
                try {
                    Thread.sleep(100000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < temp.size(); i++) {
                    long timestampNow = System.currentTimeMillis();
                    long timeAlive = (timestampNow - temp.get(i).getImAlive()) / 1000;
                    if (timeAlive > 100) {
                        try {
                            removeUser(temp.get(i).getUsername(), temp, usernames);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        checkActiveClients.start();
    }

    public static void sendActiveClientList(ArrayList activeClients, ArrayList usernames){
        ArrayList<Client> temp = activeClients;
        String newUser = "LIST " + usernames;
        for (int i = 0; i < activeClients.size(); i++) {
            try {
                OutputStream outputStream = temp.get(i).getSocket().getOutputStream();
                outputStream.write(newUser.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendMessageToAll(String msgToSend, String username, ArrayList activeClients) {
        Thread sendMsgToAll = new Thread(() -> {
            ArrayList<Client> temp = activeClients;
            try {
                //Send message to all clients but it self
                for (int i = 0; i < activeClients.size(); i++) {
                    if (!username.equals(temp.get(i).getUsername())) {
                        temp.get(i).getOutput().write(msgToSend.getBytes());
                    }
                }
            } catch (IOException e) {
            }
        });
        sendMsgToAll.start();
    }

    public static void checkImAlive(long timestamp, String username, ArrayList activeClients) {
        ArrayList<Client> temp = activeClients;
        for (Client client : temp) {
            if (client.getUsername().equals(username)) {
                long tempTime = client.getImAlive();
                long secondsAlive = (timestamp - tempTime) / 1000;
                if (secondsAlive < 100) {
                    client.setImAlive(timestamp);
                }
            }
        }
    }

    public static void removeUser(String username, ArrayList activeClients, ArrayList usernames) throws IOException {
        ArrayList<Client> tempActiveClients = activeClients;
        for (Client client : tempActiveClients) {
            if (username.equals(client.getUsername())) {
                activeClients.remove(client);
                break;
            }
        }
        ArrayList<String> tempUsernames = usernames;
        for (String user : tempUsernames) {
            if (username.equals(user)) {
                usernames.remove(user);
                break;
            }

        }
        for (Client client : tempActiveClients) {
            String msgToSend = "LIST " + usernames;
            client.getOutput().write(msgToSend.getBytes());
        }

    }


}