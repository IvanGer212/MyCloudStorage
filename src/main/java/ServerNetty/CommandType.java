package ServerNetty;

import java.io.Serializable;

public enum CommandType {
    FILE_REQUEST,
    FILE_MESSAGE,
    LIST_REQUEST,
    LIST_MESSAGE,
    DELETE_REQUEST,
    RENAME_REQUEST,
    FILE_CREATE,
    DIR_CREATE

}
