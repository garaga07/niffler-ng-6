package guru.qa.niffler.test.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.AllPeopleQuery;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class AllPeopleDepthGraphQlTest extends BaseGraphQlTest {

    @User()
    @Test
    @ApiLogin
    void allPeopleQueryShouldReturnDepthExceededError(@Token String bearerToken) {
        // Создание GraphQL-запроса
        final AllPeopleQuery query = AllPeopleQuery.builder()
                .page(0)
                .size(10)
                .build();

        // Выполнение запроса с добавлением заголовка авторизации
        final ApolloCall<AllPeopleQuery.Data> queryCall = apolloClient.query(query)
                .addHttpHeader("authorization", bearerToken);

        final ApolloResponse<AllPeopleQuery.Data> response = Rx2Apollo.single(queryCall).blockingGet();

        // Извлечение ошибок из ответа
        List<com.apollographql.apollo.api.Error> errors = Optional.ofNullable(response.errors)
                .orElse(Collections.emptyList());

        // Проверка текста ошибки
        Assertions.assertFalse(errors.isEmpty(), "Errors list should not be empty");
        String actualErrorMessage = errors.getFirst().getMessage();
        Assertions.assertEquals(
                "Can`t fetch over 2 friends sub-queries",
                actualErrorMessage,
                "Error message should match the expected text for depth limit exceeded"
        );
    }
}