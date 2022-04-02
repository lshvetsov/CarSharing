package carsharing.dbConnector;

import carsharing.dto.Car;
import carsharing.dto.Company;
import carsharing.dto.Customer;
import carsharing.dto.Entity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CompanyDateBase {

    void addCompany (String name);
    Optional<Company> getCompany (int companyId);
    List<Company> getCompanies();

    void addCar (String name, long companyId);
    Optional<Car> getCar (long carId);
    List<Car> getCars(long companyId);
    List<Car> getCarsFreeOfRent (long companyId);

    void createCustomer (String name);
    Optional<Customer> getCustomer (long customerId);
    List<Customer> getCustomers();

    Map<String, Entity> getCustomerAndCars (long customerId);
    void rentCar (long customerId, long carId);
    void stopRent (long customerId);
}
