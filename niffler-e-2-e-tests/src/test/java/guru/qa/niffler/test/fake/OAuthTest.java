package guru.qa.niffler.test.fake;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.impl.AuthApiClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OAuthTest {
    private final AuthApiClient authApiClient = new AuthApiClient();

    @User
    @Test
    void oauthTest(UserJson user) {
        String token = authApiClient.doLogin(user.username(), user.testData().password());
        System.out.println(token);
        Assertions.assertNotNull(token);
    }
}