import java.util.Date;

public class SerializationTest {
    @org.junit.jupiter.api.Test
    void getFans() {
        for (Fan field : reflect.getFields(User.class)) {
            System.out.println(field.getKey());
        }
    }

    @org.junit.jupiter.api.Test
    void toJson() {
        User tue = new User("tue我");
        System.out.println(API.serialize(tue));

        System.out.println(API.serialize("🎆🎇🧨🎈"));
        System.out.println(Types.Ary.getClass());
        System.out.println(new Date().getTime());
        System.out.println(Date.class.getName());
    }
}
