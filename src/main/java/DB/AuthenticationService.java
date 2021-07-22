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

        public static class Entry {
            private String name;
            private String login;
            private String password;

            public Entry (String name, String login, String password) {
                this.name = name;
                this.login = login;
                this.password = password;
            }
        }
}
