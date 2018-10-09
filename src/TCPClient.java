import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * @author Janni on 27. sep. 2018
 */
public class TCPClient {
    static InputStream input;
    static OutputStream output;
    static Socket socket;

    public static void main(String[] args) {
        String msgToSend;
        final String IP_SERVER_STR = "127.0.0.1";
        final int PORT_SERVER = 5656;
        String username;
        System.out.println("=============CLIENT==============");

        Scanner sc = new Scanner(System.in);


        try {
            InetAddress ip = InetAddress.getByName(IP_SERVER_STR);

            System.out.println("\nConnecting...");
            System.out.println("SERVER IP: " + IP_SERVER_STR);
            System.out.println("SERVER PORT: " + PORT_SERVER + "\n");

            socket = new Socket(ip, PORT_SERVER);

            input = socket.getInputStream();
            output = socket.getOutputStream();

            while (true){
                System.out.println("Enter username");
                username = sc.nextLine();

                String joinUser = "JOIN " + username + ", " + IP_SERVER_STR + ":" + PORT_SERVER;
                byte[] sendJoin = joinUser.getBytes();
                output.write(sendJoin);

                byte[] validatedUsername = new byte[1024];
                input.read(validatedUsername);
                String usernameValid = new String(validatedUsername);
                usernameValid = usernameValid.trim();
                System.out.println(usernameValid);

                if(usernameValid.equalsIgnoreCase("J_OK")){
                    break;
                }
            }
            imAlive();
            sendMessageToServer();

            do {
                //Userinput
                sc = new Scanner(System.in);
                msgToSend = "DATA " + username + ": " + sc.nextLine();

                //Send message to server
                output.write(msgToSend.getBytes());

            } while (!msgToSend.equalsIgnoreCase("quit"));

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Sends message to server
    public static void sendMessageToServer(){
        Thread sendMessageToAll = new Thread(() -> {
            while (true) {
                byte[] inputFromServer = new byte[1024];
                try {
                    input.read(inputFromServer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String usernameValid = new String(inputFromServer);
                usernameValid = usernameValid.trim();
                System.out.println(usernameValid);
            }
        });
        sendMessageToAll.start();
    }

    //Sends IMAV to server every 60 sec
    public static void imAlive(){
        Thread imAlive = new Thread(() -> {
            while (true){
                try {
                    Thread.sleep(60_000);
                    output.write("IMAV".getBytes());
                }catch (InterruptedException e){}
                catch (IOException e){}
            }
        });
        imAlive.start();
    }
}