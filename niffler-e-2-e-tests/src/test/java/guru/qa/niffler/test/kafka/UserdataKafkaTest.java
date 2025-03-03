package guru.qa.niffler.test.kafka;

import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.repository.impl.UserdataUserRepositoryHibernate;
import guru.qa.niffler.jupiter.annotation.meta.KafkaTest;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.utils.Waiter;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

@KafkaTest
public class UserdataKafkaTest {

    private static final Config CFG = Config.getInstance();
    private final AuthApi authApi = new RestClient.EmptyClient(CFG.authUrl()).create(AuthApi.class);
    private final UserdataUserRepositoryHibernate userRepository = new UserdataUserRepositoryHibernate();

    @Test
    void userShouldBeSavedToDatabaseAfterKafkaProcessing() throws Exception {
        final String username = RandomDataUtils.randomUsername();
        final String password = "12345";

        // Регистрация пользователя через Auth API
        authApi.requestRegisterForm().execute();
        authApi.register(username, password, password, ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")).execute();

        // Ожидаем появления пользователя в БД `userdata`
        UserJson userFromDb = Waiter.waitForCondition(
                () -> userRepository.findByUsername(username)
                        .map(u -> UserJson.fromEntity(u, null))
                        .orElse(null),
                5000L
        );

        // Проверяем, что пользователь появился в `userdata`
        Assertions.assertNotNull(userFromDb, "Пользователь не найден в БД `userdata`");
        Assertions.assertEquals(username, userFromDb.username());
    }
}