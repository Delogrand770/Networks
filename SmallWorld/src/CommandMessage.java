
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Represents a Command from the Client to the Server
 *
 * @author adrian.defreitas
 */
public class CommandMessage extends Message {

    private String command;
    private String parameters;

    /**
     * Constructor
     *
     * @param command - the Command Name (i.e. "MV")
     * @param parameters - any parameters (depends on the command)
     */
    public CommandMessage(String command, String parameters) {
        super("COMMAND");

        this.command = command.trim().toLowerCase();
        this.parameters = parameters.trim();
    }

    @Override
    public String toString() {
        String result = String.format("COMMAND MESSAGE: \n\tcommand: %s \n\tparameters: %s", command, parameters);
        return result;
    }
}
