package ServerNetty;

import lombok.Getter;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Getter
@ToString
public class FileCreater extends AbstractCommand {

    private final String filename;
    private final String dirName;

    public FileCreater(Path path) throws IOException {
        filename = path.getFileName().toString();
        dirName = path.getParent().toString();
    }

    @Override
    public CommandType getType() {
        return CommandType.FILE_CREATE;
    }
}
