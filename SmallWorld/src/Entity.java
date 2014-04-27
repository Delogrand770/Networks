
/**
 * Represents an Entity
 *
 * @author adrian.defreitas
 */
public class Entity {

    private String id;
    private String name;
    private String entityType;
    private String description;

    /**
     * Constructor
     *
     * @param id - the unique ID of the entity
     * @param name - the entity's name
     * @param entityType - the type of the entity
     * @param description - one or more descriptions (separated by ;)
     */
    public Entity(String id, String name, String entityType, String description) {
        this.id = id;
        this.name = name;
        this.entityType = entityType;
        this.description = description;
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

    @Override
    public String toString() {
        String result = String.format("[%s, %s, %s, %s]", 
                id, name, entityType, description);
        return result;
    }
}
