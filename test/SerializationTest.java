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
        System.out.println(Kusa.serialize(tue));
        System.out.println(Kusa.serialize("🎆🎇🧨🎈"));
    }
}
