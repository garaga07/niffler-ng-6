package guru.qa.niffler.test.fake;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.model.rest.UserJson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OAuthTest {

    @Test
    @ApiLogin(username = "eldon.mante", password = "12345")
    void oauthTest(@Token String token, UserJson user) {
        System.out.println(user);
        Assertions.assertNotNull(token);
    }
}