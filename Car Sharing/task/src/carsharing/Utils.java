package carsharing;

public class Utils {

    public static String dbNameChooser (String[] args) {

        final String DB_NAME_ARGUMENT = "-databaseFileName";
        final String defaultDbName = "db";

        if (args == null) return defaultDbName;

        for (int i = 0; i < args.length; i++) {
            if (DB_NAME_ARGUMENT.equals(args[i])) return (i < args.length-1) ? args[i+1] : defaultDbName;
        }
        return defaultDbName;
    }

}
