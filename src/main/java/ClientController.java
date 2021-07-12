import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;

public class ClientController implements Initializable {
    public ListView<String> listFileClient;
    public Label status;
    public ListView<String> listFileServer;
    private DataOutputStream os;
    private DataInputStream is;


    public void uploadOnServer(ActionEvent actionEvent) throws IOException {
        String filename = listFileClient.getSelectionModel().getSelectedItem();
        File file = new File("dir/"+filename);
        long filesize = file.length();
        os.writeUTF(filename);
        os.writeLong(filesize);
        Files.copy(file.toPath(),os);
        status.setText("File: "+filename + " send on server.");

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Socket socket = new Socket("Localhost",8189);
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
            File dir = new File("dir");
            listFileClient.getItems().addAll(dir.list());
            File serverDir = new File("server_dir");
            listFileServer.getItems().addAll(serverDir.list());
            Thread readThread = new Thread(()->
            { try {
                while (true) {
                    String statusRead = is.readUTF();
                    Platform.runLater(()->status.setText(statusRead));
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
}
