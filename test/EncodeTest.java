import org.junit.jupiter.api.Test;

public class EncodeTest {
    @Test
    void string() {
        JsonString A = new JsonString("Az\r\n\"-贴的🎀🎀");
        System.out.println(JSON.parse(JSON.stringify(A)).String().get().equals(A.get()));
    }
}
