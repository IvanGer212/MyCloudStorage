package ServerNetty;

import DB.AuthenticationService;
import lombok.Getter;
import lombok.ToString;

import java.util.Optional;

@ToString
@Getter
public class AuthenticationResponse extends AbstractCommand{

    private final String userName;
    private final int userId;

    public AuthenticationResponse(Optional<AuthenticationService.Entry> entry) {
        userName = entry.get().getName();
        userId = entry.get().getIdClient();
    }


    @Override
    public CommandType getType() {
        return CommandType.AUTHENTICATION_RESP;
    }
}
