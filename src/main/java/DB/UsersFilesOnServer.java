package DB;

import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Optional;
@Getter
@ToString
public class UsersFilesOnServer {

    public Optional<ParentDir> getUsersParentDirOnServer(int idClient) {
        Users_Repository users_repository = new Users_Repository();
        return users_repository.getUsersParentDirOnServer(idClient);
    }
    @Getter
    public static class ParentDir{
        private final String dirName;

        public ParentDir(String dirName) {
            this.dirName = dirName;
        }
    }

}
