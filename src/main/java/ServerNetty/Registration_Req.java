package ServerNetty;

import lombok.Getter;

@Getter
public class Registration_Req extends AbstractCommand {
    private final String name;
    private final String login;
    private final String password;

    public Registration_Req(String name, String login, String password) {
        this.name = name;
        this.login = login;
        this.password = password;
    }

    @Override
    public CommandType getType() {
        return CommandType.REGISTRATION_REQUEST;
    }
}
