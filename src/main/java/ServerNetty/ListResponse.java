package ServerNetty;

import lombok.Getter;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString

public class ListResponse extends AbstractCommand{

    private final List<String> listFromServer;

    public ListResponse (Path path) throws IOException {
        listFromServer = Files.list(path)
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());
    }

    @Override
    public CommandType getType() {
        return CommandType.LIST_MESSAGE;
    }
}
