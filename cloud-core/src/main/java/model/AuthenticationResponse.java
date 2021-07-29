package model;


import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class AuthenticationResponse extends AbstractCommand{

    private final String userName;
    private final int userId;

    public AuthenticationResponse(String userName, int userId) {
        this.userName = userName;
        this.userId = userId;
    }


    @Override
    public CommandType getType() {
        return CommandType.AUTHENTICATION_RESP;
    }
}
