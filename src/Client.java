import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author Janni on 01. okt. 2018
 */
public class Client implements Runnable {
    private Socket socket;
    private String username;
    private String clientIp;
    private InputStream input;
    private OutputStream output;


    public Client(Socket socket, String clientIp, String username, InputStream input, OutputStream output) {
        this.socket = socket;
        this.clientIp = clientIp;
        this.username = username;
        this.input = input;
        this.output = output;
    }

    @Override
    public void run(){
        try {
            do {
                byte[] dataIn = new byte[1024];
                input.read(dataIn);
                String msgIn = new String(dataIn);
                msgIn = msgIn.trim();


                System.out.println("IN -->" + msgIn + "<--");

                String msgToSend = username + ": " + msgIn;
                byte[] dataToSend = msgToSend.getBytes();
                output.write(dataToSend);

                if(msgIn.equalsIgnoreCase("quit")){
                    this.socket.close();
                    this.input.close();
                    this.output.close();
                    break;
                }
            } while (true);
        }catch (Exception e){}
    }
}
