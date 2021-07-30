package model;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AuthenticationRequest extends AbstractCommand{

    private final String login;
    private final String password;

    public AuthenticationRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }

    @Override
    public CommandType getType() {
        return CommandType.AUTHENTICATION;
    }
}
