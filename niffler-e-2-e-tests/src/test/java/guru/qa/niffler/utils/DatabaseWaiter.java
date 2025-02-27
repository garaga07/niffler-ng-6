package guru.qa.niffler.utils;

import guru.qa.niffler.data.repository.impl.UserdataUserRepositoryHibernate;
import guru.qa.niffler.model.rest.UserJson;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class DatabaseWaiter {

    private static final UserdataUserRepositoryHibernate userRepository = new UserdataUserRepositoryHibernate();

    public static UserJson waitForUserInDatabase(String username, long timeoutMs) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        UserJson[] result = new UserJson[1];

        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < timeoutMs) {
            userRepository.findByUsername(username).ifPresent(user -> {
                result[0] = UserJson.fromEntity(user, null);
                latch.countDown();
            });
            if (latch.getCount() == 0) break;
            Thread.sleep(100);
        }

        latch.await(timeoutMs, TimeUnit.MILLISECONDS);
        return result[0];
    }
}
