import ServerNetty.*;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;

public class ClientController implements Initializable {
    public ListView<String> listFileClient;
    public Label status;
    public ListView<String> listFileServer;
    public TextField newFilename;
    public TextField newFilenameClient;
    public Label helpNewFile;
    public TextField loginClient;
    public PasswordField passwordClient;
    public Button buttonEnter;
    public TextField newFilenameServer;
    public TextField nameNewClient;
    public TextField loginNewClient;
    public PasswordField passwordNewClient;
    public PasswordField repeatPassword;
    public Label statusRegistr;
    public Label curDirOnServer;
    public Label curDirOnClient;
    public Label statusAuthentication;
    private ObjectEncoderOutputStream os;
    public String serverRoot = "server_dir";
    private ObjectDecoderInputStream is;
    public Path clientRoot = Paths.get("dir").toAbsolutePath();
    public int idClient;
    public String nameClient;
    private Node node;



    public void uploadOnServer(ActionEvent actionEvent) throws IOException {
        String filename = listFileClient.getSelectionModel().getSelectedItem();
        os.writeObject(new FileMessage(Paths.get(clientRoot.toString(), filename),Paths.get(serverRoot).toAbsolutePath().toString()));
        os.flush();
    }

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
                        case LIST_MESSAGE:
                            ListResponse response = (ListResponse) command;
                            List<String> nameFilesOnServer = response.getListFromServer();
                            serverRoot = response.getRootDir();
                            refreshFileListOnServer(nameFilesOnServer);
                            Platform.runLater(()->refreshFileListOnClient());
                            break;
                        case REQUEST_SERVER_DIR:
                            ResponseServerDir serverDir = (ResponseServerDir) command;
                            String name = serverDir.getNameServerDir();
                            Paths.get(serverRoot).resolve(Paths.get(name));
                            Platform.runLater(()->curDirOnServer.setText(name));
                            break;
                        case AUTHENTICATION_RESP:
                            AuthenticationResponse authenticationResponse = (AuthenticationResponse) command;
                                idClient = authenticationResponse.getUserId();
                                nameClient = authenticationResponse.getUserName();
                                buttonEnter.setDisable(false);
                            break;
                        case REGISTRATION_RESPONSE:
                            RegistrationResponse registrationResponse = (RegistrationResponse) command;
                            Platform.runLater(()->statusRegistr.setText(registrationResponse.getMsg()));
                            break;
                        case MESSAGE_RESPONSE:
                            Message_Response message_response = (Message_Response) command;
                            Platform.runLater(()->statusAuthentication.setText(message_response.getMsg()));
                            break;

                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            });
            readThread.setDaemon(true);
            readThread.start();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private void refreshFileListOnServer (List<String> fileName){
        Platform.runLater(()-> {
                listFileServer.getItems().clear();
                listFileServer.getItems().addAll(fileName);
                });
    }

    private void refreshFileListOnClient(){
        File clientDir = new File(clientRoot.toString());
        listFileClient.getItems().clear();
        listFileClient.getItems().addAll(clientDir.list());
    }

    public void refreshServer(ActionEvent actionEvent) throws IOException {
        os.writeObject(new ListRequest(idClient, nameClient));
        os.flush();
    }

    public void refreshClient(ActionEvent actionEvent) {
        File clientDir = new File(clientRoot.toString());
        curDirOnClient.setText(clientRoot.toString());
        listFileClient.getItems().clear();
        listFileClient.getItems().addAll(clientDir.list());
    }

    public void downloadFromServer(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        String filename = listFileServer.getSelectionModel().getSelectedItem();
        Path path = Paths.get(serverRoot.toString(), filename);
        String clientDir = clientRoot.toAbsolutePath().toString();
        os.writeObject(new FileRequest(path,clientDir));
        os.flush();

    }

    public void deleteFileOnServer(ActionEvent actionEvent) throws IOException {
        String filename = listFileServer.getSelectionModel().getSelectedItem();
         os.writeObject(new FileDeleter(Paths.get(serverRoot,filename)));
         os.flush();
    }

    public void renameFileOnServer(ActionEvent actionEvent) throws IOException {
        String filename = listFileServer.getSelectionModel().getSelectedItem();
        String renameName = newFilenameServer.getText();
        Object obj = new RenameRequest(Paths.get(serverRoot, filename),renameName);
        os.writeObject(obj);
        os.flush();
    }

    public void renameFileOnClient(ActionEvent actionEvent) throws IOException {
        String filename = listFileClient.getSelectionModel().getSelectedItem();
        String renameName = newFilenameClient.getText();
        Path source = Paths.get(clientRoot.toString(), filename);
        Files.move(source,source.resolveSibling(renameName)).toString();
    }

    public void deleteFileOnClient(ActionEvent actionEvent) throws IOException {
        String filename = listFileClient.getSelectionModel().getSelectedItem();
        Path file = Paths.get(clientRoot.toString(),filename);
        Files.delete(file);
        refreshFileListOnClient();
    }

    public void createNewDirOnServer(ActionEvent actionEvent) throws IOException {
        String dirname = newFilenameServer.getText();
        os.writeObject(new DirCreater(Paths.get(serverRoot),dirname));
        os.flush();
    }

    public void createNewDirOnClient(ActionEvent actionEvent) throws IOException {
        String dirname = newFilenameClient.getText();
        Files.createDirectories(Paths.get(clientRoot.toString(), dirname));
        refreshFileListOnClient();
    }

    public void goOnDirServer(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.isStillSincePress() && mouseEvent.getClickCount() == 2){
            String selectDir = listFileServer.getSelectionModel().getSelectedItem();
            os.writeObject(new GoToDir(selectDir));
            os.flush();
        }
    }
    public void upToServerDir(ActionEvent actionEvent) throws IOException {
            os.writeObject(new UpServerRequest());
            os.flush();
    }

    public void goOnDirClient(MouseEvent mouseEvent) {
        if (mouseEvent.isStillSincePress() && mouseEvent.getClickCount() == 2){
            String selectDir = listFileClient.getSelectionModel().getSelectedItem();
            if (Files.isDirectory(clientRoot.resolve(selectDir))){
                clientRoot = clientRoot.resolve(selectDir);
                refreshFileListOnClient();
            }
        }
        curDirOnClient.setText(clientRoot.toString());
    }

    public void upToClientDir(ActionEvent actionEvent) {
        clientRoot = clientRoot.getParent();
        refreshFileListOnClient();
        curDirOnClient.setText(clientRoot.toString());
    }

    public void setHelpMessageNewFolder(MouseEvent mouseEvent) {
        helpNewFile.setLayoutX(700);
        helpNewFile.setVisible(true);
        helpNewFile.setText("Создать новую папку");
    }

    public void resetHelpMessageNewFolder(MouseEvent mouseEvent) {
        helpNewFile.setVisible(false);
        //helpNewFile.setText("");
    }

    public void setHelpMessageRenameSerFile(MouseEvent mouseEvent) {
        helpNewFile.setLayoutX(810);
        helpNewFile.setVisible(true);
        helpNewFile.setText(" Изменить название");
    }

    public void resetHelpMessageRenameSerFile(MouseEvent mouseEvent) {
        helpNewFile.setVisible(false);
        //helpNewFile.setText("");
    }

    public void setHelpMessageDeleteFileServ(MouseEvent mouseEvent) {
        helpNewFile.setLayoutX(860);
        helpNewFile.setVisible(true);
        helpNewFile.setText(" Удалить файл");
    }

    public void resetHelpMessageDeleteFileServ(MouseEvent mouseEvent) {
        helpNewFile.setVisible(false);
        //helpNewFile.setText("");
    }

        public void setHelpMessageNewFolderClient(MouseEvent mouseEvent) {
        helpNewFile.setLayoutX(120);
        helpNewFile.setVisible(true);
        helpNewFile.setText("Создать новую папку");
    }

    public void setHelpMessageRenameClientFile(MouseEvent mouseEvent) {
        helpNewFile.setLayoutX(240);
        helpNewFile.setVisible(true);
        helpNewFile.setText(" Изменить название");
    }

    public void resetHelpMessageRenameClientFile(MouseEvent mouseEvent) {
        helpNewFile.setVisible(false);
        //helpNewFile.setText("");
    }

    public void setHelpMessageDeleteFilecCient(MouseEvent mouseEvent) {
        helpNewFile.setLayoutX(380);
        helpNewFile.setVisible(true);
        helpNewFile.setText(" Удалить файл");
    }

    public void resetHelpMessageDeleteFilecCient(MouseEvent mouseEvent) {
        helpNewFile.setVisible(false);
        //helpNewFile.setText("");
    }

   public void resetHelpMessageNewFolderClient(MouseEvent mouseEvent) {
        helpNewFile.setVisible(false);
        //helpNewFile.setText("");
    }

    public void goToRegistration(ActionEvent actionEvent) throws IOException {
        node = (Node) actionEvent.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        Parent parent = FXMLLoader.load(getClass().getResource("Cloud_Registration.fxml"));
        stage.setScene(new Scene(parent));
        stage.show();
    }

    public void goToAuthentication(ActionEvent actionEvent) throws IOException {
        node = (Node) actionEvent.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        Parent parent = FXMLLoader.load(getClass().getResource("Cloud_Authentication_window.fxml"));
        stage.setScene(new Scene(parent));
        stage.show();
    }

    public void enterOnProgram(ActionEvent actionEvent) throws IOException {
        node = (Node) actionEvent.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("cloud_storage1.fxml"));
        fxmlLoader.setController(this);
        Parent parent = fxmlLoader.load();
       // Parent parent = FXMLLoader.load(getClass().getResource("cloud_storage1.fxml"));
        stage.setScene(new Scene(parent));
        stage.show();
        refreshFileListOnClient();
    }

    public void sendAuthDataOnServer(ActionEvent actionEvent) throws IOException {
        String login = loginClient.getText();
        String password = passwordClient.getText();
        os.writeObject(new AuthenticationRequest(login,password));
        os.flush();
    }

    public void sendDataNewClientOnServer(ActionEvent actionEvent) throws IOException {
        String name = nameNewClient.getText();
        String login = loginNewClient.getText();
        String password = passwordNewClient.getText();
        String repeatPassword1 = repeatPassword.getText();
        if (name.equals("") || login.equals("") || password.equals("")){
            statusRegistr.setText("Name, login and password can not be null");
        }
        else if (password.equals(repeatPassword1)){
            os.writeObject(new Registration_Req(name,login,password));
            os.flush();
        }
        else
            statusRegistr.setText("Password and repeated password not match");

    }

    public void goOnStartScreen(ActionEvent actionEvent) throws IOException {
        node = (Node) actionEvent.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        Parent parent = FXMLLoader.load(getClass().getResource("cloud_storage_StartWindow.fxml"));
        stage.setScene(new Scene(parent));
        stage.show();
    }
}
