package ServerNetty;

import lombok.Getter;
import lombok.ToString;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

@ToString
@Getter
public class FileMessage extends AbstractCommand{
    public FileMessage(Path path, String serverDir) throws IOException {

        name = path.getFileName().toString();
        size = Files.size(path);
        data = Files.readAllBytes(path);
        this.serverDir = serverDir;
    }

    private final String name;
    private final long size;
    private final byte[] data;
    private final String serverDir;

    @Override
    public CommandType getType() {
        return CommandType.FILE_MESSAGE;
    }
}
