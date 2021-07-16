import ServerNetty.*;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

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


    public void uploadOnServer(ActionEvent actionEvent) throws IOException {
        String filename = listFileClient.getSelectionModel().getSelectedItem();
        os.writeObject(new FileMessage(Paths.get("dir", filename)));
        os.flush();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Socket socket = new Socket("Localhost",8188);
            os = new ObjectEncoderOutputStream(socket.getOutputStream());
            is = new ObjectDecoderInputStream(socket.getInputStream());
           File dir = new File("dir");
            listFileClient.getItems().addAll(dir.list());
            File serverDir = new File("server_dir");
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
        File serverDir = new File("server_dir");
        listFileServer.getItems().clear();
        listFileServer.getItems().addAll(serverDir.list());
    }

    public void refreshClient(ActionEvent actionEvent) {
        File clientDir = new File("dir");
        listFileClient.getItems().clear();
        listFileClient.getItems().addAll(clientDir.list());
    }

    public void downloadFromServer(ActionEvent actionEvent){
    }

    public void deleteFileOnServer(ActionEvent actionEvent) throws IOException {
        String filename = listFileServer.getSelectionModel().getSelectedItem();
         os.writeObject(new FileDeleter(Paths.get("server_dir",filename)));
         os.flush();
    }

    public void createNewFileOnServer(ActionEvent actionEvent) throws IOException {
        String filename = newFilename.getText();
        os.writeObject(new FileCreater(Paths.get("server_dir",filename)));
        os.flush();
    }

    public void renameFileOnServer(ActionEvent actionEvent) throws IOException {
        String filename = listFileServer.getSelectionModel().getSelectedItem();
        String renameName = newFilename.getText();
        Object obj = new RenameRequest(Paths.get("server_dir", filename),renameName);
        os.writeObject(obj);
        os.flush();

    }

    public void createNewFileOnClient(ActionEvent actionEvent) throws IOException {
        String filename = newFilenameClient.getText();
        os.writeObject(new FileCreater(Paths.get("dir",filename)));
        os.flush();
    }

    public void renameFileOnClient(ActionEvent actionEvent) throws IOException {
        String filename = listFileClient.getSelectionModel().getSelectedItem();
        String renameName = newFilenameClient.getText();
        Object obj = new RenameRequest(Paths.get("dir", filename),renameName);
        os.writeObject(obj);
        os.flush();
    }

    public void deleteFileOnClient(ActionEvent actionEvent) throws IOException {
        String filename = listFileClient.getSelectionModel().getSelectedItem();
        os.writeObject(new FileDeleter(Paths.get("dir",filename)));
        os.flush();
    }

    public void createNewDirOnServer(ActionEvent actionEvent) throws IOException {
        String dirname = newFilename.getText();
        os.writeObject(new DirCreater(Paths.get("server_dir"),dirname));
        os.flush();
    }

    public void createNewDirOnClient(ActionEvent actionEvent) throws IOException {
        String dirname = newFilenameClient.getText();
        os.writeObject(new DirCreater(Paths.get("dir"),dirname));
        os.flush();
    }
}
