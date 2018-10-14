import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author Janni on 01. okt. 2018
 */
public class Client {
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
