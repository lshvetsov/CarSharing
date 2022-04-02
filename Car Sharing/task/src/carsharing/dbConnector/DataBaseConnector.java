package carsharing.dbConnector;

import carsharing.dto.Car;
import carsharing.dto.Company;
import carsharing.dto.Customer;
import carsharing.dto.Entity;

import java.sql.*;
import java.util.*;

public class DataBaseConnector implements CompanyDateBase {

    // JDBC driver name and database URL
    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:file:./src/carsharing/db/";

    private static Connection conn = null;

    public DataBaseConnector (String dbName) {

        try {
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            conn = DriverManager.getConnection(DB_URL+dbName);
            conn.setAutoCommit(true);

//            String dropCompany = "DROP TABLE COMPANY";
//            String dropCar = "DROP TABLE CAR";
//            String dropCustomer = "DROP TABLE CUSTOMER";
//            execute(dropCustomer);
//            execute(dropCar);
//            execute(dropCompany);

            String companyStatement = "CREATE TABLE IF NOT EXISTS COMPANY (" +
                    "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY , " +
                    "name VARCHAR(255) NOT NULL UNIQUE" +
                    ");";
            execute(companyStatement);

            String carStatement = "CREATE TABLE IF NOT EXISTS CAR (" +
                    "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL," +
                    "company_id INT NOT NULL," +
                    "FOREIGN KEY(company_id) references COMPANY(id));";
            execute(carStatement);

            String customerStatement = "CREATE TABLE IF NOT EXISTS CUSTOMER (" +
                    "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL UNIQUE," +
                    "rented_car_id INT," +
                    "FOREIGN KEY(rented_car_id) REFERENCES CAR(id));";
            execute(customerStatement);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addCompany(String name) {

        String statement = "INSERT INTO COMPANY (name) VALUES ('$company_name')";
        execute(statement.replace("$company_name", name));
        System.out.println("The company was created!\n");
    }

    @Override
    public Optional<Company> getCompany(int companyId) {

        String statement = String.format("SELECT * FROM COMPANY WHERE id = %d", companyId);

        try (PreparedStatement stmt = conn.prepareStatement(statement)) {
            stmt.execute();
            ResultSet resultSet = stmt.getResultSet();

            if (!resultSet.next()) return Optional.empty();
            return Optional.of(new Company(
                    resultSet.getInt("ID"),
                    resultSet.getString("NAME")));

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public List<Company> getCompanies() {

        String statement = "SELECT * FROM COMPANY";
        List<Company> result = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(statement)) {
            stmt.execute();
            ResultSet resultSet = stmt.getResultSet();

            if (!resultSet.next()) return result;

            do {
                result.add(new Company(resultSet.getInt("ID"), resultSet.getString("NAME")));
            } while (resultSet.next());

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        result.sort(Comparator.comparing(Company::getId, Long::compare));

        return result;

    }

    @Override
    public void addCar(String name, long companyId) {

        String statement = String.format("INSERT INTO CAR (name, company_id) VALUES ('%s', %d)",
                name, companyId);
        execute(statement);
        System.out.println("The car was added!\n");

    }

    @Override
    public Optional<Car> getCar(long carId) {

        String statement = String.format("SELECT * FROM CAR WHERE id = %d", carId);

        try (PreparedStatement stmt = conn.prepareStatement(statement)) {
            stmt.execute();
            ResultSet resultSet = stmt.getResultSet();

            if (!resultSet.next()) return Optional.empty();
            return Optional.of(new Car(
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getInt("company_id")
            ));

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Car> getCars(long companyId) {

        String statement = "SELECT * FROM CAR WHERE company_id = ?";
        List<Car> result = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(statement)) {
            stmt.setString(1, String.valueOf(companyId));
            stmt.execute();
            ResultSet resultSet = stmt.getResultSet();

            if (!resultSet.next()) return result;

            do {
                result.add(new Car(
                                resultSet.getInt("id"),
                                resultSet.getString("name"),
                                resultSet.getInt("company_id")
                ));
            } while (resultSet.next());

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return result;
    }

    @Override
    public List<Car> getCarsFreeOfRent(long companyId) {

        String statement = "SELECT * FROM CAR " +
                "LEFT JOIN CUSTOMER ON CAR.ID = CUSTOMER.RENTED_CAR_ID " +
                "WHERE CAR.COMPANY_ID = ? AND CUSTOMER.RENTED_CAR_ID is NULL";

        List<Car> result = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(statement)) {
            stmt.setString(1, String.valueOf(companyId));
            stmt.execute();
            ResultSet resultSet = stmt.getResultSet();

            if (!resultSet.next()) return result;

            do {
                result.add(new Car(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getInt("company_id")
                ));
            } while (resultSet.next());

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return result;

    }

    @Override
    public void createCustomer(String name) {
        String statement = String.format("INSERT INTO CUSTOMER (name) VALUES ('%s')", name);
        execute(statement);
        System.out.println("The customer was added!\n");
    }

    @Override
    public Optional<Customer> getCustomer(long customerId) {

        String statement = String.format("SELECT * FROM CUSTOMER WHERE id = %d", customerId);

        try (PreparedStatement stmt = conn.prepareStatement(statement)) {
            stmt.execute();
            ResultSet resultSet = stmt.getResultSet();

            if (!resultSet.next()) return Optional.empty();
            return Optional.of(new Customer(
                    resultSet.getInt("ID"),
                    resultSet.getString("NAME"),
                    resultSet.getLong("RENTED_CAR_ID")));

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Customer> getCustomers() {

        String statement = "SELECT * FROM CUSTOMER";
        List<Customer> result = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(statement)) {
            stmt.execute();
            ResultSet resultSet = stmt.getResultSet();

            if (!resultSet.next()) return result;

            do {
                result.add(new Customer(
                        resultSet.getInt("ID"),
                        resultSet.getString("NAME"),
                        resultSet.getLong("RENTED_CAR_ID")));
            } while (resultSet.next());

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        result.sort(Comparator.comparing(Customer::getId, Long::compare));

        return result;
    }

    @Override
    public Map<String, Entity> getCustomerAndCars(long customerId) {
        String statement = "SELECT * FROM CUSTOMER " +
                "LEFT JOIN CAR ON CUSTOMER.RENTED_CAR_ID = CAR.ID " +
                "LEFT JOIN COMPANY ON CAR.COMPANY_ID = COMPANY.ID " +
                "WHERE CUSTOMER.ID = ?";
        Map<String, Entity> result = new HashMap<>();

        try {
            PreparedStatement stmt = conn.prepareStatement(statement);
            stmt.setLong(1, customerId);
            stmt.execute();
            ResultSet resultSet = stmt.getResultSet();

            if (!resultSet.next()) return result;

            do {
                result.put("customer", new Customer(resultSet.getInt("CUSTOMER.ID"),
                        resultSet.getString("CUSTOMER.NAME"),
                        resultSet.getLong("RENTED_CAR_ID")));
                result.put("car", new Car(
                        resultSet.getInt("CAR.ID"),
                        resultSet.getString("CAR.NAME"),
                        resultSet.getInt("COMPANY_ID")
                ));
                result.put("company", new Company(
                        resultSet.getInt("COMPANY.ID"),
                        resultSet.getString("COMPANY.NAME")));
            } while (resultSet.next());

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    @Override
    public void rentCar(long customerId, long carId) {

        String statement = String.format("UPDATE CUSTOMER SET rented_car_id = %d WHERE id = %d",
                carId, customerId);

        execute(statement);
        System.out.printf("You rented '%s'\n", getCar(carId).orElseThrow().getName());
    }

    @Override
    public void stopRent(long customerId) {

        String statement = String.format("UPDATE CUSTOMER SET rented_car_id = null WHERE id = %d",
                customerId);

        execute(statement);
    }

    public void close() {

        try {
            if(conn!=null) conn.close();
        } catch(SQLException se){
            se.printStackTrace();
        }
    }

    private void execute(String statement) {

        try (PreparedStatement stmt = conn.prepareStatement(statement)) {
            stmt.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void carNumberIncrement(Integer companyId) {

        String statement = "SELECT * FROM COMPANY WHERE id=?";
        List<Company> result = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(statement,
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_UPDATABLE)) {
            stmt.setInt(1, companyId);
            stmt.execute();
            ResultSet resultSet = stmt.getResultSet();

            resultSet.last();
            if (resultSet.getRow() > 1) throw new IllegalStateException();
            resultSet.beforeFirst();

//            if (resultSet.next()) carNumbers.compute(resultSet.getInt("id"),
//                    (k,v) -> v == null ? 1 : ++v);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
