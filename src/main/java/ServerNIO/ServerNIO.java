package ServerNIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class ServerNIO {
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private ByteBuffer buf;

    public ServerNIO() throws IOException {
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
        ByteBuffer response = ByteBuffer.wrap(builder.toString().getBytes(StandardCharsets.UTF_8));
        channel.write(response);
    }
    private void handleAccept(SelectionKey key) throws IOException {
        SocketChannel channel = serverSocketChannel.accept();
        channel.configureBlocking(false);
        channel.register(selector,SelectionKey.OP_READ);
    }

    public static void main(String[] args) throws IOException {
        new ServerNIO();
    }
}
