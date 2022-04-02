package carsharing.dto;

public class Entity {

    private final long id;
    private final String name;

    public Entity(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }
}
