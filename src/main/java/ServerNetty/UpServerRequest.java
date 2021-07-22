package ServerNetty;

public class UpServerRequest extends AbstractCommand{
    @Override
    public CommandType getType() {
        return CommandType.UP_SERVER_DIR;
    }
}
