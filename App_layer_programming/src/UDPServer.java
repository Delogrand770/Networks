
import java.net.*;
import java.io.*;

/**
 * UDP Server
 *
 * @author CS467
 */
public class UDPServer {

    private final int buffSize = 1024;
    private DatagramSocket sock;
    private String request;
    private String response;
    private InetAddress clientAddr;
    private int clientPort;

    public UDPServer(DatagramSocket s) {
        sock = s;
    }

    public void getRequest() {
        try {
            byte[] recvBuff = new byte[buffSize];
            DatagramPacket recvPacket = new DatagramPacket(recvBuff, buffSize);
            sock.receive(recvPacket); //blocking function
            recvBuff = recvPacket.getData();
            request = new String(recvBuff).trim();  // The trim here removes the extra spaces (since your string probably isn't as big as your buffer
            clientAddr = recvPacket.getAddress();
            clientPort = recvPacket.getPort();
        } catch (Exception ex) {
            System.err.println("Exception in getRequest");
            ex.printStackTrace();
        }
    }

    public void process() {
        // Add code for processing the request and creating the response
        response = "UDP Server received " + request;
    }

    public void sendResponse() {
        try {
            byte[] sendBuff = response.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendBuff, sendBuff.length, clientAddr, clientPort);
            sock.send(sendPacket);
        } catch (Exception ex) {
            System.err.println("Exception in sendResponse");
            ex.printStackTrace();
        }
    }

    /**
     * Starts the UDP Server
     *
     * @param args - contains your command line arguments
     * @throws IOException
     */
    public static void main(String[] args) throws IOException, SocketException {
        final int port = 6789;
        DatagramSocket sock = new DatagramSocket(port);
        UDPServer server = new UDPServer(sock);

        System.out.println("UDP Server online and listening on port " + port);

        while (true) {
            System.out.print("Waiting . . . ");
            server.getRequest();
            System.out.println("RECEIVED A DATAGRAM");
            server.process();
            server.sendResponse();
        }
    }
}
