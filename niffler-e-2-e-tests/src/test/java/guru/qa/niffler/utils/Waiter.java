package guru.qa.niffler.utils;

import java.util.function.Supplier;

public class Waiter {

    public static <T> T waitForCondition(Supplier<T> condition, long timeoutMs) throws InterruptedException {
        long start = System.currentTimeMillis();

        while (System.currentTimeMillis() < start + timeoutMs) {
            T result = condition.get();
            if (result != null) {
                return result;
            }
            Thread.sleep(100);
        }

        return null;
    }
}
