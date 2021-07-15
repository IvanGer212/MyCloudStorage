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

    public RenameRequest(Path path, String filename) throws IOException {
        name = path.getFileName().toString();
        newFilename = filename;
    }

    @Override
    public CommandType getType() {
        return CommandType.RENAME_REQUEST;
    }
}
