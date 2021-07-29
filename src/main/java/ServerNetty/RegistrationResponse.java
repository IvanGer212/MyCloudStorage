package ServerNetty;

import lombok.Getter;

@Getter
public class RegistrationResponse extends AbstractCommand{
    private final String msg;

    public RegistrationResponse(String msg) {
        this.msg = msg;
    }

    @Override
    public CommandType getType() {
        return CommandType.REGISTRATION_RESPONSE;
    }
}
