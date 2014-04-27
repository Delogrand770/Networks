package ex3;


import java.net.*;
import java.io.*;

/**
 * TCP Server (used to demonstrate object DEserialization)
 * @author CS467
 */
public class TCPServer 
{
    private Socket       sock;
    private InputStream  recvStream;
    private OutputStream sendStream;
    private String       request;
    private String       response;
    
    public TCPServer(Socket s) throws IOException, UnknownHostException
    {
        sock       = s;
        recvStream = sock.getInputStream();
        sendStream = sock.getOutputStream();
    }
    
    public void getRequest()
    {
        try
        {
            int dataSize;
            while ((dataSize = recvStream.available()) == 0);
            byte[] recvBuff = new byte[dataSize];
            recvStream.read(recvBuff);
            request = new String(recvBuff);
        }
        catch (IOException ex)
        {
            System.err.println("IOException in getRequest");
        }
    }
    
    public void process()
    {
        // Code to Process the Request Goes Here
    	response = "TCP Server received " + request;
    }
    
    public void sendResponse()
    {
        try
        {
            byte[] sendBuff = new byte[response.length()];
            sendBuff = response.getBytes();
            sendStream.write(sendBuff);
        }
        catch (IOException ex)
        {
            System.err.println("IOException in sendResponse");
        }
    }
    
    public void close()
    {
        try
        {
            recvStream.close();
            sendStream.close();
            sock.close();
        }
        catch (IOException ex)
        {
            System.err.println("IOException in close");
        }
    }
    
    /**
     * Starts the TCP Server
     * @param args - contains your command line arguments
     * @throws IOException
     */
    public static void main(String[] args) throws IOException
    {      
        int port = 6789;
        
        // Creates the Server Socket that Listens for Connections
        ServerSocket listenSock = new ServerSocket(port);
        
        System.out.println("TCP Server online and listening on port " + port);
        
        // This is why we say that a server program is infinite
        while (true)
        {
        	System.out.print("I'm waiting for a connection . . . ");
            TCPServer server = new TCPServer(listenSock.accept());
            System.out.println(" CLIENT CONNECTED.");
            
            server.getRequest();
            server.process();
            server.sendResponse();
            server.close();
        }
    }
}
