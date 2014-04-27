
import java.net.*;
import java.io.*;
import java.util.Scanner;

/**
 * UDP Client - Connectionless, no error checking data packets are limited to
 * 2^16 bytes minus headers applications: video, skype
 *
 * @author CS467
 */
public class Client {

    private final int buffSize = 1024;
    private static DatagramSocket sock;
    private static String request;
    private static String response;
    private static InetAddress serverAddr;
    private int serverPort;
    private static boolean keepAlive = true;
    private static final boolean DEBUG = false;
    private static final String[] COMMANDS = {"history", "answer", "numtries", "reset"};
    private static int tryNumber;

    public Client(DatagramSocket s, String sName, int sPort) throws UnknownHostException {
        sock = s;
        serverAddr = InetAddress.getByName(sName); //Gets host ip address from host name
        serverPort = sPort;
    }

    public void makeRequest(String data) {
        request = data;
    }

    /**
     * Determines if a string is a admin command.
     *
     * @param data The string to compare against admin commands.
     * @return boolean T/F
     */
    public static boolean isCommand(String data) {
        for (int i = 0; i < COMMANDS.length; i++) {
            if (data.equalsIgnoreCase(COMMANDS[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sends a datagram request to the specified ip address on the specified port.
     */
    public void sendRequest() {
        try {
            byte[] sendBuff = request.getBytes(); //Creates a byte array to store the outgoing datagram.
            DatagramPacket sendPacket = new DatagramPacket(sendBuff, sendBuff.length, serverAddr, serverPort); //Constructs a datagram packet using the buffer, its size, ip address and port number.
            sock.send(sendPacket); //Sends the datagram packet over the socket.
        } catch (Exception ex) {
            System.err.println("Exception in getRequest");
            ex.printStackTrace();
        }
    }

    /**
     * Blocking call that waits for a datagram response.
     */
    public void getResponse() {
        try {
            byte[] recvBuff = new byte[buffSize]; //Creates a byte array to store the incoming datagram.
            DatagramPacket recvPacket = new DatagramPacket(recvBuff, buffSize); //Constructs a datagram packet using the buffer along with its size.
            sock.receive(recvPacket); //Blocking call that waits until it receives a datagram. It stores the data in the buffer.
            response = new String(recvBuff).trim(); //Creates a string from the buffer data.
        } catch (Exception ex) {
            System.err.println("Exception in getResponse");
            ex.printStackTrace();
        }
    }

    /**
     * Parses the datagram string and determines what action to take.
     */
    public void useResponse() {
        if (DEBUG) {
            System.out.println("\tRequest:  " + request + "\n\tResponse: " + response);
        }
        String[] data = response.split(",");
        if (data[0].equalsIgnoreCase("state")) {
            tryNumber = Integer.parseInt(response.split(",")[3]);
        } else if (data[0].equalsIgnoreCase("guess")) {
            tryNumber = Integer.parseInt(response.split(",")[3]);
            System.out.println("\t" + data[1]);
            checkEndGame(data);
        } else if (data[0].equalsIgnoreCase("history")) {
            if (data.length == 1) {
                System.out.println("\tNo History");
            }
            for (int i = 1; i < data.length; i++) {
                System.out.println("\t" + i + ". " + data[i]);
            }
        } else if (data[0].equalsIgnoreCase("answer")) {
            System.out.println("\tAnswer: " + data[1]);
        } else if (data[0].equalsIgnoreCase("numtries")) {
            System.out.println("\tGuesse(s) Remaining: " + (11 - Integer.parseInt(data[1])));
        } else if (data[0].equalsIgnoreCase("reset")) {
            System.out.println("\nA new pattern has been generated!");
        } else if (data[0].equalsIgnoreCase("set")) {
            System.out.println("\nThe pattern is now " + data[1]);
        }else if (data[0].equalsIgnoreCase("error")){
            System.out.println("\nA bad request was sent to the server.\nWe are going to quit before we break it more.");
            keepAlive = false;
        } else {
            System.out.println("\tUnrecognized command");
        }
    }

    public void close() {
        sock.close(); //Closes the datagram socket.
    }

    /**
     * Gets the user input and determines the appropriate action.
     *
     * @return
     */
    public static String getInput() {
        while (true) {
            Scanner input = new Scanner(System.in);
            System.out.print("Guess " + tryNumber + ": ");
            String data = input.nextLine().trim().toLowerCase();

            if (isCommand(data)) {
                return data;
            } else if (data.startsWith("set ")) {
                String pattern = data.split(" ")[1].trim();
                if (pattern.matches("[a-f]{4}")) {
                    return "set," + pattern;
                } else {
                    System.out.println("\tPatterns are 4 characters long and contain the letters a-f.");
                }
            } else if (data.length() == 4) {
                if (data.matches("[a-f]{4}")) {
                    return "guess," + data;
                } else {
                    System.out.println("\tPatterns are 4 characters long and contain the letters a-f.");
                }
            } else {
                System.out.println("\tBad command / Patterns are 4 characters long and contain the letters a-f.");
            }
        }
    }

    /**
     * Starts the UDP Client
     *
     * @param args - contains your command line arguments
     * @throws IOException
     */
    public static void main(String[] args) throws IOException, SocketException {
        final String servName = (args.length > 1 && args[1] != null)
                ? args[1] : "localhost";

        final int servPort = (args.length > 2 && args[2] != null && Integer.parseInt(args[2]) > 0)
                ? Integer.parseInt(args[2]) : 12345;

        sock = new DatagramSocket(); //Creates a new UDP socket
        Client client = new Client(sock, servName, servPort); //Constructor call
        System.out.println("UDP client contacting " + servName + " on port " + servPort);
        System.out.println("\nWelcome to Mastermind");

        //Get the initial state of the game. Allows joining of a game that was not finished.
        client.makeRequest("state");
        client.sendRequest();
        client.getResponse();
        client.useResponse();

        //Game loop
        while (keepAlive) {
            client.makeRequest(getInput());
            client.sendRequest();
            client.getResponse();
            client.useResponse();
        }

        client.close();
    }

    /**
     * Checks for the end conditions of the game, out of tries or guessed
     * correctly. It then displays the appropriate message.
     *
     * @param data
     */
    private void checkEndGame(String[] data) {
        if (data[2].equalsIgnoreCase("outOfGuesses")) {
            System.out.println("You are out of guesses. \nThe pattern was: " + data[5]);
            System.out.println("\nWaiting for next Pattern");
        } else if (data[2].equalsIgnoreCase("patternMatched")) {
            System.out.println("You Matched the Pattern!");
            System.out.println("\nNew pattern generated!");
        }
    }
}
