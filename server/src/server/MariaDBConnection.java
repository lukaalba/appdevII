package src.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.*;

public class MariaDBConnection {
    private Connection conn;

    public Connection dbconn() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/urlaubsplan", "root", "");
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void closeCon(){
        if(conn !=null) {
            try {
                conn.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
