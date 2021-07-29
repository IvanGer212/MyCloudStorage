package model;

import lombok.Getter;
import lombok.ToString;


@Getter
@ToString
public class ResponseServerDir extends AbstractCommand{

    private final String nameServerDir;

    public ResponseServerDir(String nameServerDir) {

        this.nameServerDir = nameServerDir;
    }

    @Override
    public CommandType getType() {
        return CommandType.REQUEST_SERVER_DIR;
    }
}
