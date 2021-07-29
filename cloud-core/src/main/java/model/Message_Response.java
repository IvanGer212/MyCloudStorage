package model;

import lombok.Getter;

@Getter
public class Message_Response extends AbstractCommand{
    private final String msg;

    public Message_Response(String msg) {
        this.msg = msg;
    }

    @Override
    public CommandType getType() {
        return CommandType.MESSAGE_RESPONSE;
    }
}
