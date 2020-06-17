import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ReflectTest {
    @Test
    void mt() throws InterruptedException {
        ExecutorService executor = Executors.newCachedThreadPool();
        HashSet<Class<?>> clsMap = new HashSet<>();

        for (int i = 0; i < 10; i++) {
            executor.execute(
                    new Runnable() {
                        @Override
                        public void run() {
                            clsMap.add(Object.class);
                            System.out.println("E!");
                        }
                    }
            );
        }

        TimeUnit.SECONDS.sleep(1);

        System.out.println(clsMap.size());
    }
}
