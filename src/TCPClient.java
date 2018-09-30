import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author Janni on 27. sep. 2018
 */
public class TCPClient {

    public static void main(String[] args) throws Exception {
        String message;

        Scanner fromUser = new Scanner(new InputStreamReader(System.in));

        System.out.println("Trying to connect");
        Socket clientSocket = new Socket("127.0.0.1", 5656);
        System.out.println("Connection complete");

        DataOutputStream toServer = new DataOutputStream(clientSocket.getOutputStream());
        Scanner fromServer = new Scanner(new InputStreamReader(clientSocket.getInputStream()));

        //System.out.println("Type username");
        //userInput = fromUser.next();

        while (true) {
            //Let user type message
            System.out.println("Type message: ");
            message = fromUser.nextLine();
            toServer.writeBytes(message + '\n');

            //If message is 'quit', close connection
            if (message.equalsIgnoreCase("quit")) {
                break;
            }

            //Get message from server
            message = fromServer.nextLine();
            System.out.println(message);

            //If message is quit, close connection
            if (message.equalsIgnoreCase("quit")) {
                break;
            }
        }
        System.out.println("Connection closed");
        clientSocket.close();
    }
}
