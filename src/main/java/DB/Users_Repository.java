package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class Users_Repository {

    public Optional<AuthenticationService.Entry> getNameForAuthentication(String login, String password){
        Connection connection = DB_connection.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM cloud_users WHERE login = ? AND password = ?;");
            preparedStatement.setString(1,login);
            preparedStatement.setString(2,password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return Optional.of(new AuthenticationService.Entry(
                            //resultSet.getInt("id"),
                            resultSet.getString("Name"),
                            resultSet.getString("Login"),
                            resultSet.getString("Password")
                ));
            }
            else return Optional.empty();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        return Optional.empty();
    }
}
