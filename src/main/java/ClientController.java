import ServerNetty.*;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

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
    public AnchorPane window_setText;
    public Button upClientDir, upServDir;
    private ObjectEncoderOutputStream os;
    public Path serverRoot = Paths.get("server_dir");
    private ObjectDecoderInputStream is;
    public Path clientRoot = Paths.get("dir").toAbsolutePath();
   // public ButtonType button;


    public void uploadOnServer(ActionEvent actionEvent) throws IOException {
        String filename = listFileClient.getSelectionModel().getSelectedItem();
        os.writeObject(new FileMessage(Paths.get(clientRoot.toString(), filename),serverRoot.toAbsolutePath().toString()));
        os.flush();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Socket socket = new Socket("Localhost",8188);
            os = new ObjectEncoderOutputStream(socket.getOutputStream());
            is = new ObjectDecoderInputStream(socket.getInputStream());
           File dir = new File(clientRoot.toString());
            listFileClient.getItems().addAll(dir.list());
            os.writeObject(new ListRequest());
            os.flush();
            Thread readThread = new Thread(()->
            { try {
                while (true) {
                    AbstractCommand command = (AbstractCommand) is.readObject();
                    switch (command.getType()) {
                        case LIST_MESSAGE:
                            ListResponse response = (ListResponse) command;
                            List<String> nameFilesOnServer = response.getListFromServer();
                            refreshFileListOnServer(nameFilesOnServer);
                            break;
                        case REQUEST_SERVER_DIR:
                            ResponseServerDir serverDir = (ResponseServerDir) command;
                            String name = serverDir.getNameServerDir();
                            serverRoot.resolve(Paths.get(name));
                            //Platform.runLater(()->serverRoot);
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
                }
                );
    }

    public void refreshServer(ActionEvent actionEvent) throws IOException {
        os.writeObject(new ListRequest());
        os.flush();
    }

    public void refreshClient(ActionEvent actionEvent) {
        File clientDir = new File(clientRoot.toString());
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
         os.writeObject(new FileDeleter(Paths.get(serverRoot.toString(),filename)));
         os.flush();
    }

    public void renameFileOnServer(ActionEvent actionEvent) throws IOException {
        String filename = listFileServer.getSelectionModel().getSelectedItem();
        String renameName = newFilename.getText();
        Object obj = new RenameRequest(Paths.get(serverRoot.toString(), filename),renameName);
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
    }

    public void createNewDirOnServer(ActionEvent actionEvent) throws IOException {
        String dirname = newFilename.getText();
        os.writeObject(new DirCreater(Paths.get(serverRoot.toString()),dirname));
        os.flush();
    }

    public void createNewDirOnClient(ActionEvent actionEvent) throws IOException {
        String dirname = newFilenameClient.getText();
        Path newDirClient = Files.createDirectories(Paths.get(clientRoot.toString(), dirname));
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
                File clientDir = new File(clientRoot.toString());
                listFileClient.getItems().clear();
                listFileClient.getItems().addAll(clientDir.list());
            }
        }
    }

    public void upToClientDir(ActionEvent actionEvent) {
        clientRoot = clientRoot.getParent();
        File clientDir = new File(clientRoot.toString());
        listFileClient.getItems().clear();
        listFileClient.getItems().addAll(clientDir.list());
    }

    public void setHelpMessageNewFolder(MouseEvent mouseEvent) {
        helpNewFile.setLayoutX(730);
        helpNewFile.setVisible(true);
        helpNewFile.setText("Создать новую папку");
    }

    public void resetHelpMessageNewFolder(MouseEvent mouseEvent) {
        helpNewFile.setVisible(false);
        //helpNewFile.setText("");
    }

    public void setHelpMessageRenameSerFile(MouseEvent mouseEvent) {
        helpNewFile.setLayoutX(820);
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
        helpNewFile.setLayoutX(180);
        helpNewFile.setVisible(true);
        helpNewFile.setText("Создать новую папку");
    }

    public void setHelpMessageRenameClientFile(MouseEvent mouseEvent) {
        helpNewFile.setLayoutX(280);
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

    public void apply(ActionEvent actionEvent) {
    }

    public void cancel(ActionEvent actionEvent) {
    }
}
