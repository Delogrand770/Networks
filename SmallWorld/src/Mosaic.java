
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * Mask
 *
 * This class will create a bufferedImage containing a grid mosaic of
 * translucent squares. This mosaic can then be drawn over another bufferedImage
 * to create the effect of pixels that appear to be tinted/highlighted. It is
 * possible to generate mosaics with different color focuses and to arrange the
 * squares at random or in a pattern.
 *
 * @author Gavin.Delphia
 * @date March 2013
 */
public class Mosaic {

    public final static int GREY_FOCUS = 0;
    public final static int NO_FOCUS = 1;
    public final static int RED_FOCUS = 2;
    public final static int GREEN_FOCUS = 3;
    public final static int BLUE_FOCUS = 4;
    ////////////////////////////////
    public Color[] colorSetRandom;
    ////////////////////////////////
    private BufferedImage mosaic; //The resulting mosaic to apply over another buffered image.
    private Graphics2D g; //The graphics object for the mosaic bufferedImage
    private Random gen = new Random(); //RNG
    private int gridSize; //The size of the squares
    private int width; //The width of the mosaic
    private int height; //The height of the mosaic
    private boolean useBorder; //Determines if the borders are drawn or not
    private boolean randomArrange = true; //Determines if a colorset is accessed at random or in order.
    private Color borderColor = new Color(.2f, .2f, .2f, .2f); //The default border color... blackish
    private Color[] borderColorSet; //A colorset to use for the border drawing.
    private boolean bordersUseColorset; //Determines if a border colorset is to be used or not.
    private float[] defaultIntensity = {.2f, .1f, .1f, .1f, .2f, .2f, .1f, .2f, .3f}; //Default intensity list for colorset generation
    private Color[] defaultColorSet = { //Default colorset for mosaic generation... gray
        new Color(1f, 1f, 1f, .2f), new Color(.8f, .8f, .8f, .1f), new Color(.6f, .6f, .6f, .1f),
        new Color(.8f, .8f, .8f, .1f), new Color(1f, 1f, 1f, .2f), new Color(.3f, .3f, .3f, .2f),
        new Color(.6f, .6f, .6f, .1f), new Color(.3f, .3f, .3f, .2f), new Color(.1f, .1f, .1f, .3f)
    };

    /**
     * Constructor
     *
     * @param width The width of the mosaic
     * @param height The height of the mosaic
     */
    public Mosaic(int width, int height) {
        this(width, height, 8, true);
    }

    /**
     * Constructor
     *
     * @param width The width of the mosaic
     * @param height The height of the mosaic
     * @param gridSize The size of the mosaic squares
     */
    public Mosaic(int width, int height, int gridSize) {
        this(width, height, gridSize, true);
    }

    /**
     * Constructor
     *
     * @param width The width of the mosaic
     * @param height The height of the mosaic
     * @param gridSize The size of the mosaic squares
     * @param useBorder Determines if the square borders are drawn or not.
     */
    public Mosaic(int width, int height, int gridSize, boolean useBorder) {
        this.width = width;
        this.height = height;
        this.gridSize = gridSize;
        this.useBorder = useBorder;
        mosaic = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g = mosaic.createGraphics();
    }

    /**
     * Creates a new bufferedImage over the old bufferedImage.
     */
    public void clearMosaic() {
        mosaic = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g = mosaic.createGraphics();
    }

    /**
     * Generates a mosaic with the defaultColorSet
     */
    public void generateMosaic() {
        generateMosaic(defaultColorSet);
    }

    /**
     * Generates a mosaic with the specified colorSet. If colorSet is null then
     * the defaultColorSet will be used instead.
     *
     * @param colorSet The array of colors to use.
     */
    public void generateMosaic(Color[] colorSet) {
        colorSet = (colorSet == null) ? defaultColorSet : colorSet;
        generateSquares(colorSet);
        if (useBorder) {
            generateBorder();
        }
    }

    /**
     * Accessor method to get the bufferedImage mosaic
     *
     * @return
     */
    public BufferedImage getMosaic() {
        return mosaic;
    }

    /**
     * Mutator method to change the useBorder boolean
     *
     * @param useBorder true/false boolean
     */
    public void useBorder(boolean useBorder) {
        this.useBorder = useBorder;
    }

    /**
     * Mutator method to change the gridSize int
     *
     * @param gridSize int for the gridSize
     */
    public void setGridSize(int gridSize) {
        this.gridSize = gridSize;
    }

    /**
     * Mutator method to change the borderColor Color
     *
     * @param borderColor Color for the borderColor
     */
    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    /**
     * Mutator method to add a borderColorSet. It also auto enables its use.
     *
     * @param colorSet the color set to use for the borders.
     */
    public void setBorderColorset(Color[] colorSet) {
        this.bordersUseColorset = true;
        this.borderColorSet = colorSet;
    }

    /**
     * Mutator method to remove a borderColorSet. It also disables its use.
     */
    public void removeBorderColorset() {
        this.bordersUseColorset = false;
        this.borderColorSet = null;
    }

    /**
     * Mutator method to change the randomArrange boolean
     *
     * @param randomArrange true/false boolean
     */
    public void randomArrange(boolean randomArrange) {
        this.randomArrange = randomArrange;
    }

    /**
     * Save the mosaic for future use.
     *
     * @param fileName "./src/" is the parent directory appended to the front of
     * the fileName
     * @param fileType The extension to save the file as excluding the leading
     * period. EX PNG, JPG, BMP
     */
    public void saveMosaic(String fileName, String fileType) {
        File f = new File("./src/" + fileName + "." + fileType);
        try {
            ImageIO.write(mosaic, fileType, f);
        } catch (IOException ex) {
            Logger.getLogger(Mosaic.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Generates a random color set and stores it in the array colorSetRandom.
     *
     * @param size The size of the colored boxes
     * @param focus The color focus that will be used during generation.
     * @param intensity The array of intensities to be used. Use null for
     * default intensity list
     * @return The Color[] array generated
     */
    public Color[] generateColorset(int size, int focus, float[] intensity) {
        intensity = (intensity == null) ? defaultIntensity : intensity;
        int modSize = intensity.length;
        colorSetRandom = new Color[size];
        for (int i = 0; i < colorSetRandom.length; i++) {
            if (focus == 0) { //gray focus
                float f = randomFloat();
                colorSetRandom[i] = new Color(f, f, f, intensity[i % modSize]);
            } else if (focus == 1) { //all colors
                colorSetRandom[i] = new Color(randomFloat(), randomFloat(), randomFloat(), intensity[i % modSize]);
            } else if (focus == 2) { //red focus
                colorSetRandom[i] = new Color(1f, randomFloat(), randomFloat(), intensity[i % modSize]);
            } else if (focus == 3) { //green focus
                colorSetRandom[i] = new Color(randomFloat(), 1f, randomFloat(), intensity[i % modSize]);
            } else if (focus == 4) { //blue focus
                colorSetRandom[i] = new Color(randomFloat(), randomFloat(), 1f, intensity[i % modSize]);
            }
        }
        return colorSetRandom;
    }

    /**
     * Draws the square grid pattern for the bufferedImage mosaic. This occurs
     * after the borders have been drawn.
     *
     * @param set The color set to use during generation.
     */
    private void generateSquares(Color[] set) {
        int innerCount = 0;
        int outerCount = 0;
        int modSize = set.length;
        int increment = (int) Math.floor(set.length / 3);

        for (int i = 0; i < width; i = i + gridSize) {
            innerCount = 0;
            for (int j = 0; j < height; j = j + gridSize) {
                if (randomArrange) {
                    g.setColor(set[gen.nextInt(set.length)]);
                } else {
                    g.setColor(set[(innerCount + outerCount) % modSize]);

                    //Increment counters
                    innerCount++;
                    if (innerCount >= increment) {
                        innerCount = 0;
                    }
                }
                if (useBorder) {
                    g.fillRect(i + 1, j + 1, gridSize - 1, gridSize - 1);
                } else {
                    g.fillRect(i, j, gridSize, gridSize);
                }
            }

            //Increment counters
            outerCount += increment;
            if (outerCount > increment * 2) {
                outerCount = 0;
            }
        }
    }

    /**
     * Draws the borders on the bufferedImage mosaic.
     */
    private void generateBorder() {
        Color[] colorSet = (borderColorSet == null) ? defaultColorSet : borderColorSet;
        g.setColor(borderColor);

        //Draw vertical
        for (int i = 0; i <= width; i = i + gridSize) {
            if (bordersUseColorset) {
                g.setColor(colorSet[gen.nextInt(colorSet.length)]);
            }
            g.drawLine(i, 0, i, height);
        }

        //Draw horizontal
        for (int i = 0; i <= height; i = i + gridSize) {
            if (bordersUseColorset) {
                g.setColor(colorSet[gen.nextInt(colorSet.length)]);
            }
            g.drawLine(0, i, width, i);
        }
    }

    /**
     * Generates a random float from 0.0f to 1.0f both inclusive.
     *
     * @return A random float from 0.0f to 1.0f
     */
    private float randomFloat() {
        float f = gen.nextFloat();
        if (gen.nextBoolean()) { //[0,1)
            return f;
        } else { //(0,1]
            return 1.0f - f;
        }
    }
}
