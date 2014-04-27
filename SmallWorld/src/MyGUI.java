
import java.awt.*;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.*;

//=====================================================================
public class MyGUI extends Thread {

    public int width, height; // dimensions of window frame
    public JFrame frame; // overall window frame
    public static MyCanvas canvas; // drawing canvas for window (inside panel)
    private BufferedImage image; // remembers drawing commands
    public Graphics2D offscreenGraphics; // buffered graphics context for painting
    public JTextField chatSendField;
    public JTextArea chatReceivedArea;
    public JButton attackButton;
    public JButton reviveButton;
    public JButton statButton;
    public JButton chatToggleButton;
    public JButton inventoryToggleButton;
    public JButton craftToggleButton;
    public JLabel peoplePresentLabel;
    public JLabel serverMessageLabel;
    public JLabel listItemSceneLabel;
    public JPanel serverMessagePanel;
    public JPanel playerActionPanel;
    public JPanel peoplePresentPanel;
    public JPanel inventoryScene; //**
    public JPanel craftScene; //**
    public JPanel gameScene; //**
    public JPanel listSceneLeft; //**
    public JPanel listSceneRight; //**
    public JPanel itemListScene; //**
    public JPanel attackListScene; //**
    public JPanel gatherListScene; //**
    public JPanel examineListScene; //**
    public JPanel playerInfoPanel;
    public JLabel playerInfoField;
    public JScrollPane chatReceivedPane;
    public final int SIZE = TCPClient.scale * 5;
    public Mosaic mosaic;
    public boolean drawMosaic = true;
    //---------------------------------
    public static TCPClient client = null;

    public static void main(String[] args) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MyGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MyGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MyGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MyGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        MyGUI panel = new MyGUI(900, 700);
        panel.mosaic = new Mosaic(900, 700, 10);
        //panel.mosaic.generateColorset(3, Mosaic.GREY_FOCUS, null);
        panel.mosaic.generateMosaic();
        //Get command line arguments that will specify the ip and port
        final String servName = (args.length >= 1 && args[0] != null)
                ? args[0] : "128.236.40.20"; //SmallWorld Server
        final int servPort = (args.length >= 2 && args[1] != null && Integer.parseInt(args[1]) > 0)
                ? Integer.parseInt(args[1]) : 12345;

        panel.setServerMessage(
                "Attempting TCP client connnection to " + servName + " on port " + servPort + " ... ");

        //Create the TCP client
        try {
            client = new TCPClient(servName, servPort);//Create/Start the thread on the TCP client
            Thread listen = new Thread(client);
            listen.start();
            panel.setServerMessage("Attempting TCP client connnection to " + servName + " on port " + servPort + " ... SUCCESS");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panel.frame, "The server " + servName + ":" + servPort + " cannot be reached at this time.", panel.frame.getTitle(), 0);
            System.exit(0);
        }
        //Pass panel object to TCPClient
        String result = client.givePanel(panel);

        if (!result.isEmpty()) {
            JOptionPane.showMessageDialog(panel.frame, result, panel.frame.getTitle(), 0);
            System.exit(0);
        }

        //Login to the server
        panel.login();

        //Load default crafting receipes
        panel.createCraftScene();
    }

    public MyGUI(int desiredWidth, int desiredHeight) {
        width = desiredWidth;
        height = desiredHeight;

        // Start the drawing panel in its own thread
        this.run();
    }

    @Override
    public void run() {
        // Construct a buffered image (an offscreen image that is stored in RAM)    
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        offscreenGraphics = image.createGraphics();
        offscreenGraphics.setColor(Color.BLACK);
        setBackground(Color.WHITE);


        // Create a AWT canvas object that can be drawn on - the offscreen image will be drawn onto this canvas
        canvas = new MyCanvas(this);
        canvas.setMaximumSize(new Dimension(SIZE, SIZE));
        canvas.setBounds(0, 0, SIZE, SIZE);
        canvas.setMinimumSize(new Dimension(SIZE, SIZE));
        canvas.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                //System.out.println(evt.getKeyCode());
                if (evt.getKeyCode() == 38) { //up
                    client.move("North");
                } else if (evt.getKeyCode() == 40) {//down
                    client.move("South");
                } else if (evt.getKeyCode() == 37) {//left
                    client.move("West");
                } else if (evt.getKeyCode() == 39) {//right
                    client.move("East");
                } else if (evt.getKeyCode() == 65) { //a
                    client.attack();
                } else if (evt.getKeyCode() == 82) { //r
                    client.revive(); //No warning this way
                } else if (evt.getKeyCode() == 18) { //alt
                    chatSendField.setVisible(true);
                    chatReceivedPane.setVisible(true);
                    frame.pack();
                    chatSendField.requestFocus();
                } else if (evt.getKeyCode() == 71) { //g
                    client.get();
                } else if (evt.getKeyCode() == 73) { //i
                    inventoryScene.setVisible(true);
                    frame.pack();
                } else if (evt.getKeyCode() == 80) { //p
                } else if (evt.getKeyCode() == 77) { //m
                    drawMosaic = !drawMosaic;
                    client.look();
                } else if (evt.getKeyCode() == 87) { //w
                    client.who();
                }
            }
        });

        attackListScene = new JPanel();
        attackListScene.setBorder(BorderFactory.createTitledBorder("Attack"));
        attackListScene.setPreferredSize(new Dimension(300, 50));
        itemListScene = new JPanel();
        itemListScene.setBorder(BorderFactory.createTitledBorder("Get"));
        itemListScene.setPreferredSize(new Dimension(300, 50));
        gatherListScene = new JPanel();
        gatherListScene.setBorder(BorderFactory.createTitledBorder("Gather"));
        gatherListScene.setPreferredSize(new Dimension(300, 50));
        examineListScene = new JPanel();
        examineListScene.setBorder(BorderFactory.createTitledBorder("Examine / Talk"));
        examineListScene.setPreferredSize(new Dimension(300, 50));

        listSceneLeft = new JPanel();
        listSceneLeft.setLayout(new BoxLayout(listSceneLeft, BoxLayout.Y_AXIS));
        listSceneLeft.add(attackListScene);
        listSceneLeft.add(itemListScene);
        listSceneLeft.setMaximumSize(new Dimension(width, 180));

        listSceneRight = new JPanel();
        listSceneRight.setLayout(new BoxLayout(listSceneRight, BoxLayout.Y_AXIS));
        listSceneRight.add(gatherListScene);
        listSceneRight.add(examineListScene);
        listSceneRight.setMaximumSize(new Dimension(width, 180));

        gameScene = new JPanel();
        gameScene.setLayout(new BoxLayout(gameScene, BoxLayout.X_AXIS));
        gameScene.add(listSceneLeft);
        gameScene.add(canvas);
        gameScene.add(listSceneRight);
        gameScene.setPreferredSize(new Dimension(width, SIZE));

        //Create player info bar
        playerInfoField = new JLabel();
        playerInfoField.setText(" ");
        playerInfoPanel = new JPanel();
        playerInfoPanel.add(playerInfoField);

        //-----Create all the buttons-----//
        attackButton = new JButton();
        attackButton.setText("Attack");
        attackButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                client.attack();
                canvas.requestFocus();
            }
        });

        chatToggleButton = new JButton();
        chatToggleButton.setText("Chat");
        chatToggleButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (chatReceivedPane.isVisible()) {
                    chatSendField.setVisible(false);
                    chatReceivedPane.setVisible(false);
                } else {
                    chatSendField.setVisible(true);
                    chatReceivedPane.setVisible(true);
                }
                frame.pack();
                canvas.requestFocus();
            }
        });

        inventoryToggleButton = new JButton();
        inventoryToggleButton.setText("Inventory");
        inventoryToggleButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (inventoryScene.isVisible()) {
                    inventoryScene.setVisible(false);
                } else {
                    showInventory();
                }
                frame.pack();
                canvas.requestFocus();
            }
        });

        craftToggleButton = new JButton();
        craftToggleButton.setText("Craft");
        craftToggleButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (craftScene.isVisible()) {
                    craftScene.setVisible(false);
                } else {
                    showCraft();
                }
                frame.pack();
                canvas.requestFocus();
            }
        });

        statButton = new JButton();
        statButton.setText("Stats");
        statButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                client.statsRequested = true;
                client.stat();
                canvas.requestFocus();
            }
        });

        reviveButton = new JButton();
        reviveButton.setText("Revive");
        reviveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int result = JOptionPane.showConfirmDialog(frame, "Are you sure you want to revive?");
                if (result == 0) {
                    client.revive();
                }
                canvas.requestFocus();
            }
        });

        peoplePresentLabel = new JLabel(" ");
        peoplePresentPanel = new JPanel();
        peoplePresentPanel.add(peoplePresentLabel);
        peoplePresentPanel.setMaximumSize(new Dimension(width, 30));

        //Create the player action panel
        playerActionPanel = new JPanel();
        playerActionPanel.add(attackButton);
        playerActionPanel.add(reviveButton);
        playerActionPanel.add(statButton);
        playerActionPanel.add(chatToggleButton);
        playerActionPanel.add(inventoryToggleButton);
        playerActionPanel.add(craftToggleButton);


        //Create the inventory scene
        inventoryScene = new JPanel();
        inventoryScene.setLayout(new GridLayout(0, 5));

        //Create the inventory scene
        craftScene = new JPanel();
        craftScene.setLayout(new GridLayout(0, 5));

        //Create server message bar 
        serverMessageLabel = new JLabel(" ");
        serverMessageLabel.setBackground(Color.WHITE);
        serverMessageLabel.setForeground(Color.BLACK);
        serverMessagePanel = new JPanel();
        serverMessagePanel.add(serverMessageLabel);
        serverMessagePanel.setMaximumSize(new Dimension(width, 30));

        //Create chat input
        chatSendField = new JTextField();
        chatSendField.setMaximumSize(new Dimension(width, 30));
        chatSendField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                //System.out.println(evt.getKeyCode());
                int enterKey = 10;
                if (evt.getKeyCode() == enterKey) {
                    String msg = chatSendField.getText().trim();
                    if (!msg.isEmpty()) {
                        if (msg.startsWith("SHOUT ")) { //Hidden shout command
                            client.shout(msg.split("SHOUT ")[1]);
                        } else { //Otherwise use normal chat command
                            client.chat(msg);
                        }
                        chatSendField.setText("");
                    }
                    canvas.requestFocus();
                }
            }
        });

        // Create chat window
        chatReceivedArea = new JTextArea();
        chatReceivedArea.setColumns(20);
        chatReceivedArea.setRows(8);
        chatReceivedArea.setFocusable(false);
        chatReceivedPane = new JScrollPane();
        chatReceivedPane.setViewportView(chatReceivedArea);

        //Hide some things by default
        inventoryScene.setVisible(false);
        craftScene.setVisible(false);

        // Create the window
        frame = new JFrame();
        frame.setTitle("Small World");
        frame.setResizable(false);
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.getContentPane().add(gameScene);
        frame.getContentPane().add(playerInfoPanel);
        frame.getContentPane().add(peoplePresentPanel);
        frame.getContentPane().add(playerActionPanel);
        frame.getContentPane().add(inventoryScene);
        frame.getContentPane().add(craftScene);
        frame.getContentPane().add(chatSendField);
        frame.getContentPane().add(chatReceivedPane);
        frame.getContentPane().add(serverMessagePanel);
        frame.setSize(width, height);
        //frame.setPreferredSize(new Dimension(width, height));
        frame.setFocusable(true);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.toFront();
        centerWindow();
        disableWindow();
        // Make the canvas have the focus so that events are immediately sent to it. 
        canvas.requestFocus();
        frame.setAlwaysOnTop(false);
    }

    /**
     * Populates the examine box.
     *
     * @param list The list of examinable entities
     */
    public void createExamineListScene(ArrayList<String> list) {
        //The list provided is already parsed so just create the objects from it.
        examineListScene.removeAll();
        for (int i = 0; i < list.size(); i++) {
            ImageIcon myIcon = new ImageIcon(client.getImage(list.get(i)));
            final JButton temp = new JButton(myIcon);
            temp.setToolTipText(list.get(i));
            temp.setPreferredSize(new Dimension(32, 32));
            temp.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    String[] options = new String[]{"Examine", "Talk", "Cancel"};
                    int result = JOptionPane.showOptionDialog(frame, "What would you like to do?",
                            "Smallworld", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
                    if (result == 0) {
                        client.examine(temp.getToolTipText());
                    } else if (result == 1) {
                        client.talk(temp.getToolTipText());
                    }
                    canvas.requestFocus();
                }
            });
            examineListScene.add(temp);
        }
        examineListScene.repaint();
        frame.pack();
    }

    /**
     * Populates the gather box.
     *
     * @param list The list of gatherable entities
     */
    public void createGatherListScene(ArrayList<String> list) {
        //The list provided is already parsed so just create the objects from it.
        gatherListScene.removeAll();
        for (int i = 0; i < list.size(); i++) {
            ImageIcon myIcon = new ImageIcon(client.getImage(list.get(i)));
            final JButton temp = new JButton(myIcon);
            temp.setToolTipText(list.get(i));
            temp.setPreferredSize(new Dimension(32, 32));
            temp.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    client.gather(temp.getToolTipText());
                    canvas.requestFocus();
                }
            });
            gatherListScene.add(temp);
        }
        gatherListScene.repaint();
        frame.pack();
    }

    /**
     * Populates the attack box.
     *
     * @param list The list of attackFable entities
     */
    public void createAttackListScene(ArrayList<String> list) {
        //The list provided is already parsed so just create the objects from it.
        attackListScene.removeAll();
        for (int i = 0; i < list.size(); i++) {
            ImageIcon myIcon = new ImageIcon(client.getImage(list.get(i)));
            final JButton temp = new JButton(myIcon);
            temp.setToolTipText(list.get(i));
            temp.setPreferredSize(new Dimension(32, 32));
            temp.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    client.attack(temp.getToolTipText());
                    canvas.requestFocus();
                }
            });
            attackListScene.add(temp);
        }
        attackListScene.repaint();
        frame.pack();
    }

    /**
     * Populates the get box.
     *
     * @param list The list of gettable entities
     */
    public void createItemListScene(ArrayList<String> list) {
        //The list provided is already parsed so just create the objects from it.
        itemListScene.removeAll();
        for (int i = 0; i < list.size(); i++) {
            ImageIcon myIcon = new ImageIcon(client.getImage(list.get(i)));
            final JButton temp = new JButton(myIcon);
            temp.setToolTipText(list.get(i));
            temp.setPreferredSize(new Dimension(32, 32));
            temp.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    client.get(temp.getToolTipText());
                    canvas.requestFocus();
                }
            });
            itemListScene.add(temp);
        }
        itemListScene.repaint();
        frame.pack();
    }

    /**
     * Populates the craft menu.
     */
    public void createCraftScene() {
        String[] string = {
            "Oak Pointy Stick",
            "Oak Wood Sword",
            "Copper Sword",
            "Copper Ingot",
            "Oak Shield",
            "Copper Shield",
            "Tonic",
            "Potion",
            "Flame Sabre",
            "Earth Sabre",
            "Mythril Ingot",
            "Polished Mythril",
            "Crab Shield",
            "King Crab Shield",
            "Mythril Blade",
            "Mythril Shield",
            "Diamond Blade",
            "Diamond Shield",
            "Elemental Blade",
            "Elemental Shield"
        };
        String[] toolTip = {
            "Oak Wood Branch, Oak Wood Branch",
            "Oak Wood Branch, Oak Wood Branch, Oak Wood Branch, Oak Wood Branch",
            "Oak Wood Branch, Copper Ingot, Copper Ingot, Fire Crystal",
            "Copper Ore, Copper Ore",
            "Oak Wood Branch, Oak Wood Branch, Oak Wood Branch",
            "Oak Wood Branch, Oak Wood Branch, Copper Ingot, Fire Crystal",
            "Blue Slime Drop, Blue Slime Drop",
            "Yellow Slime Drop, Yellow Slime Drop, Blue Slime Drop",
            "Silver Sword, Pine Lumber, Pine Lumber, Fire Crystal",
            "Copper Sword, Pine Lumber, Pine Lumber, Earth Crystal",
            "Mythril Ore, Mythril Ore, Mythril Ore",
            "Mythril Ingot, Mythril Ingot, Mythril Ingot",
            "Pine Lumber, Crab Shell, Crab Shell",
            "Pine Lumber, Crab Shell, Crab Shell, King Crab Shell",
            "Pine Lumber, Mythril Ingot, Mythril Ingot",
            "Pine Lumber, Pine Lumber, Polished Mythril",
            "Diamond, Crystal Sword, Fire Crystal, Fire Crystal",
            "Diamond, Crystal Shield, Fire Crystal, Fire Crystal",
            "Fire Crystal, Earth Crystal, Lightning Crystal, Water Crystal, Diamond Blade, Polished Mythril",
            "Fire Crystal, Earth Crystal, Lightning Crystal, Water Crystal, Diamond Shield, Polished Mythril"
        };

        //Build objects from provided data
        for (int i = 0; i < string.length; i++) {
            ImageIcon myIcon = new ImageIcon(client.getImage(string[i]));
            final JButton temp = new JButton(string[i], myIcon);
            temp.setToolTipText(toolTip[i]);
            temp.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    int result = JOptionPane.showConfirmDialog(frame, "Are you sure you want to craft a(n) " + temp.getText() + "?");
                    if (result == 0) {
                        client.craft(temp.getText());
                    }
                    canvas.requestFocus();
                }
            });

            craftScene.add(temp);
        }
        craftScene.repaint();
        frame.pack();
    }

    /**
     * Populates the inventory menu
     *
     * @param msg String with the inventory to be parsed
     */
    public void createInventoryScene(String msg) {
        String money = msg.split("Currency: ")[1].split("Cooldown")[0];
        //Manually parse out the inventory...
        ArrayList<JButton> buttons = new ArrayList<>();
        if (Integer.parseInt(msg.split("Carrying ")[1].split(" items")[0]) != 0) {
            msg = msg.split("max\\):")[1];

            Scanner scan = new Scanner(msg);
            while (scan.hasNextLine()) {
                String[] start = scan.nextLine().split("\\(");
                String str = start[0].trim();
                String toolTip = (start.length > 1) ? "(" + start[1].trim() : "";
                if (!str.isEmpty()) {
                    ImageIcon myIcon = new ImageIcon(client.getImage(str));
                    JButton temp = new JButton(str, myIcon);
                    temp.setToolTipText(toolTip);
                    buttons.add(temp);
                }
            }
        }

        //Build objects from parsed message
        inventoryScene.removeAll();
        for (int i = 0; i < buttons.size(); i++) {
            final String textData = buttons.get(i).getText();
            buttons.get(i).addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    String[] options = new String[]{"Equip", "Unequip", "Use", "Sell", "Drop", "Dispose"};
                    int result = JOptionPane.showOptionDialog(frame, "What would you like to do with the " + textData + "?",
                            "Smallworld", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
                    //System.out.println(result);
                    if (result == 0) {
                        client.equip(textData);
                    } else if (result == 1) {
                        client.unequip(textData);
                    } else if (result == 2) {
                        String target = textData.equalsIgnoreCase("brass key")
                                ? "gate" : textData.contains("Key")
                                ? textData.split(" ")[0] + " door" : client.userName;
                        client.use(textData, target);
                    } else if (result == 3) {
                        client.sell(textData);
                    } else if (result == 4) {
                        client.drop(textData);
                    } else if (result == 5) {
                        int result2 = JOptionPane.showConfirmDialog(frame, "Are you sure you want to dispose of " + textData + " FOREVER?");
                        if (result2 == 0) {
                            client.dispose(textData);
                        }
                    }
                    canvas.requestFocus();
                }
            });
            inventoryScene.add(buttons.get(i));
        }

        //Add special inventory item to show money amount
        ImageIcon myIcon = new ImageIcon(client.getImage("money"));
        JButton temp = new JButton(money.trim(), myIcon);
        buttons.add(temp);
        inventoryScene.add(temp);

        inventoryScene.repaint();
        frame.pack();
    }

    /**
     * Creates a store menu when NPC's are selling things.
     *
     * @param msg The String to be parsed with the store content.
     */
    public void createStoreScene(String msg) {
        JPanel store = new JPanel();
        store.setLayout(new GridLayout(0, 3));
        ArrayList<JButton> buttons = new ArrayList<>();
        Scanner scan = new Scanner(msg);
        String title = scan.nextLine();

        //Parse msg
        while (scan.hasNextLine()) {
            String[] start = scan.nextLine().split(" - ");
            String str = start[0].trim();
            String toolTip = (start.length > 1) ? start[1].trim() : "";
            if (!str.isEmpty()) {
                ImageIcon myIcon = new ImageIcon(client.getImage(str));
                JButton temp = new JButton(str, myIcon);
                temp.setToolTipText(toolTip);
                buttons.add(temp);
            }
        }

        //Build objects from parsed message
        for (int i = 0; i < buttons.size(); i++) {
            final String textData = buttons.get(i).getText();
            buttons.get(i).addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    int result = JOptionPane.showConfirmDialog(frame, "Would you like to buy a(n) " + textData + "?");
                    if (result == 0) {
                        client.buy(textData);
                    }
                    canvas.requestFocus();
                }
            });
            store.add(buttons.get(i));
        }
        JOptionPane.showMessageDialog(frame, store, title, 1);
    }

    /**
     * Handles the login dialog and actions.
     */
    public void login() {
        disableWindow();

        //Create the form objects
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayout(2, 2));
        loginPanel.setPreferredSize(new Dimension(300, 50));

        JLabel labelName = new JLabel("Username: ");
        JTextField fieldName = new JTextField("Gavin.Delphia", 30);

        JLabel labelPass = new JLabel("Password: ");
        JPasswordField fieldPass = new JPasswordField("4zPWkt", 30);

        loginPanel.add(labelName);
        loginPanel.add(fieldName);
        loginPanel.add(labelPass);
        loginPanel.add(fieldPass);

        boolean keepGoing = true;
        while (keepGoing) {
            int result = JOptionPane.showConfirmDialog(frame, loginPanel, "Login", JOptionPane.OK_CANCEL_OPTION);
            if (result == -1 || result == 2) { //Cancel or red x pressed
                System.exit(0);
                keepGoing = false;
            } else if (!fieldName.getText().isEmpty() && !fieldPass.getText().isEmpty()) { //Everything looks good so attempt a login!
                setServerMessage("The server appears to be running but LAG may cause this screen to hang.");
                client.login(fieldName.getText(), fieldPass.getText());
                client.sentFirst = false;
                keepGoing = false;
            }
        }
    }

    /**
     * Creates a simple dialog to inform of examine events.
     *
     * @param message The message from the event.
     */
    public void inform(String message) {
        JOptionPane.showMessageDialog(frame, message, "Info", 1);
    }

    /**
     * Shows the inventory.
     */
    public void showInventory() {
        inventoryScene.setVisible(true);
        craftScene.setVisible(false);
        frame.pack();
    }

    /**
     * Shows the craft menu.
     */
    public void showCraft() {
        inventoryScene.setVisible(false);
        craftScene.setVisible(true);
        frame.pack();
    }

    /**
     * Hides all content in the frame.
     */
    public void disableWindow() {
        gameScene.setVisible(false);
        chatSendField.setVisible(false);
        chatReceivedPane.setVisible(false);
        playerActionPanel.setVisible(false);
    }

    /**
     * Unhides content in the frame.
     */
    public void enableWindow() {
        gameScene.setVisible(true);
        chatSendField.setVisible(true);
        chatReceivedPane.setVisible(true);
        playerActionPanel.setVisible(true);
        frame.pack();
        canvas.requestFocus();
    }

    /**
     * Centers the JAVA window on opening
     *
     * Credit:
     * http://www.java-forums.org/awt-swing/3491-jframe-center-screen.html
     */
    public final void centerWindow() {
        // Get the size of the screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        // Determine the new location of the window
        int w = width;
        int h = height;
        int x = (dim.width - w) / 2;
        int y = (dim.height - h) / 2;

        // Move the window
        frame.setLocation(x, y);
    }

    /**
     * Sets the server message area text.
     *
     * @param status The string to set the label to
     */
    public void setServerMessage(String status) {
        serverMessageLabel.setText("  " + status);
    }

    /**
     * Shows the player stats.
     *
     * @param data The players stats.
     */
    public void showStats(String data) {
        JOptionPane.showMessageDialog(frame, data, "Player Stats", 1);
    }

    /**
     * Gets the graphics object
     *
     * @return The graphics object
     */
    public Graphics2D getGraphics() {
        return offscreenGraphics;
    }

    /**
     * Fills the BufferedImage with a color
     *
     * Credit: 210 Drawing Panel
     *
     * @param c The color to fill with
     */
    public void setBackground(Color c) {
        // remember the current color so it can be restored
        Color currentColor = offscreenGraphics.getColor();

        offscreenGraphics.setColor(c);
        offscreenGraphics.fillRect(0, 0, width, height);

        // restore color
        offscreenGraphics.setColor(currentColor);
    }

    /**
     * Copies the BufferedImage to the canvas
     *
     * Credit: 210 DrawingPanel
     */
    public void copyGraphicsToScreen() {
        Graphics2D myG = (Graphics2D) canvas.getGraphics();
        myG.drawImage(image, 0, 0, width, height, null);


    }
}

/**
 * Creates a canvas that can be drawn to.
 *
 * Credit 210 DrawingPanel
 *
 */
class MyCanvas extends Canvas {

    private MyGUI panel;

    public MyCanvas(MyGUI thisPanel) {
        super();
        panel = thisPanel;
    }

    @Override
    public void paint(Graphics g) {
        panel.copyGraphicsToScreen();
    }
}