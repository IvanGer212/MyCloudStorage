package ServerNetty;

import lombok.Getter;
import lombok.ToString;

import java.nio.file.Path;

@ToString
@Getter
public class FileDeleter extends AbstractCommand{

    private final String filename;
    private final String dir;

    public FileDeleter(Path path) {
        filename = path.getFileName().toString();
        dir = path.getParent().toString();
    }

    @Override
    public CommandType getType() {
        return CommandType.DELETE_REQUEST;
    }
}
