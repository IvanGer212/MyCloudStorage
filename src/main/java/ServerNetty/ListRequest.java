package ServerNetty;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class ListRequest extends AbstractCommand{

   private final int idClient;

    public ListRequest(int idClient) {
        this.idClient = idClient;
    }

    @Override
    public CommandType getType() {
        return CommandType.LIST_REQUEST;
    }
}
