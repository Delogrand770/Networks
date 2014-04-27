
import java.net.*;
import java.io.*;

/**
 * TCP Client
 *
 * @author CS467
 */
public class TCPClient {

    private Socket sock;
    private OutputStream sendStream;
    private InputStream recvStream;
    private String request;
    private String response;

    public TCPClient(String server, int port) throws IOException, UnknownHostException {
        sock = new Socket(server, port);
        sendStream = sock.getOutputStream();
        recvStream = sock.getInputStream();
    }

    public void makeRequest() {
        // Add code to make the request string here
        request = "ABC";
    }

    public void sendRequest() {
        try {
            byte[] sendBuff = new byte[request.length()];
            sendBuff = request.getBytes();
            sendStream.write(sendBuff);
        } catch (Exception ex) {
            System.err.println("Error in sendRequest");
            ex.printStackTrace();
        }
    }

    public void getResponse() {
        try {
            int dataSize;
            while ((dataSize = recvStream.available()) == 0);
            byte[] recvBuff = new byte[dataSize];
            recvStream.read(recvBuff);
            response = new String(recvBuff);
        } catch (Exception ex) {
            System.err.println("Error in getResponse");
            ex.printStackTrace();
        }
    }

    public void useResponse() {
        // Add code to use the response string here
        System.out.println("Received:  " + response);
    }

    public void close() {
        try {
            sendStream.close();
            recvStream.close();
            sock.close();
        } catch (Exception ex) {
            System.err.println("Error in close");
            ex.printStackTrace();
        }
    }

    /**
     * Starts the TCP Client
     *
     * @param args - contains your command line arguments
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        final int servPort = 12345;
        final String servName = "128.236.40.20"; //SmallWorld Server
        TCPClient client = new TCPClient(servName, servPort);

        System.out.println("TCP client contacting " + servName + " on port " + servPort);
        client.makeRequest();
        client.sendRequest();
        client.getResponse();
        client.useResponse();
        client.close();
        System.out.println("Session Complete!  Client Terminated.");
    }
}
