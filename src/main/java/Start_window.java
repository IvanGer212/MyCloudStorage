import DB.AuthenticationService;
import ServerNetty.AbstractCommand;
import ServerNetty.AuthenticationRequest;
import ServerNetty.AuthenticationResponse;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class Start_window implements Initializable {
    public TextField loginClient;
    public PasswordField passwordClient;
    private ObjectDecoderInputStream is;
    private ObjectEncoderOutputStream os;
    public int idClient;
    public String nameClient;
    private Stage newWindow;
    private Node node;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Socket socket = new Socket("Localhost",8188);
            os = new ObjectEncoderOutputStream(socket.getOutputStream());
            is = new ObjectDecoderInputStream(socket.getInputStream());
            Thread readThread = new Thread(()->
            { try {
                while (true) {
                    AbstractCommand command = (AbstractCommand) is.readObject();
                    switch (command.getType()) {
                        case AUTHENTICATION_RESP:
                            AuthenticationResponse authenticationResponse = (AuthenticationResponse) command;
                           idClient = authenticationResponse.getIdClient();
                            nameClient = authenticationResponse.getNameClient();
                            try {
                                Stage stage = (Stage) node.getScene().getWindow();
                                Parent parent = FXMLLoader.load(getClass().getResource("cloud_storage1.fxml"));
                                stage.setScene(new Scene(parent));
                                stage.show();
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                                //idClient = authenticationResponse.getEntry().get().getIdClient();
                                //nameClient = authenticationResponse.getEntry().get().getName();
                            }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
            }
        );
            readThread.setDaemon(true);
            readThread.start();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void tryConnectWithServer(ActionEvent actionEvent) throws IOException {
        node = (Node) actionEvent.getSource();
        String login = loginClient.getText();
        String password = passwordClient.getText();
        os.writeObject(new AuthenticationRequest(login,password));
        os.flush();
    }
}
