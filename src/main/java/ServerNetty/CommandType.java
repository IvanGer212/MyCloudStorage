package ServerNetty;

import java.io.Serializable;

public enum CommandType {
    FILE_REQUEST,
    FILE_MESSAGE,
    LIST_REQUEST,
    LIST_MESSAGE,
    DELETE_REQUEST,
    RENAME_REQUEST,
    DIR_CREATE,
    UP_SERVER_DIR,
    REQUEST_SERVER_DIR,
    GO_TO_DIR,
    REFRESH_FILE_LIST,
    AUTHENTICATION,
    AUTHENTICATION_RESP

}
