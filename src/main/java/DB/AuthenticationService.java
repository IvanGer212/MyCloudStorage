package DB;

import lombok.Getter;
import lombok.ToString;

import java.util.Optional;

@Getter
@ToString
public class AuthenticationService {

    public Optional<Entry> getEntryForAuthentication (String login, String password){
            Users_Repository users_repository = new Users_Repository();
            return users_repository.getNameForAuthentication(login, password);
    }
        @Getter
        public static class Entry {
            private final int idClient;
            private final String name;
            private final String login;
            private final String password;

            public Entry (int id, String name, String login, String password) {
                this.idClient = id;
                this.name = name;
                this.login = login;
                this.password = password;
            }
        }
}
