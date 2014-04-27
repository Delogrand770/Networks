
import java.net.*;
import java.io.*;

/**
 * UDP Client - 
 * Connectionless, no error checking
 * data packets are limited to 2^16 bytes minus headers
 * applications: video, skype
 *
 * @author CS467
 */
public class UDPClient {

    private final int buffSize = 1024;
    private DatagramSocket sock;
    private String request;
    private String response;
    private InetAddress serverAddr;
    private int serverPort;

    public UDPClient(DatagramSocket s, String sName, int sPort) throws UnknownHostException {
        sock = s;
        serverAddr = InetAddress.getByName(sName);
        serverPort = sPort;
    }

    public void makeRequest() {
        // Code to create the request string to be added here
        request = "Hey";
    }

    public void sendRequest() {
        try {
            byte[] sendBuff = request.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendBuff, sendBuff.length, serverAddr, serverPort);
            sock.send(sendPacket);
        } catch (Exception ex) {
            System.err.println("Exception in getRequest");
            ex.printStackTrace();
        }
    }

    public void getResponse() {
        try {
            byte[] recvBuff = new byte[buffSize];
            DatagramPacket recvPacket = new DatagramPacket(recvBuff, buffSize);
            sock.receive(recvPacket);
            response = new String(recvBuff).trim();	// The trim here removes the extra spaces (since your string probably isn't as big as your buffer
        } catch (Exception ex) {
            System.err.println("Exception in getResponse");
            ex.printStackTrace();
        }
    }

    public void useResponse() {
        // Code to use the response string needs to be added here
        System.out.println("Request:  " + request + ", Response:  " + response);
    }

    public void close() {
        sock.close();
    }

    /**
     * Starts the UDP Client
     *
     * @param args - contains your command line arguments
     * @throws IOException
     */
    public static void main(String[] args) throws IOException, SocketException {
        final int servPort = 6789;
        final String servName = "dfcs-brown13";
        DatagramSocket sock = new DatagramSocket();
        UDPClient client = new UDPClient(sock, servName, servPort);

        System.out.println("UDP client contacting " + servName + " on port " + servPort);
        client.makeRequest();
        client.sendRequest();
        client.getResponse();
        client.useResponse();
        System.out.println("Done");
        client.close();
    }
}
