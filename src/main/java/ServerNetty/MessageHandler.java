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
            try (FileOutputStream fos = new FileOutputStream(message.getServerDir()+"/"+message.getName())){
                fos.write(message.getData());
            }
            break;
        case FILE_REQUEST:
            break;
        case LIST_MESSAGE:
            break;
        case LIST_REQUEST:
            break;
        case DELETE_REQUEST:
            FileDeleter deleter = (FileDeleter) command;
            String filename = deleter.getFilename();
            String dirDel = deleter.getDir();
            Path file = Paths.get(dirDel,filename);
            Files.delete(file);
            break;
        case RENAME_REQUEST:
            {
                RenameRequest renameRequest = (RenameRequest) command;
                String newFilename = renameRequest.getNewFilename();
                String dirRename = renameRequest.getDir();
                Path source = Paths.get(dirRename,renameRequest.getName());
                Files.move(source,source.resolveSibling(newFilename)).toString();
            break;
            }
        case FILE_CREATE:
            FileCreater fileCreater = (FileCreater) command;
            String filename1 = fileCreater.getFilename();
            String dirCreate = fileCreater.getDirName();
            Files.createFile(Paths.get(dirCreate, filename1));
            break;
        case DIR_CREATE:
            DirCreater dirCreater = (DirCreater) command;
            String dir1 = dirCreater.getDir();
            String newDir = dirCreater.getNewDir();
            Files.createDirectories(Paths.get(dir1,newDir));
            break;
    }
    }
}
