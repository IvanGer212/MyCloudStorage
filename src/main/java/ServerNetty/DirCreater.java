package ServerNetty;

import lombok.Getter;
import lombok.ToString;

import java.nio.file.Path;

@ToString
@Getter
public class DirCreater extends AbstractCommand{
    private final String dir;
    private final String newDir;

    public DirCreater(Path path, String newDir) {
        dir = path.toAbsolutePath().toString();
        this.newDir = newDir;
    }

    @Override
    public CommandType getType() {
        return CommandType.DIR_CREATE;
    }
}
