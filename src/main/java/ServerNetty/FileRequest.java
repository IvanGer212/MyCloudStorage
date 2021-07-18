package ServerNetty;

import lombok.Getter;
import lombok.ToString;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.PrimitiveIterator;

@ToString
@Getter
public class FileRequest extends AbstractCommand{

    private final String fileName;
    private final long fileSize;
    private final byte[] fileData;
    private final String clientDir;

    public FileRequest(Path path, String clientDir) throws IOException {
        fileName = path.getFileName().toString();
        fileSize = Files.size(path);
        fileData = Files.readAllBytes(path);
        this.clientDir = clientDir;
    }

    @Override
    public CommandType getType() {
        return CommandType.FILE_REQUEST;
    }
}
