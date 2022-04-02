package carsharing;

import carsharing.dbConnector.CompanyDateBase;
import carsharing.dto.Car;
import carsharing.dto.Company;
import carsharing.dto.Customer;
import carsharing.dto.Entity;

import java.util.*;

public class UserConsole {

    private static final Scanner scanner = new Scanner(System.in);
    private final CompanyDateBase companyDateBase;
    private final Map<Integer, String> menues = Map.of(
            1,  "1. Log in as a manager\n2. Log in as a customer\n3. Create a customer\n0. Exit\n",
            2,  "1. Company list\n2. Create a company\n0. Back\n",
            3, "1. Car list\n2. Create a car\n0. Back\n",
            4,  "1. Rent a car\n2. Return a rented car\n3. My rented car\n0. Back"
    );

    public UserConsole (CompanyDateBase companyDateBase) {
        this.companyDateBase = companyDateBase;
    }

    public void run() {

        boolean stopMarker = true;

        while (stopMarker) {

            System.out.println(menues.get(1));
            String choice = scanner.next();

            switch (choice) {
                case "1": managerMenu(); break;
                case "2": allCustomersMenu(); break;
                case "3": customerCreationMenu(); break;
                default: stopMarker = false; break;
            }
        }

    }

    private void managerMenu () {

        boolean stopMarker = true;

        while (stopMarker) {

            System.out.println(menues.get(2));
            String choice = scanner.next();

            switch (choice) {
                case "1":
                    long companyId = chooseCompany();
                    if (companyId == 0) break;
                    companyMenu(companyId);
                    break;
                case "2":
                    System.out.println("Enter the company name:");
                    scanner.nextLine();
                    String companyName = scanner.nextLine();
                    companyDateBase.addCompany(companyName);
                    break;
                default: stopMarker = false;
            }

        }

    }
    private void companyMenu (Long companyId) {

        boolean stopMarker = true;

        while (stopMarker) {

            System.out.println(menues.get(3));
            String choice = scanner.next();

            switch (choice) {
                case "1":
                    printCompanyCars(companyDateBase.getCars(companyId));
                    break;
                case "2":
                    System.out.println("Enter the car name:");
                    scanner.nextLine();
                    String carName = scanner.nextLine();
                    companyDateBase.addCar(carName, companyId);
                    break;
                default: stopMarker = false;
            }

        }

    }
    private void allCustomersMenu() {
        var customers = companyDateBase.getCustomers();
        if (customers.isEmpty()) {
            System.out.println("The customer list is empty!\n");
            return;
        }
        printCustomers(customers);
        int index = scanner.nextInt() - 1;

        if (index < 0 || index > customers.size() - 1) {
            System.out.println("There is no such user\n");
            return;
        }
        customerMenu(customers.get(index).getId());
    }
    private void customerMenu (long customerId) {

        boolean stopMarker = true;

        while (stopMarker) {

            System.out.println(menues.get(4));
            String choice = scanner.next();

            Customer customer;

            switch (choice) {
                case "1":
                    customer = companyDateBase.getCustomer(customerId).orElseThrow();

                    if (customer.getRented_car_id() != 0) {
                        System.out.println("You've already rented a car!");
                        break;
                    }
                    var companyChoice = chooseCompany();
                    if (companyChoice == 0) break;

                    var carIdChoice = chooseCar(companyChoice, true);
                    if (carIdChoice == 0) break;

                    companyDateBase.rentCar(customerId, carIdChoice);
                    break;
                case "2":
                    customer = companyDateBase.getCustomer(customerId).orElseThrow();
                    if (customer.getRented_car_id() == 0) {
                        System.out.println("You didn't rent a car!\n");
                        break;
                    }
                    companyDateBase.stopRent(customerId);
                    System.out.println("You've returned a rented car!");
                    break;
                case "3":
                    printRentedCar(companyDateBase.getCustomerAndCars(customerId));
                    break;
                default: stopMarker = false;
            }
        }
    }
    private void customerCreationMenu () {
        System.out.println("Enter the customer name:");
        scanner.nextLine();
        String customerName = scanner.nextLine();
        companyDateBase.createCustomer(customerName);
    }

    private long chooseCompany () {
        var companies = companyDateBase.getCompanies();
        if (companies.isEmpty()) {
            System.out.println("The company list is empty!\n");
            return 0;
        }
        printCompanies(companies);
        var index = scanner.nextInt() - 1;
        if (companies.isEmpty() || index < 0 || index > companies.size() - 1) return 0;
        return companies.get(index).getId();
    }
    private long chooseCar (long companyId, boolean isFreeOfRent) {
        var cars = isFreeOfRent ?
                companyDateBase.getCarsFreeOfRent(companyId) :
                companyDateBase.getCars(companyId);
        if (cars.isEmpty()) {
            System.out.println("The car list is empty!\n");
            return 0;
        }
        printCompanyCars(cars);
        var index = scanner.nextInt() - 1;
        if (cars.isEmpty() || index < 0 || index > cars.size() - 1) return 0;
        return cars.get(index).getId();
    }
    private void printCompanies(List<Company> listToPrint)  {
        System.out.println("Choose the company:");
        listToPrint.forEach(x -> System.out.printf("%d. %s\n", listToPrint.indexOf(x)+1, x.getName()));
        System.out.println("0. Back\n");
    }
    private void printCompanyCars (List<Car> listToPrint) {
        if (listToPrint.isEmpty()) System.out.println("The car list is empty!\n");
        else {
            System.out.println("Car list:");
            listToPrint.forEach(x -> System.out.printf("%d. %s\n", listToPrint.indexOf(x)+1, x.getName()));
            System.out.println();
        }
    }
    private void printCustomers(List<Customer> listToPrint) {
        System.out.println("Choose a customer:");
        listToPrint.forEach(x -> System.out.printf("%d. %s\n", listToPrint.indexOf(x)+1, x.getName()));
        System.out.println("0. Back\n");
    }
    private void printRentedCar (Map<String, Entity> rentedCarToPrint) {

        if (rentedCarToPrint.get("car").getId() == 0) System.out.println("You didn't rent a car!\n");
        else {
            System.out.printf("Your rented car:\n%s\n", rentedCarToPrint.get("car").getName());
            System.out.printf("Company:\n%s\n\n", rentedCarToPrint.get("company").getName());
        }
    }

    public CompanyDateBase getCompanyDateBase() {
        return companyDateBase;
    }

}
