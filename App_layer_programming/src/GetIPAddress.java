
import java.net.*;
import java.io.*;

/**
 * InetAddress Demonstration
 *
 * @author CS467
 */
public class GetIPAddress {

    public static void main(String[] args) throws UnknownHostException {
        // Example One:  Specify the IP Address Yourself
        InetAddress exampleOne = InetAddress.getByName("128.236.40.20");
        System.out.println(exampleOne);
        // Example Two:  Specify the Name, and Have DNS Look it Up
        InetAddress exampleTwo = InetAddress.getByName("www.google.com");
        System.out.println(exampleTwo);
        // Example Three:  Specify the IP Address, and Get the Host Name
        InetAddress exampleThree = InetAddress.getByName("128.236.40.20");
        System.out.println(exampleThree.getHostName());
        // Example Four:  Get the Address for the Local Host
        InetAddress exampleFour = InetAddress.getLocalHost();
        System.out.println(exampleFour.getHostName());
    }
}
