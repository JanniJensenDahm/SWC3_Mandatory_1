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
    private long imAlive;

    public Client(){

    }

    public Client(Socket socket, String clientIp, String username, InputStream input, OutputStream output, long timestamp) {
        this.socket = socket;
        this.clientIp = clientIp;
        this.username = username;
        this.input = input;
        this.output = output;
        this.imAlive = timestamp;
    }

    @Override
    public void run(){
        try {
            do {
                //Message received from user
                byte[] dataIn = new byte[1024];
                input.read(dataIn);
                String msgIn = new String(dataIn);
                msgIn = msgIn.trim();

                if (msgIn.substring(0,4).equals("DATA")) {
                    //Message split at ':', check message after ':'
                    int splitMsg = msgIn.indexOf(":");
                    msgIn = msgIn.substring(splitMsg + 2);
                }


                //Send message from one user to all users if not 'quit' or 'IMAV'
                if(!msgIn.equals("QUIT") && !msgIn.equals("IMAV") && msgIn.length() <= 250) {
                    msgIn = username + ": " + msgIn;
                    System.out.println(msgIn);
                    TCPServer.sendMessageToAll(msgIn, username);
                }else if(msgIn.equals("QUIT")){
                    //If message is quit, close socket and break loop.
                    getSocket().close();
                    input.close();
                    output.close();
                    TCPServer.removeUser(username);
                    break;
                }else if(msgIn.equals("IMAV")){
                    long timestamp = System.currentTimeMillis();
                    TCPServer.checkImAlive(timestamp, username);
                }else if(msgIn.length() > 250){
                    msgIn = msgIn.substring(0, 249);
                    TCPServer.sendMessageToAll(msgIn, username);
                }
            } while (true);

        }catch (Exception e){}
    }

    @Override
    public String toString() {
        return username + ", " + socket;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public InputStream getInput() {
        return input;
    }

    public void setInput(InputStream input) {
        this.input = input;
    }

    public OutputStream getOutput() {
        return output;
    }

    public void setOutput(OutputStream output) {
        this.output = output;
    }

    public long getImAlive() {
        return imAlive;
    }

    public void setImAlive(long imAlive) {
        this.imAlive = imAlive;
    }
}
