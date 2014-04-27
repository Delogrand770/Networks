
/**
 *
 * @author C14Gavin.Delphia
 */
public abstract class Entity {
    private String id;
    private String name;
    private String entityType;
    private String description;
    private int x;
    private int y;

    /**
     * Constructor
     *
     * @param id - the unique ID of the entity
     * @param name - the entity's name
     * @param entityType - the type of the entity
     * @param description - one or more descriptions (separated by ;)
     */
    public Entity(String id, String name, String entityType, String description, int x, int y) {
        this.id = id;
        this.name = name;
        this.entityType = entityType;
        this.description = description;
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the ID for this entity
     *
     * @return
     */
    public String getID() {
        return id;
    }

    /**
     * Returns the name of this entity
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the Type of this Entity (i.e. Creature, Player, etc)
     *
     * @return
     */
    public String getEntityType() {
        return entityType;
    }

    /**
     * Gets the description of this entity
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the x position of this entity
     *
     * @return
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the y position of this entity
     *
     * @return
     */
    public int getY() {
        return y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    abstract void tick();
    abstract void draw();

    @Override
    public String toString() {
        return String.format("[%s, %s, %s, %s, %d, %d]", id, name, entityType, description, x, y);
    }
}
