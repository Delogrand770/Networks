
import java.awt.Point;
import java.util.ArrayList;

/**
 * Describes the Field of View for a Player
 *
 * @author adrian.defreitas
 */
public class FieldOfViewMessage extends Message {

    private Point centerCoordinate;
    private int width;
    private int height;
    private ArrayList<Location> locations;

    /**
     * Constructor
     *
     * @param width
     * @param height
     * @param centerCoordinate
     */
    public FieldOfViewMessage(int width, int height, Point centerCoordinate) {
        super("FOV");
        this.width = width;
        this.height = height;
        this.centerCoordinate = centerCoordinate;
        locations = new ArrayList<>();
    }

    /**
     * Retrieves all locations in row major order (left to right, top to bottom)
     *
     * @return
     */
    public ArrayList<Location> getLocations() {
        return locations;
    }

    /**
     * Returns the Width of the Field of View
     *
     * @return
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the Height of the Field of View
     *
     * @return
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the Center Coordinate of the Field of View
     *
     * @return
     */
    public Point getCenterCoordinate() {
        return centerCoordinate;
    }
    @Override
    public String toString() {
        String result = String.format("FOV MESSAGE \n\twidth: %s \n\theight: %s \n\tcenter: %s\n\tlocations: \n%s", 
                width, height, centerCoordinate, locations);
        return result;
    }
}
