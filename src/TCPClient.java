import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

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

            do {
                System.out.println("Enter username");
                username = sc.nextLine();
            }while (!username.matches("(?=.{1,12}$)[a-åA-Å0-9_]+(-?)") || username.matches("[' ']"));

            byte[] sendUsername = username.getBytes();
            output.write(sendUsername);

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
            }while (!msgToSend.equalsIgnoreCase("quit"));

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}