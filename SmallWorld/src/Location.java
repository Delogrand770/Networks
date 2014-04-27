
import java.awt.Point;
import java.util.ArrayList;

/**
 * Represents a location in the Smallworld Simulation
 *
 * @author adrian.defreitas
 */
public class Location {

    private Point coordinate;
    private String terrainType;
    private boolean traverseable;
    private ArrayList<Entity> entities;

    /**
     * Constructor
     *
     * @param coordinate
     * @param terrainType
     * @param traverseable
     */
    public Location(Point coordinate, String terrainType, boolean traverseable) {
        this.coordinate = coordinate;
        this.terrainType = terrainType;
        this.traverseable = traverseable;
        this.entities = new ArrayList<Entity>();
    }

    /**
     * Gets the Location's Zone Coordinate
     *
     * @return
     */
    public Point getCoordinate() {
        return coordinate;
    }

    /**
     * Gets the Terrain Type of the Location (i.e. Grass, Water, etc)
     *
     * @return
     */
    public String getTerrainType() {
        return terrainType;
    }

    /**
     * Returns TRUE is the environment can be traversed by a player, FALSE
     * otherwise
     *
     * @return
     */
    public boolean isTraverseable() {
        return traverseable;
    }

    /**
     * Gets all of the entities located at this location
     *
     * @return
     */
    public ArrayList<Entity> getEntities() {
        return entities;
    }

    @Override
    public String toString() {
        String result = String.format("\t[%s, %s, %s]\n\t\t%s\n", 
                coordinate, terrainType, traverseable, entities);
        return result;
    }
}
