package ex1;


/**
 * Demonstrates how to create and use threads
 *
 * @author CS467
 *
 */
public class ThreadExample {

    /**
     * Constructor
     */
    public ThreadExample() {
        RunnableCounter a = new RunnableCounter("a");
        RunnableCounter b = new RunnableCounter("b");

        Thread c = new Thread(a);
        Thread d = new Thread(b);
        
        c.start();
        d.start();
    }

    /**
     * This starts the whole program!
     *
     * @param args
     */
    public static void main(String[] args) {
        new ThreadExample();
    }
}
