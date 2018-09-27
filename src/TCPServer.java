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

        while (true){
            System.out.println("");
        }
    }
}
