package carsharing.dto;

public class Customer extends Entity {

    private final Long rented_car_id;

    public Customer(long id, String name, long rented_car_id) {
        super(id, name);
        this.rented_car_id = rented_car_id;
    }

    public long getRented_car_id() {
        return rented_car_id;
    }
}
