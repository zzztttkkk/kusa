@JsonReflectSafe
public class User extends Animal {
    private String name;

    public User(String name) {
        super();
        this.name = name;
    }

    void speak(String msg) {
        System.out.println(msg);
    }
}
