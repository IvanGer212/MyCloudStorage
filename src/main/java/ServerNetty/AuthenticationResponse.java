package ServerNetty;

import DB.AuthenticationService;
import lombok.Getter;
import lombok.ToString;

import java.util.Optional;

@ToString
@Getter
public class AuthenticationResponse extends AbstractCommand{

    private final Optional<AuthenticationService.Entry> entry;

    public AuthenticationResponse(Optional<AuthenticationService.Entry> entry) {
        this.entry = entry;
    }


    @Override
    public CommandType getType() {
        return CommandType.AUTHENTICATION_RESP;
    }
}
