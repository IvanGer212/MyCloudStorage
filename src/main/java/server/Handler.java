package server;

import java.io.*;
import java.net.Socket;

public class Handler implements Runnable{
    private final String directory = "server_dir";
    private final DataInputStream is;
    private final DataOutputStream os;
    private final byte[] buffer;

    public Handler(Socket socket) throws IOException {
        is = new DataInputStream(socket.getInputStream());
        os = new DataOutputStream(socket.getOutputStream());
        buffer = new byte[1024];
    }

    @Override
    public void run() {
        try{
            while (true){
                String filename = is.readUTF();
                System.out.println("File: " + filename);
                long filesize = is.readLong();
                System.out.println("Size: " + filesize);
                try(FileOutputStream fos = new FileOutputStream(directory + "/" + filename)){
                    for (int i = 0; i < (filesize+1023)/1024; i++) {
                        int read = is.read(buffer);
                        fos.write(buffer,0,read);
                    }
                }
                os.writeUTF("File: " + filename + " successfully received!");
                os.flush();
            }
        } catch (Exception e){
            System.err.println("Exception while read");
        }

    }
}
