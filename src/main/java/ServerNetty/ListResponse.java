package ServerNetty;

import lombok.Getter;
import lombok.ToString;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString

public class ListResponse extends AbstractCommand{

    private final List<String> listFromServer;
    //private final String serverDir;

    public ListResponse (Path path) throws IOException {
        //this.listFromServer = listFromServer;
        listFromServer = Files.list(path)
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());

    }

    @Override
    public CommandType getType() {
        return CommandType.LIST_MESSAGE;
    }
}
