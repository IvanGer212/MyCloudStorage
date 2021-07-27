package ServerNetty;

import DB.AuthenticationService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Slf4j
public class MessageHandler extends SimpleChannelInboundHandler<AbstractCommand> {

    private Path serverRoot;

    public MessageHandler() {
        serverRoot = Paths.get("server_dir");
    }

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
            FileRequest fileRequest = (FileRequest) command;
            try (FileOutputStream fos = new FileOutputStream(fileRequest.getClientDir()+"/"+fileRequest.getFileName())){
                fos.write(fileRequest.getFileData());
            }
            break;
        case LIST_MESSAGE:
            ListResponse listResponse = (ListResponse) command;
            ctx.writeAndFlush(listResponse.getListFromServer());
            break;
        case LIST_REQUEST:
            //ListRequest listRequest = (ListRequest) command;
            ctx.writeAndFlush(new ListResponse(serverRoot));

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

        case DIR_CREATE:
            DirCreater dirCreater = (DirCreater) command;
            String dir1 = dirCreater.getDir();
            String newDir = dirCreater.getNewDir();
            Files.createDirectories(Paths.get(dir1,newDir));
            break;

        case UP_SERVER_DIR:
            if (serverRoot.getParent() != null){
                serverRoot = serverRoot.getParent();
            }
            //ctx.writeAndFlush(new ResponseServerDir(serverRoot.toString()));
            ctx.writeAndFlush(new ListResponse(serverRoot));
            break;
        case GO_TO_DIR:
            GoToDir goToDir = (GoToDir) command;
            Path newPath = serverRoot.resolve(goToDir.getDirName());
            if (Files.isDirectory(newPath)){
                serverRoot = newPath;
            }
            //ctx.writeAndFlush(new ResponseServerDir(serverRoot.toString()));
            ctx.writeAndFlush(new ListResponse(serverRoot));
            break;
        case REFRESH_FILE_LIST:
            //ctx.writeAndFlush(new ResponseServerDir(serverRoot.toString()));
            ctx.writeAndFlush(new ListResponse(serverRoot));
            break;
        case AUTHENTICATION:
            AuthenticationRequest authenticationRequest = (AuthenticationRequest) command;
            String login = authenticationRequest.getLogin();
            String password = authenticationRequest.getPassword();
            AuthenticationService authenticationService = new AuthenticationService();
            Optional<AuthenticationService.Entry> entryForAuthentication = authenticationService.getEntryForAuthentication(login, password);
            if (entryForAuthentication.isPresent()){
                ctx.writeAndFlush(new AuthenticationResponse(entryForAuthentication));
            } else
            {ctx.writeAndFlush(null);}
            break;

    }
    }
}
