package model;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class GoToDir extends AbstractCommand{

    private String dirName;

    public GoToDir(String dirName) {
        this.dirName = dirName;
    }

    @Override
    public CommandType getType() {
        return CommandType.GO_TO_DIR;
    }
}
