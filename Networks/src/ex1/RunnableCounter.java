package ex1;


/**
 * This is an example of a Runnable object, which can be executed in a thread
 *
 * @author CS467
 *
 */
public class RunnableCounter implements Runnable {
    // This variable is used to keep track of the object
    // it will be useful when we have multiple threads

    private String name;

    /**
     * Constructor for a Runnable Counter Object
     *
     * @param name - the (hopefully unique) name of this object
     */
    public RunnableCounter(String name) {
        // Saves the Name for Later
        this.name = name;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println(name + ": " + i);
        }
    }
}
