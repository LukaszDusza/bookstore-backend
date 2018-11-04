package devlab.app.auth.User;


import javax.persistence.*;


//@NoArgsConstructor
//@AllArgsConstructor
//@Getter
//@Setter
@Entity
@Table(name = "user")
public class UserApp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String username;
    private String password;

    public UserApp(String username, String password) {
        this.username = username;
        this.password = password;
    }

    //konstruktor bez arg. dla EntityManager'a
    public UserApp() { }



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

