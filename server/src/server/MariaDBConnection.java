package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MariaDBConnection {
    public Connection dbconn() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/urlaubsplan", "root", "");
            return conn;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void selectStatement(String sqlstatement, String[] parameter) {
        try {
            PreparedStatement ps = dbconn().prepareStatement(sqlstatement);
            for (int i =0; i < parameter.length; i++) {
                ps.setString(i, parameter[i]);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void insertStatement(String sqlstatement, String[] parameter) {
        try {
            PreparedStatement ps = dbconn().prepareStatement(sqlstatement);
            ps.setString(1, "lalalal");
            ps.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
