package ServerNetty;

import lombok.Getter;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Path;
@ToString
@Getter
public class RenameRequest extends AbstractCommand{
    private final String name;
    private final String newFilename;
    private final String dir;

    public RenameRequest(Path path, String filename) throws IOException {
        name = path.getFileName().toString();
        newFilename = filename;
        dir = path.getParent().toString();

    }

    @Override
    public CommandType getType() {
        return CommandType.RENAME_REQUEST;
    }
}
