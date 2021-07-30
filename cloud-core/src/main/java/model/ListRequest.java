package model;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ListRequest extends AbstractCommand{

    private final int idClient;
    private final String nameClient;

    public ListRequest(int idClient, String nameClient) {
        this.idClient = idClient;
        this.nameClient = nameClient;

    }

    @Override
    public CommandType getType() {
        return CommandType.LIST_REQUEST;
    }
}
