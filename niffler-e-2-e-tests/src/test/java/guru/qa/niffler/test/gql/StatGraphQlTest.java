package guru.qa.niffler.test.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.StatQuery;
import guru.qa.niffler.jupiter.annotation.*;
import guru.qa.type.CurrencyValues;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StatGraphQlTest extends BaseGraphQlTest {
    @User
    @Test
    @ApiLogin
    void statTest(@Token String bearerToken) {
        final ApolloCall<StatQuery.Data> currenciesCall = apolloClient.query(StatQuery.builder()
                        .filterCurrency(null)
                        .statCurrency(null)
                        .filterPeriod(null)
                        .build())
                .addHttpHeader("authorization", bearerToken);
        final ApolloResponse<StatQuery.Data> response = Rx2Apollo.single(currenciesCall).blockingGet();
        final StatQuery.Data data = response.dataOrThrow();
        StatQuery.Stat result = data.stat;
        Assertions.assertEquals(
                0.0,
                result.total
        );
    }

    @User(
            categories = {
                    @Category(name = "Groceries"),
                    @Category(name = "Entertainment", archived = true)
            },
            spendings = {
                    @Spending(category = "Groceries", description = "Bought apples", amount = 50.0, currency = guru.qa.niffler.model.rest.CurrencyValues.USD),
                    @Spending(category = "Entertainment", description = "Movie tickets", amount = 100.0, currency = guru.qa.niffler.model.rest.CurrencyValues.USD)
            }
    )
    @Test
    @ApiLogin
    void statShouldIncludeSpendingsAndCategories(@Token String bearerToken) {
        // Создание GraphQL-запроса
        final ApolloCall<StatQuery.Data> statQueryCall = apolloClient.query(
                StatQuery.builder()
                        .filterPeriod(null)
                        .filterCurrency(CurrencyValues.USD)
                        .statCurrency(CurrencyValues.USD)
                        .build()
        ).addHttpHeader("authorization", bearerToken);

        final ApolloResponse<StatQuery.Data> response = Rx2Apollo.single(statQueryCall).blockingGet();

        // Проверка данных в ответе
        final StatQuery.Data data = response.dataOrThrow();
        StatQuery.Stat result = data.stat;

        Assertions.assertNotNull(result, "Stat response should not be null");
        Assertions.assertEquals(150.0, result.total, "Total should match the combined spending amount in USD");
        Assertions.assertFalse(result.statByCategories.isEmpty(), "Stat by categories should not be empty");

        // Проверка наличия категорий
        boolean hasGroceriesCategory = result.statByCategories.stream()
                .anyMatch(category -> category.categoryName.equals("Groceries") && category.sum == 50.0);
        Assertions.assertTrue(hasGroceriesCategory, "Groceries category should be included in the response");

        boolean hasArchivedCategory = result.statByCategories.stream()
                .anyMatch(category -> category.categoryName.equals("Archived") && category.sum == 100.0);
        Assertions.assertTrue(hasArchivedCategory, "Archived categories should be included in the response");
    }
}