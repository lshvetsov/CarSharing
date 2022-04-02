package carsharing.dto;

public class Car extends Entity {

    private final long company_id;

    public Car(long id, String name, long company_id) {
        super(id, name);
        this.company_id = company_id;
    }

    public long getCompany_id() {
        return company_id;
    }

}
