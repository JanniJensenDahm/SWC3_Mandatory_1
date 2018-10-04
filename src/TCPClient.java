import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * @author Janni on 27. sep. 2018
 */
public class TCPClient {

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

            Socket socket = new Socket(ip, PORT_SERVER);

            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();

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

            do {
                sc = new Scanner(System.in);
                msgToSend = sc.nextLine();

                byte[] dataToSend = msgToSend.getBytes();
                output.write(dataToSend);

                byte[] dataIn = new byte[1024];
                input.read(dataIn);
                String msgIn = new String(dataIn);
                msgIn = msgIn.trim();


                System.out.println(msgIn);
            } while (!msgToSend.equalsIgnoreCase("quit"));

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}