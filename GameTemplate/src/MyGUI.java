
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;

//=====================================================================
public class MyGUI extends Thread {

    public int width, height; // dimensions of window frame
    public JFrame frame; // overall window frame
    public static MyCanvas canvas; // drawing canvas for window (inside panel)
    private BufferedImage image; // remembers drawing commands
    public Graphics2D offscreenGraphics; // buffered graphics context for painting
    public JButton defaultButton;
    public JLabel statusbarMessageLabel;
    public JPanel statusbarMessagePanel;
    public Mosaic mosaic;

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


        MyGUI panel = new MyGUI(732, 380);
        panel.mosaic = new Mosaic(732, 353, 3, true);

        Mosaic m = panel.mosaic;
        //m.randomArrange(false);
        //m.useBorder(false);
        m.generateColorset(4, Mosaic.GREY_FOCUS, null);
        //m.setBorderColorset(m.colorSetRandom);
        m.generateMosaic(m.colorSetRandom);
        //m.generateMosaic();
        m.saveMosaic("images/mosaic", "png");

        panel.setStatusbar("Message");
        try {
            BufferedImage loading = ImageIO.read(new File("./src/images/img1.jpg"));
            panel.offscreenGraphics.drawImage(loading, 0, 0, null);
            panel.offscreenGraphics.drawImage(panel.mosaic.getMosaic(), 0, 0, null);
            panel.copyGraphicsToScreen();
        } catch (IOException ex) {
            Logger.getLogger(MyGUI.class.getName()).log(Level.SEVERE, null, ex);
        }

        //System.exit(0);
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
        canvas.setMaximumSize(new Dimension(width, height));
        canvas.setBounds(0, 0, width, height);
        canvas.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                System.out.println(evt.getKeyCode());
                if (evt.getKeyCode() == 38) { //up
                } else if (evt.getKeyCode() == 40) {//down
                } else if (evt.getKeyCode() == 37) {//left
                } else if (evt.getKeyCode() == 39) {//right
                } else if (evt.getKeyCode() == 65) { //a
                } else if (evt.getKeyCode() == 82) { //r
                } else if (evt.getKeyCode() == 18) { //alt
                } else if (evt.getKeyCode() == 71) { //g
                } else if (evt.getKeyCode() == 73) { //i
                } else if (evt.getKeyCode() == 80) { //p
                }
            }
        });

        //-----Create all the buttons-----//
        defaultButton = new JButton();
        defaultButton.setText("Btn");
        defaultButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
            }
        });

        //Create server message bar 
        statusbarMessageLabel = new JLabel(" ");
        statusbarMessageLabel.setBackground(Color.WHITE);
        statusbarMessageLabel.setForeground(Color.BLACK);
        statusbarMessagePanel = new JPanel();
        statusbarMessagePanel.add(statusbarMessageLabel);
        statusbarMessagePanel.setMaximumSize(new Dimension(width, 30));

        // Create the window
        frame = new JFrame();
        frame.setTitle("Game Title");
        frame.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                canvas.requestFocus();
            }
        });
        frame.setResizable(false);
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.getContentPane().add(canvas);
        frame.getContentPane().add(statusbarMessagePanel);
        frame.setSize(width, height);
        //frame.pack();
        frame.setFocusable(true);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.toFront();
        // Make the canvas have the focus so that events are immediately sent to it. 
        frame.setAlwaysOnTop(false);
        centerWindow();
    }

    /**
     * Hides all content in the frame.
     */
    public void disableWindow() {
    }

    /**
     * Shows content in the frame.
     */
    public void enableWindow() {
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
    public void setStatusbar(String status) {
        statusbarMessageLabel.setText("  " + status);
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