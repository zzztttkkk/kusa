import java.util.Date;

@JsonReflectSafe
public class User extends Animal {
    private String name;
    @JsonAlias("-")
    private Date date;

    public User(String name) {
        super();
        this.name = name;
        this.date = new Date();
    }

    void speak(String msg) {
        System.out.println(msg);
    }
}
