
import com.google.gson.Gson;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JTextArea;

/**
 * TCP Client
 *
 * @author CS467
 */
public class TCPClient implements Runnable {

    //These are the TCP variables
    private Socket sock;
    private OutputStream sendStream;
    private InputStream recvStream;
    private String request;
    private String response;
    private String buffer = ""; //Holds partial json responses
    //These lists populate with the appropriate entities as the player moves.
    private ArrayList<String> attackableList = new ArrayList<>();
    private ArrayList<String> gettableList = new ArrayList<>();
    private ArrayList<String> gatherableList = new ArrayList<>();
    private ArrayList<String> examinableList = new ArrayList<>();
    private ArrayList<String> playerList = new ArrayList<>();
    //These lists are used by the graphics scheme
    private ArrayList<String> imageFiles = new ArrayList<>();
    private ArrayList<BufferedImage> images = new ArrayList<>();
    public static int scale = 60; //The height and width value to set the graphics.
    private String graphicsSet = "dev"; //The graphic theme to use... "dev"
    private String defaultPlayer = "player/HeroSorcerer.png"; //The default image to assign to all players.
    private String defaultOtherPlayer = "player/HeroBase.png"; //The default image to assign to all players.
    private String defaultNPC = "player/NPC.png"; //The default image to assign to all players.
    private String potionFix = "potion/potion.png"; //The item potion is in the folder called potion and this causes errors.
    private String unknownEntity = "tile/unknown.png"; //The default image to show when an entity does not yet have a graphic specified.
    private String zoneTransition = "tile/zonetransition.png"; //The default image to show for a zone transition.
    //These are often used objects from the MyGUI class
    private MyGUI p;
    private Graphics2D g;
    public boolean statsRequested = false;
    public String userName = "";
    public boolean sentFirst = false;
    public boolean showAllTraffic = false;

    public TCPClient(String server, int port) throws IOException, UnknownHostException {
        sock = new Socket(server, port);
        sendStream = sock.getOutputStream();
        recvStream = sock.getInputStream();
    }

    /**
     * Sends a TCP request
     */
    public void sendRequest() {
        try {
            byte[] sendBuff = new byte[request.length()];
            sendBuff = request.getBytes();
            sendStream.write(sendBuff);
            if (showAllTraffic) {
                System.out.println("S: " + request);
            }
        } catch (Exception ex) {
            System.err.println("Error in sendRequest");
        }
    }

    /**
     * A blocking call that waits for a TCP message.
     */
    @SuppressWarnings("empty-statement")
    public void getResponse() {
        try {
            int dataSize;
            while ((dataSize = recvStream.available()) == 0);
            byte[] recvBuff = new byte[dataSize];
            recvStream.read(recvBuff);
            response = new String(recvBuff);
        } catch (Exception ex) {
            System.err.println("Error in getResponse");
        }
    }

    /**
     * Parses the GSon responses and takes the appropriate action.
     */
    public void useResponse() {
        if (!sentFirst && !userName.isEmpty()) {
            sentFirst = true;
            p.enableWindow();
        }
        response = buffer + response; //Attach buffer data to the begining of the new response.
        buffer = ""; //Empty the buffer so it doesn't stack up.
        ArrayList<String> responses = new ArrayList<>();

        int open = 0; //Number of '{' counted
        int close = 0; //Number of '}' counted
        int last = 0; //The end index in the response string the last valid json string.

        //Figure out where the responses start and end in the response string.
        for (int i = 0; i < response.length(); i++) {

            //Count '{' and '}'
            if (response.charAt(i) == '{') {
                open++;
            } else if (response.charAt(i) == '}') {
                close++;
            }

            //If the count of '{' and '}' are equal then we have a valid json string.
            if (open > 0 && open == close) {
                responses.add(response.substring(last, i + 1)); //Save this complete response.
                last = i + 1; //Remember where the end of this response is.
                open = close = 0; //Reset the '{' and '}' counts.
            }
        }

        //If the last response is not at the end of the string then we have a partial json string and need to save it to the buffer.
        if (last != response.length()) {
            //System.out.println("SPLIT MESSAGE!!!");
            //System.out.println("\t" + buffer);
            buffer = response.substring(last);
        }

        //Figure out what kind of messages was received and take the appropriate action.
        Gson gsonLibrary = new Gson();
        for (int i = 0; i < responses.size(); i++) {

            //Cast the response into a generic message type and the find its actual type
            Message message = gsonLibrary.fromJson(responses.get(i), Message.class);
            String type = message.getMessageType();

            //Now that we have the correct type, recast to that type and then take the appropriate action.
            if (type.equalsIgnoreCase("TEXT")) {
                TextMessage msg = gsonLibrary.fromJson(responses.get(i), TextMessage.class);
                sendText(msg);
                System.out.println(">> " + msg);
            } else if (type.equalsIgnoreCase("FOV")) {
                FieldOfViewMessage msg = gsonLibrary.fromJson(responses.get(i), FieldOfViewMessage.class);
                drawClient(msg);
                if (showAllTraffic) {
                    System.out.println(">> " + msg);
                }
            } else if (type.equalsIgnoreCase("COMMAND")) {
                CommandMessage msg = gsonLibrary.fromJson(responses.get(i), CommandMessage.class);
                if (showAllTraffic) {
                    System.out.println(">> " + msg);
                }
            }
        }
    }

    /**
     * Closes the TCP Socket
     */
    public void close() {
        try {
            sendStream.close();
            recvStream.close();
            sock.close();
        } catch (Exception ex) {
            System.err.println("Error in close");
        }
    }

    /**
     * A constructor to start a new MyGUI because it is a pain to switch back
     * and forth to run the program.
     *
     * @param args - contains your command line arguments
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        MyGUI.main(args);
    }

    /**
     * Takes a string and returns its corresponding BufferedImage.
     *
     * @param test This string is usually in the form of
     * "entityType/entityName"; EX "creature/blue slime"
     * @return The buffered image corresponding to the test string.
     */
    public BufferedImage getImage(String test) {
        //System.out.println("-- " + test);

        //Use a default player image since there are no player classes.
        if (test.contains("nonplayercharacter") || test.contains("NPC")) { //NPC character
            return images.get(4);
        } else if (test.contains("player")) {
            if (test.contains(userName)) { //Users character
                return images.get(1);
            } else { //Other real players
                return images.get(3);
            }
        } else if (test.contains("zonetransition")) {
            return images.get(2);
        }

        if (test.contains("PLAYER")) {
            return images.get(3);
        }

        test = test.replaceAll("^\"|\"$", "").toLowerCase(); //Remove double quotes from string.

        //Look through all the imageFiles strings and try to match to a BufferedImage.
        for (int i = 0; i < imageFiles.size(); i++) {
            String file = imageFiles.get(i);
            //System.out.println(test + " | " + file + " | " + file.split("/")[1].split("\\.")[0]);
            if (file.startsWith(test) || file.split("/")[1].split("\\.")[0].equalsIgnoreCase(test)) {
                //System.out.println("\t*used above");
                return images.get(i);
            }
        }

        //If the test string doesn't match that means the image is not preloaded and will instead display as a question mark.
        //This is useful because as new entities are introuduced it is easy to figure out what their names are.
        System.out.println("Did not find Entity: \n\t" + test);
        return images.get(0);
    }

    /**
     * Draws all the graphics that the client sees using a FOV type message.
     *
     * @param data The FieldOfViewMessage to get the data from
     */
    public void drawClient(FieldOfViewMessage data) {
        p.setBackground(Color.WHITE);
        drawBackground(data);
        drawForeground(data);
        if (p.drawMosaic) {
            g.drawImage(p.mosaic.getMosaic(), 0, 0, p.width, p.height, null);
        }
        p.copyGraphicsToScreen();
    }

    /**
     * Draws the background tiles
     *
     * @param data The FieldOfViewMessage to get the data from
     */
    public void drawBackground(FieldOfViewMessage data) {
        int width = data.getWidth();
        ArrayList<Location> locations = data.getLocations();

        //Draw background tiles
        int x = 0;
        int y = 0;
        for (int i = 0; i < locations.size(); i++) {
            Location current = locations.get(i);
            String type = current.getTerrainType();

            //Draw the actual graphics to the buffer
            g.drawImage(getImage("tile/" + type), x * scale, y * scale, scale, scale, null);


            //Increment the x and y
            x++;
            if (x > width - 1) {
                x = 0;
                y++;
            }
        }
    }

    /**
     * Draws the foreground entities
     *
     * @param data The FieldOfViewMessage to get the data from
     */
    public void drawForeground(FieldOfViewMessage data) {
        int width = data.getWidth();
        ArrayList<Location> locations = data.getLocations();

        //Draw foreground tiles
        int x = 0;
        int y = 0;
        boolean createLists = false;
        boolean listsCreated = false;
        String text = " ";
        for (int i = 0; i < locations.size(); i++) {
            //System.out.println("\nIteration " + i);
            Location current = locations.get(i);
            ArrayList<Entity> entities = current.getEntities();

            //Checks if the current block is where the player is and signals for all entity lists to be populated.
            //Also updates player stats field.
            if (!createLists) {
                //System.out.println(i + ". " + entities + " | Size: " + entities.size());
                for (int j = 0; j < entities.size(); j++) {
                    Entity ent = entities.get(j);
                    if (ent.getName().equalsIgnoreCase(userName)) {
                        //System.out.println("Location " + i + " " + current);
                        p.playerInfoField.setText(ent.getDescription()); //Update player stats

                        //Clear all lists out
                        attackableList.clear();
                        gettableList.clear();
                        gatherableList.clear();
                        playerList.clear();
                        examinableList.clear();
                        createLists = true;
                    }
                }
            }

            //System.out.println("createLists = " + createLists);

            //Draws entites for the current location
            for (int j = 0; j < entities.size(); j++) {
                Entity ent = entities.get(j);
                String type = ent.getEntityType();
                String name = ent.getName();
                String imgRequest = type + "/" + name;
                //System.out.println(type);

                //Build lists
                //Add to entity lists if this is the players block.
                //Also displays gettable, fightable and gatherable items that are on the same space as the player.
                if (createLists && !listsCreated) {
                    if (type.equalsIgnoreCase("creature") || type.equalsIgnoreCase("player")) {
                        if (!name.contains(userName)) { //Don't add my own name to the list!
                            String OPLAYER_flag = type.equalsIgnoreCase("player") ? "PLAYER " : "";
                            attackableList.add(OPLAYER_flag + name);

                            text = ent.getDescription();
                        }
                        //System.out.println("AttackableList " + name);
                    }

                    if (type.equalsIgnoreCase("player")) {
                        if (!name.contains(userName)) { //Don't add my own name to the list!
                            playerList.add(name);
                        }
                    }
                    if (type.equalsIgnoreCase("material") || type.equalsIgnoreCase("potion") || type.equalsIgnoreCase("equipment") || type.equalsIgnoreCase("keyitem")) {
                        gettableList.add(name);
                        //System.out.println("GettableList " + name);
                    } else if (type.equalsIgnoreCase("resourceproducer")) {
                        gatherableList.add(name);
                        //System.out.println("GatherableList " + name);
                    } else if (type.equalsIgnoreCase("scenery") || type.equalsIgnoreCase("nonplayercharacter")) {
                        String NPC_flag = type.equalsIgnoreCase("nonplayercharacter") ? "NPC " : "";
                        examinableList.add(NPC_flag + name);
                        //System.out.println("ExaminableList " + name);
                    }

                }

                //Draw the actual graphics to the buffer regardless of type
                g.drawImage(getImage(imgRequest), x * scale, y * scale, scale, scale, null);

            }
            listsCreated = createLists ? true : false;

            //Increment the x and y
            x++;
            if (x > width - 1) {
                x = 0;
                y++;
            }
        }


        if (!playerList.isEmpty()) {
            text = "Players: ";
            for (int i = 0; i < playerList.size(); i++) {
                text += " - " + playerList.get(i);
            }
        }
        p.peoplePresentLabel.setText(text);

        p.createItemListScene(gettableList);
        p.createAttackListScene(attackableList);
        p.createGatherListScene(gatherableList);
        p.createExamineListScene(examinableList);
    }

    ////////////// Start Command Section //////////////
    public void formatCommand(CommandMessage message) {
        Gson gsonLibrary = new Gson();
        request = gsonLibrary.toJson(message);
        sendRequest();
    }

    public void chat(String msg) {
        CommandMessage message = new CommandMessage("CHAT", msg);
        formatCommand(message);
    }

    public void shout(String msg) {
        CommandMessage message = new CommandMessage("SHOUT", msg);
        formatCommand(message);
    }

    public void login(String username, String password) {
        this.userName = username; //Save the username so we are self aware.
        CommandMessage message = new CommandMessage("LOGI", username + " " + password);
        formatCommand(message);
        stat();
    }

    public void logoff() {
        CommandMessage message = new CommandMessage("LOGO", "");
        formatCommand(message);
    }

    public void look() {
        CommandMessage message = new CommandMessage("LOOK", "");
        formatCommand(message);
    }

    public void move(String direction) {
        CommandMessage message = new CommandMessage("MV", direction);
        formatCommand(message);
    }

    public void revive() {
        CommandMessage message = new CommandMessage("REV", "");
        formatCommand(message);
    }

    public void stat() {
        CommandMessage message = new CommandMessage("STAT", "");
        formatCommand(message);
    }

    public void who() {
        CommandMessage message = new CommandMessage("WHO", "");
        formatCommand(message);
    }

    public void attack() {
        if (!attackableList.isEmpty()) {
            String target = attackableList.get(0);
            target = target.startsWith("PLAYER ") ? target.split("PLAYER ")[1] : target; //Remove PLAYER label so the target can resolve
            CommandMessage message = new CommandMessage("ATK", "\"" + target + "\"");
            formatCommand(message);
        }
    }

    public void attack(String target) {
        if (!attackableList.isEmpty()) {
            target = target.startsWith("PLAYER ") ? target.split("PLAYER ")[1] : target; //Remove PLAYER label so the target can resolve
            CommandMessage message = new CommandMessage("ATK", "\"" + target + "\"");
            formatCommand(message);
        }
    }

    public void get() {
        if (!gettableList.isEmpty()) {
            CommandMessage message = new CommandMessage("GET", "\"" + gettableList.get(0) + "\"");
            formatCommand(message);
            stat();
        }
    }

    public void get(String item) {
        if (!gettableList.isEmpty()) {
            CommandMessage message = new CommandMessage("GET", "\"" + item + "\"");
            formatCommand(message);
            stat();
        }
    }

    public void examine() {
        if (!examinableList.isEmpty()) {
            String target = examinableList.get(0);
            target = target.startsWith("NPC ") ? target.split("NPC ")[1] : target; //Remove NPC label so the target can resolve
            CommandMessage message = new CommandMessage("EXM", "\"" + target + "\"");
            formatCommand(message);
        }
    }

    public void examine(String target) {
        if (!examinableList.isEmpty()) {
            target = target.startsWith("NPC ") ? target.split("NPC ")[1] : target; //Remove NPC label so the target can resolve
            CommandMessage message = new CommandMessage("EXM", "\"" + target + "\"");
            formatCommand(message);
        }
    }

    public void talk(String target) {
        if (!examinableList.isEmpty()) {
            target = target.startsWith("NPC ") ? target.split("NPC ")[1] : target; //Remove NPC label so the target can resolve
            CommandMessage message = new CommandMessage("TLK", "\"" + target + "\"");
            formatCommand(message);
        }
    }

    public void gather() {
        if (!gatherableList.isEmpty()) {
            CommandMessage message = new CommandMessage("GTHR", "\"" + gatherableList.get(0) + "\"");
            formatCommand(message);
            stat();
        }
    }

    public void gather(String target) {
        if (!gatherableList.isEmpty()) {
            CommandMessage message = new CommandMessage("GTHR", "\"" + target + "\"");
            formatCommand(message);
            stat();
        }
    }

    public void craft(String item) {
        CommandMessage message = new CommandMessage("CRFT", "\"" + item + "\"");
        formatCommand(message);
        stat();
    }

    public void equip(String item) {
        CommandMessage message = new CommandMessage("EQP", "\"" + item + "\"");
        formatCommand(message);
    }

    public void unequip(String item) {
        CommandMessage message = new CommandMessage("UEQP", "\"" + item + "\"");
        formatCommand(message);
    }

    public void drop(String item) {
        CommandMessage message = new CommandMessage("DROP", "\"" + item + "\"");
        formatCommand(message);
        stat();
    }

    public void sell(String item) {
        CommandMessage message = new CommandMessage("SELL", "\"" + item + "\"");
        formatCommand(message);
        stat();
    }

    public void buy(String item) {
        CommandMessage message = new CommandMessage("BUY", "\"" + item + "\"");
        formatCommand(message);
        stat();
    }

    public void dispose(String item) {
        CommandMessage message = new CommandMessage("DISP", "\"" + item + "\"");
        formatCommand(message);
        stat();
    }

    public void use(String item, String target) {
        CommandMessage message = new CommandMessage("USE", "\"" + item + "\"" + " " + "\"" + target + "\"");
        formatCommand(message);
        stat();
    }
    ////////////// End Command Section //////////////

    @Override
    public void run() {
        while (true) {
            getResponse();
            useResponse();
        }
    }

    /**
     * Used to get the MyGUI object from the client. Also builds the image list
     * and calls the buffering images method.
     *
     * @param panel The MyGUI object from the client.
     * @return This passes on the loadImages return string.
     */
    public String givePanel(MyGUI panel) {
        p = panel;
        g = panel.offscreenGraphics;
        imageFiles.add(unknownEntity);
        imageFiles.add(defaultPlayer);
        imageFiles.add(zoneTransition);
        imageFiles.add(defaultOtherPlayer);
        imageFiles.add(defaultNPC);
        imageFiles.add(potionFix);
        buildImageList("./src/images/" + graphicsSet + "/");
        return loadImages();
    }

    /**
     * Pre-loads all the image files in the imageFiles array.
     *
     * @return What the last image it was unable to find or "". If it can't find
     * an image the program will terminate due to the nature of the image usage
     * scheme.
     */
    public String loadImages() {
        int i = 0;
        String fileName = "";
        try {
            for (i = 0; i < imageFiles.size(); i++) {
                fileName = "./src/images/" + graphicsSet + "/" + imageFiles.get(i);
                BufferedImage loading = ImageIO.read(new File(fileName));
                if (loading != null) {
                    images.add(loading);
                } else {
                    System.out.println("Cannot Find " + fileName);
                }
            }
        } catch (Exception e) {
            return "\nERROR: Only " + images.size() + "/" + imageFiles.size() + " images loaded" + "\n\tCan't find image " + fileName;
        }
        return "";
    }

    /**
     * Recursively get all the files in the image directory and populate a
     * imageFiles list.
     *
     * @param path The path to the images.
     */
    public void buildImageList(String path) {
        File root = new File(path);
        File[] list = root.listFiles();

        for (File f : list) {
            if (f.isDirectory()) {
                buildImageList(f.getAbsolutePath());
            } else {
                String file = f.getPath().replace("\\", "/").split("./src/images/" + graphicsSet + "/")[1];
                if (!file.equalsIgnoreCase(defaultPlayer) && !file.equalsIgnoreCase(unknownEntity)) {
                    imageFiles.add(file.toLowerCase());
                }
            }
        }

    }

    /**
     * Sends messages of the text type to the client. Also populates the
     * inventory since that information comes from a text type message.
     *
     * @param data The message to send to the client
     */
    private void sendText(TextMessage data) {
        String msg = data.getMessage();

        //Populate inventory
        if (msg.contains("Carrying")) {
            if (statsRequested) {
                statsRequested = false;
                p.showStats(data.getMessage());
            }

            p.createInventoryScene(msg);

        } else if (msg.contains("Selling")) {
            p.createStoreScene(msg);
        } else if (msg.contains("now entering")) {
            String location = msg.split("entering: ")[1];
            p.frame.setTitle("SmallWorld - " + location);
        } else { //Send normal message
            JTextArea c = p.chatReceivedArea;

            if (msg.contains("Invalid credentials")) {
                userName = "";
                p.login();
            }

            //Server message goes to the status bar
            if (!data.getSender().equalsIgnoreCase("Smallworld Server")) {
                //System.out.println(data.getSender() + " - " + msg + "\n");
                c.append(data.getSender() + " - " + msg + "\n");
                c.setCaretPosition(c.getDocument().getLength());

            } else { //Chat message goes to the chatbox
                if (msg.contains("examine")) {
                    p.inform(msg);
                } else {
                    if (!data.getMessage().contains("intentional")) { //Get rid of the stupid "was that intentional messages"
                        p.serverMessageLabel.setText(data.getSender() + " - " + msg + "\n");
                    }
                }
            }
        }
    }
}
