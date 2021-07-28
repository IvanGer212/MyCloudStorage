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
                            resultSet.getInt("id"),
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

    public Optional<UsersFilesOnServer.ParentDir> getUsersParentDirOnServer (int clientId) {
        Connection connection = DB_connection.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT parent_folder FROM cloud_users WHERE id = ?;");
            preparedStatement.setInt(1, clientId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(new UsersFilesOnServer.ParentDir(
                        resultSet.getString("parent_folder")

                ));
            }else return null; //Optional.empty();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return null; //Optional.empty();
    }

    public boolean createNewUserParentDir (int clientId, String clientName){
        Connection connection = DB_connection.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE cloud_users SET parent_folder = ? where id = ?;");
            preparedStatement.setString(1, clientName);
            preparedStatement.setInt(2,clientId);
            int i = preparedStatement.executeUpdate();
            return i!=0;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        finally {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return false;
    }
}
