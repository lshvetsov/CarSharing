package carsharing;

import carsharing.dbConnector.DataBaseConnector;

public class Main {

    public static void main(String[] args) {

        String dbName = Utils.dbNameChooser(args);
        DataBaseConnector connector = new DataBaseConnector(dbName);
        UserConsole console = new UserConsole(connector);

        console.run();

        connector.close();

    }
}