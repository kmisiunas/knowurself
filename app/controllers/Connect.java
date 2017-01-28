//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.sql.Connection;
import java.sql.DriverManager;

public class Connect {
    public Connect() {
    }

    public Connection getConnection() {
        String connectionString = "jdbc:sqlserver://knowyourself.database.windows.net:1433;database=KnowYourself;user=su@KnowYourself;password=!alexandrU97;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(connectionString);
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return connection;
    }
}
