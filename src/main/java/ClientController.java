import ServerNetty.*;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class ClientController implements Initializable {
    public ListView<String> listFileClient;
    public Label status;
    public ListView<String> listFileServer;
    public TextField newFilename;
    public TextField newFilenameClient;
    private ObjectEncoderOutputStream os;
    private ObjectDecoderInputStream is;
    public Path serverRoot = Paths.get("server_dir");
    public Path clientRoot = Paths.get("dir");


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
            File serverDir = new File(serverRoot.toString());
            listFileServer.getItems().addAll(serverDir.list());
            Thread readThread = new Thread(()->
            { try {
                while (true) {
                    Message message = (Message) is.readObject();
                    Platform.runLater(()->status.setText(message.toString()));
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

    public void refreshServer(ActionEvent actionEvent) {
        File serverDir = new File(serverRoot.toString());
        listFileServer.getItems().clear();
        listFileServer.getItems().addAll(serverDir.list());
    }

    public void refreshClient(ActionEvent actionEvent) {
        File clientDir = new File(clientRoot.toString());
        listFileClient.getItems().clear();
        listFileClient.getItems().addAll(clientDir.list());
    }

    public void downloadFromServer(ActionEvent actionEvent){
    }

    public void deleteFileOnServer(ActionEvent actionEvent) throws IOException {
        String filename = listFileServer.getSelectionModel().getSelectedItem();
         os.writeObject(new FileDeleter(Paths.get(serverRoot.toString(),filename)));
         os.flush();
    }

    public void createNewFileOnServer(ActionEvent actionEvent) throws IOException {
        String filename = newFilename.getText();
        os.writeObject(new FileCreater(Paths.get(serverRoot.toString(),filename)));
        os.flush();
    }

    public void renameFileOnServer(ActionEvent actionEvent) throws IOException {
        String filename = listFileServer.getSelectionModel().getSelectedItem();
        String renameName = newFilename.getText();
        Object obj = new RenameRequest(Paths.get(serverRoot.toString(), filename),renameName);
        os.writeObject(obj);
        os.flush();

    }

    public void createNewFileOnClient(ActionEvent actionEvent) throws IOException {
        String filename = newFilenameClient.getText();
        os.writeObject(new FileCreater(Paths.get(clientRoot.toString(),filename)));
        os.flush();
    }

    public void renameFileOnClient(ActionEvent actionEvent) throws IOException {
        String filename = listFileClient.getSelectionModel().getSelectedItem();
        String renameName = newFilenameClient.getText();
        Object obj = new RenameRequest(Paths.get(clientRoot.toString(), filename),renameName);
        os.writeObject(obj);
        os.flush();
    }

    public void deleteFileOnClient(ActionEvent actionEvent) throws IOException {
        String filename = listFileClient.getSelectionModel().getSelectedItem();
        os.writeObject(new FileDeleter(Paths.get(clientRoot.toString(),filename)));
        os.flush();
    }

    public void createNewDirOnServer(ActionEvent actionEvent) throws IOException {
        String dirname = newFilename.getText();
        os.writeObject(new DirCreater(Paths.get(serverRoot.toString()),dirname));
        os.flush();
    }

    public void createNewDirOnClient(ActionEvent actionEvent) throws IOException {
        String dirname = newFilenameClient.getText();
        os.writeObject(new DirCreater(Paths.get(clientRoot.toString()),dirname));
        os.flush();
    }

    public void goOnDirServer(MouseEvent mouseEvent) {
        if (mouseEvent.isStillSincePress()){
        String selectDir = listFileServer.getSelectionModel().getSelectedItem();
        if (Files.isDirectory(serverRoot.resolve(selectDir))){
            serverRoot = serverRoot.resolve(selectDir);
        File serverDir = new File(serverRoot.toString());
        listFileServer.getItems().clear();
        listFileServer.getItems().addAll(serverDir.list());
        }
        }
    }
    public void upToServerDir(ActionEvent actionEvent) {
        serverRoot = serverRoot.getParent();
        File serverDir = new File(serverRoot.toString());
        listFileServer.getItems().clear();
        listFileServer.getItems().addAll(serverDir.list());

    }

    public void goOnDirClient(MouseEvent mouseEvent) {
        if (mouseEvent.isStillSincePress()){
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
}
