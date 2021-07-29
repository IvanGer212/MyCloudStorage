package handler;

import DB.AuthenticationService;
import DB.UsersFilesOnServer;
import DB.Users_Repository;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import model.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Slf4j
public class MessageHandler extends SimpleChannelInboundHandler<AbstractCommand> {

    private Path serverRoot;

    public MessageHandler() throws IOException {
        serverRoot = Paths.get("server_dir");
        if (!Files.exists(serverRoot)){
            Files.createDirectories(serverRoot);
        }
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
            ctx.writeAndFlush(new ListResponse(serverRoot));
            break;
        case FILE_REQUEST:
            FileRequest fileRequest = (FileRequest) command;
            try (FileOutputStream fos = new FileOutputStream(fileRequest.getClientDir()+"/"+fileRequest.getFileName())){
                fos.write(fileRequest.getFileData());
            }
            ctx.writeAndFlush(new ListResponse(serverRoot));
            break;
        case LIST_REQUEST:
            ListRequest listRequest = (ListRequest) command;
            UsersFilesOnServer usersFilesOnServer = new UsersFilesOnServer();
            Optional<UsersFilesOnServer.ParentDir> usersParentDirOnServer = usersFilesOnServer.getUsersParentDirOnServer(listRequest.getIdClient());
            if (serverRoot.equals(Paths.get("server_dir"))){
                if (!usersParentDirOnServer.get().getDirName().equals("")) {
                    serverRoot = Paths.get(usersParentDirOnServer.get().getDirName());
                }
                else {
                    String newDir = listRequest.getNameClient()+"_Dir";
                    Files.createDirectories(Paths.get(newDir));
                    usersFilesOnServer.setUsersParentDirOnServer(listRequest.getIdClient(),newDir);
                    Optional<UsersFilesOnServer.ParentDir> usersParentDirOnServer1 = usersFilesOnServer.getUsersParentDirOnServer(listRequest.getIdClient());
                    serverRoot = Paths.get(usersParentDirOnServer1.get().getDirName());
                }
            }
            ctx.writeAndFlush(new ResponseServerDir(serverRoot.toString()));
            ctx.writeAndFlush(new ListResponse(serverRoot));

            break;

        case DELETE_REQUEST:
            FileDeleter deleter = (FileDeleter) command;
            String filename = deleter.getFilename();
            String dirDel = deleter.getDir();
            Path file = Paths.get(dirDel,filename);
            Files.delete(file);
            ctx.writeAndFlush(new ListResponse(serverRoot));
            break;
        case RENAME_REQUEST:
            {
                RenameRequest renameRequest = (RenameRequest) command;
                String newFilename = renameRequest.getNewFilename();
                String dirRename = renameRequest.getDir();
                Path source = Paths.get(dirRename,renameRequest.getName());
                Files.move(source,source.resolveSibling(newFilename)).toString();
                ctx.writeAndFlush(new ListResponse(serverRoot));
            break;
            }

        case DIR_CREATE:
            DirCreater dirCreater = (DirCreater) command;
            String dir1 = dirCreater.getDir();
            String newDir = dirCreater.getNewDir();
            Files.createDirectories(Paths.get(dir1,newDir));
            ctx.writeAndFlush(new ListResponse(serverRoot));
            break;

        case UP_SERVER_DIR:
            if (serverRoot.getParent() != null){
                serverRoot = serverRoot.getParent();
            }
            ctx.writeAndFlush(new ResponseServerDir(serverRoot.toString()));
            ctx.writeAndFlush(new ListResponse(serverRoot));
            break;
        case GO_TO_DIR:
            GoToDir goToDir = (GoToDir) command;
            Path newPath = serverRoot.resolve(goToDir.getDirName());
            if (Files.isDirectory(newPath)){
                serverRoot = newPath;
            }
            ctx.writeAndFlush(new ResponseServerDir(serverRoot.toString()));
            ctx.writeAndFlush(new ListResponse(serverRoot));
            break;
        case AUTHENTICATION:
            AuthenticationRequest authenticationRequest = (AuthenticationRequest) command;
            String login = authenticationRequest.getLogin();
            String password = authenticationRequest.getPassword();
            AuthenticationService authenticationService = new AuthenticationService();
            Optional<AuthenticationService.Entry> entryForAuthentication = authenticationService.getEntryForAuthentication(login, password);
            if (entryForAuthentication.isPresent()){
                String userName = entryForAuthentication.get().getName();
                int userId = entryForAuthentication.get().getIdClient();
                ctx.writeAndFlush(new AuthenticationResponse(userName,userId));
            } else
                ctx.writeAndFlush(new Message_Response("Incorrect login or password"));
            break;
        case REGISTRATION_REQUEST:
            Registration_Req registration_req = (Registration_Req) command;
            String nameNewClient = registration_req.getName();
            String passwordNewClient = registration_req.getPassword();
            String loginNewClient = registration_req.getLogin();
            String newClientDir = nameNewClient+"_Dir";
            Users_Repository users_repository = new Users_Repository();
            boolean resultCheckUser = users_repository.checkUserOnServer(loginNewClient);
            if (resultCheckUser){
                ctx.writeAndFlush(new RegistrationResponse("User with that Login already exist!"));
            }
            else {
                boolean resultOfCreate = users_repository.createNewClientInBase(nameNewClient, loginNewClient, passwordNewClient, newClientDir);
                if (resultOfCreate) {
                    Files.createDirectories(Paths.get(newClientDir));
                    ctx.writeAndFlush(new RegistrationResponse("New client create on server!"));
                } else ctx.writeAndFlush(new RegistrationResponse("Error while create new client!"));
            }
    }
    }
}
