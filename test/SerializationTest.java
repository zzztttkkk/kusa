import java.util.ArrayList;

public class SerializationTest {
    @org.junit.jupiter.api.Test
    void getFans() {
        for (Fan field : reflect.getFields("User")) {
            System.out.println(field.getKey());
        }
    }

    @org.junit.jupiter.api.Test
    void toJson() {
        User tue = new User("tue我");

        JsonItem item = Serializer.toJson(tue);

        System.out.println(JSON.stringify(item));
    }
}
