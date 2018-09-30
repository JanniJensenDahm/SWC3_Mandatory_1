import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author Janni on 27. sep. 2018
 */
public class TCPServer {

    public static void main(String[] args) throws Exception {
        String message;
        String userText;

        ServerSocket socket = new ServerSocket(5656);
        Scanner fromUser = new Scanner(new InputStreamReader(System.in));

        System.out.println("Waiting for connection");
        Socket connectionSocket = socket.accept();
        Scanner fromClient = new Scanner(new InputStreamReader(connectionSocket.getInputStream()));
        DataOutputStream toClient = new DataOutputStream(connectionSocket.getOutputStream());
        message = fromClient.nextLine();
        System.out.println(message);

        while (true) {
            //Let server type message
            System.out.println("Message: ");
            userText = fromUser.nextLine();
            toClient.writeBytes(userText + '\n');

            //If message is 'quit', close connection
            if (userText.equalsIgnoreCase("quit")) {
                break;
            }

            //Get message from client
            message = fromClient.nextLine();
            System.out.println(message);

            //If message is 'quit', close connection
            if (message.equalsIgnoreCase("quit")) {
                break;
            }
        }

        System.out.println("Connection closed");
        connectionSocket.close();
        socket.close();
    }
}
