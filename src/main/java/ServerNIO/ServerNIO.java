package ServerNIO;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

import static java.nio.file.Files.createFile;

public class ServerNIO {
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private ByteBuffer buf;
    private Path root;


    public ServerNIO() throws IOException {
        root = Paths.get("dir");
        serverSocketChannel = ServerSocketChannel.open();
        selector = Selector.open();
        buf = ByteBuffer.allocate(256);
        serverSocketChannel.bind(new InetSocketAddress(8188));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (serverSocketChannel.isOpen()){
            selector.select();
            Set<SelectionKey> keySet = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keySet.iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                if (key.isAcceptable()){
                    handleAccept(key);
                }
                if (key.isReadable()){
                    handleRead(key);
                }
                iterator.remove();
            }
        }
    }

    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        StringBuilder builder = new StringBuilder();
        buf.clear();
        int read;
        while (true){
            read = channel.read(buf);
            if (read == -1){
                channel.close();
                break;
            }
            if (read == 0){
                break;
            }
            buf.flip();
            while (buf.hasRemaining()){
                builder.append((char)buf.get());
            }
            buf.clear();
        }
        System.out.println("Received: " + builder);
        String msg = builder.toString().trim();
        if (msg.equals("ls")) {
            String files = Files.list(root).map(path -> path.getFileName().toString()).collect(Collectors.joining("\r\n"));
            ByteBuffer response = ByteBuffer.wrap(files.getBytes(StandardCharsets.UTF_8));
            channel.write(response);
        }
        else if (msg.startsWith("touch")){
            String filename = msg.replaceAll("touch ", "").trim();
            Path newFile = Files.createFile(Paths.get( root.toString(), filename));
        }
        else if (msg.startsWith("mkdir")){
            String dirname = msg.replaceAll("mkdir","").trim();
            Path newDir = Files.createDirectory(Paths.get(root.toString(), dirname));
        }
        else if (msg.startsWith("cat")){
                String filename = msg.replaceAll("cat ","").trim();
                byte[] bites = Files.readAllBytes(Paths.get("dir",filename));
                String msg1 = new String(bites,"utf8");
                ByteBuffer response =  ByteBuffer.wrap(msg1.getBytes(StandardCharsets.UTF_8));
                channel.write(response);
            }
        else if (msg.startsWith("cd")){
            String command = msg.replaceAll("cd ","").trim();
            if (command.equals("..")){
                root = root.getParent();
            }
            else if (Files.isDirectory(root.resolve(command))){
                root = root.resolve(command);
            } else {
                ByteBuffer response = ByteBuffer.wrap((command + " is not directory\r\n").getBytes(StandardCharsets.UTF_8));
            }
        }
        else {
            ByteBuffer response = ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8));
            channel.write(response);
        }
        printPath(channel);
    }
    private void handleAccept(SelectionKey key) throws IOException {
        SocketChannel channel = serverSocketChannel.accept();
        channel.configureBlocking(false);
        channel.register(selector,SelectionKey.OP_READ);
    }

    private void printPath (SocketChannel channel) throws IOException {
        String path = root.toString() + " ";
        ByteBuffer response = ByteBuffer.wrap(path.getBytes(StandardCharsets.UTF_8));
        channel.write(response);
    }

    public static void main(String[] args) throws IOException {
        new ServerNIO();
    }
}

