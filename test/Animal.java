import java.util.concurrent.ThreadLocalRandom;

@JsonReflectSafe
public class Animal {
    private boolean gender;
    private int age;
    private int height;
    private int weight;
    private float posX;
    private float posY;

    Animal() {
        gender = ThreadLocalRandom.current().nextBoolean();
        age = ThreadLocalRandom.current().nextInt(0, 101);
        height = ThreadLocalRandom.current().nextInt(30, 200);
        weight = ThreadLocalRandom.current().nextInt(6, 200);
        posY = ThreadLocalRandom.current().nextFloat();
        posX = ThreadLocalRandom.current().nextFloat();
    }

    void run(int x, int y) {
        posX += x;
        posY += y;
    }
}
