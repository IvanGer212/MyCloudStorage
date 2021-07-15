package ServerNetty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class MessageHandler extends SimpleChannelInboundHandler<AbstractCommand> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractCommand command) throws Exception {
    log.debug("received {}", command);
    switch (command.getType()){
        case FILE_MESSAGE:
            FileMessage message = (FileMessage) command;
            try (FileOutputStream fos = new FileOutputStream("server_dir/"+message.getName())){
                fos.write(message.getData());
            }
            break;
        case FILE_REQUEST:
            break;
        case LIST_MESSAGE:
            break;
        case LIST_REQuEST:
            break;
        case DELETE_REQUEST:
            FileDeleter deleter = (FileDeleter) command;
            String filename = deleter.getFilename();
            Path file = Paths.get("server_dir",filename);
            Files.delete(file);
            break;
        case RENAME_REQUEST:
            {
                RenameRequest renameRequest = (RenameRequest) command;
                String newfilename = renameRequest.getNewFilename();
                Path source = Paths.get("server_dir",renameRequest.getName());
                Files.move(source,source.resolveSibling(newfilename)).toString();
            break;
            }
        case FILE_CREATE:
            FileCreater fileCreater = (FileCreater) command;
            String filename1 = fileCreater.getFilename();
            Files.createFile(Paths.get("server_dir", filename1));
            break;
        case DIR_CREATE:
            break;
    }
    }
}
