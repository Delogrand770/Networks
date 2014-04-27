
import java.net.*;
import java.io.*;

/**
 * UDP Server
 *
 * @author CS467
 */
public class Server {

    private final int buffSize = 1024;
    private DatagramSocket sock;
    private String request;
    private String response;
    private InetAddress clientAddr;
    private int clientPort;
    private static Mastermind puzzle;

    public Server(DatagramSocket s) {
        sock = s;
    }

    /**
     * Blocking call that waits for a datagram packet request.
     */
    public void getRequest() {
        try {
            byte[] recvBuff = new byte[buffSize]; //Creates a byte array to store the incoming datagram.
            DatagramPacket recvPacket = new DatagramPacket(recvBuff, buffSize); //Constructs a datagram packet using the buffer and its size.
            sock.receive(recvPacket); //Blocking call that waits for a datagram packet
            recvBuff = recvPacket.getData(); //Gets the data in the datagram packet.
            request = new String(recvBuff).trim(); //Creates a string from the buffered datagram data.
            clientAddr = recvPacket.getAddress(); //Gets the sending ip address for the datagram packet.
            clientPort = recvPacket.getPort(); //Gets the sending port for the datagram packet.
        } catch (Exception ex) {
            System.err.println("Exception in getRequest");
            ex.printStackTrace();
        }
    }

    /**
     * Parses the datagram string and determines what action to take.
     */
    public void process() {
        System.out.println("\tRecieved: " + request);
        String[] data = request.split(",");

        if (data[0].equalsIgnoreCase("history")) {
            response = puzzle.getSendableHistory();
        } else if (data[0].equalsIgnoreCase("answer")) {
            response = "answer," + puzzle.getSolution();
        } else if (data[0].equalsIgnoreCase("numtries")) {
            response = "numtries," + puzzle.getNumTries();
        } else if (data[0].equalsIgnoreCase("reset")) {
            puzzle = new Mastermind();
            response = "reset";
        } else if (data[0].equalsIgnoreCase("set")) {
            puzzle = new Mastermind(data[1]);
            response = "set," + data[1];
        } else if (data[0].equalsIgnoreCase("state")) {
            response = "state," + "0," + puzzle.getGameState() + "," + puzzle.getNumTries() + "," + puzzle.PUZZLESIZE + "," + puzzle.getSolution();
        } else if (data[0].equalsIgnoreCase("guess")) {
            String correct = puzzle.processGuess(data[1]);
            String gameState = puzzle.getGameState();
            int tries = (gameState.equalsIgnoreCase("patternMatched") || gameState.equalsIgnoreCase("outOfGuesses"))
                    ? 1 : puzzle.getNumTries();
            response = "guess," + correct + "," + puzzle.getGameState() + "," + tries + "," + puzzle.PUZZLESIZE + "," + puzzle.getSolution();
        } else {
            System.out.println("Unknown Request");
            response = "error,Unknown Request";
        }
    }

    /**
     * Sends a datagram response to the specified ip address on the specified
     * port.
     */
    public void sendResponse() {
        try {
            System.out.println("\tResponded: " + response);
            byte[] sendBuff = response.getBytes(); //Creates a byte array to store the outgoing datagram.
            DatagramPacket sendPacket = new DatagramPacket(sendBuff, sendBuff.length, clientAddr, clientPort); //Constructs a datagram packet using the buffer, its size, ip address and port number.
            sock.send(sendPacket); //Sends the datagram packet over the socket.
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
        final int port = (args.length > 1 && args[1] != null && Integer.parseInt(args[1]) > 0)
                ? Integer.parseInt(args[1]) : 12345;

        DatagramSocket sock = new DatagramSocket(port); //Creates a new UDP socket
        Server server = new Server(sock); //Constructor call

        System.out.println("UDP Server online and listening on port " + port);
        puzzle = new Mastermind(); //Creates a new mastermind puzzle instance

        //Server loop that waits for incoming datagrams and advances game state.
        while (true) {
            System.out.println("Waiting for datagram...");
            server.getRequest();
            server.process();
            server.sendResponse();
            String gameState = puzzle.getGameState();
            if (gameState.equalsIgnoreCase("outOfGuesses") || gameState.equalsIgnoreCase("patternMatched")) {
                System.out.println("Game Over!  \n\tReason: " + gameState);
                puzzle = new Mastermind();
            }
        }
    }
}
