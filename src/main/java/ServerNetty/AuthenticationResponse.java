package ServerNetty;

import DB.AuthenticationService;
import lombok.Getter;
import lombok.ToString;

import java.util.Optional;

@ToString
@Getter
public class AuthenticationResponse extends AbstractCommand{

    private final int idClient;
    private final String nameClient;

    public AuthenticationResponse(int idClient, String nameClient) {
        this.idClient = idClient;
        this.nameClient = nameClient;
    }


    @Override
    public CommandType getType() {
        return CommandType.AUTHENTICATION_RESP;
    }
}
