package guru.qa.niffler.test.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.FriendsCategoriesAccessQuery;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class FriendsCategoriesAccessGraphQlTest extends BaseGraphQlTest {
    @User(friends = 1)
    @Test
    @ApiLogin
    void friendsCategoriesAccessShouldReturnErrorMessage(@Token String bearerToken) {
        // Создание GraphQL-запроса
        final FriendsCategoriesAccessQuery query = FriendsCategoriesAccessQuery.builder()
                .page(0)
                .size(10)
                .build();

        // Выполнение запроса с добавлением заголовка авторизации
        final ApolloCall<FriendsCategoriesAccessQuery.Data> queryCall = apolloClient.query(query)
                .addHttpHeader("authorization", bearerToken);

        final ApolloResponse<FriendsCategoriesAccessQuery.Data> response = Rx2Apollo.single(queryCall).blockingGet();

        // Извлечение ошибок из ответа
        List<com.apollographql.apollo.api.Error> errors = Optional.ofNullable(response.errors)
                .orElse(Collections.emptyList());

        // Проверка текста ошибки
        String actualErrorMessage = errors.getFirst().getMessage();
        Assertions.assertEquals(
                "Can`t query categories for another user",
                actualErrorMessage,
                "Error message should match the expected text"
        );
    }
}