package DB;

import java.sql.Connection;
import java.sql.DriverManager;

public class DB_connection {
    public static Connection getConnection(){
        try {
            return DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/user_test","root", "123456" );
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
