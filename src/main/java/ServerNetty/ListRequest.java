package ServerNetty;

public class ListRequest extends AbstractCommand{

    //private final int idClient;

    //public ListRequest(int idClient) {
    //    this.idClient = idClient;
    //}

    @Override
    public CommandType getType() {
        return CommandType.LIST_REQUEST;
    }
}
