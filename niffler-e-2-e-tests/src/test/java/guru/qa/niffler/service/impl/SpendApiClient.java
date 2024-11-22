package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.SpendApi;
import guru.qa.niffler.api.core.RestClient.EmptyClient;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.CurrencyValues;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.service.SpendClient;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class SpendApiClient implements SpendClient {

    private static final Config CFG = Config.getInstance();
    private final SpendApi spendApi = new EmptyClient(CFG.spendUrl()).create(SpendApi.class);

    @Override
    @Nonnull
    public SpendJson createSpend(SpendJson spend) {
        final Response<SpendJson> response;
        try {
            response = spendApi.addSpend(spend)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(201, response.code());
        return requireNonNull(response.body());
    }

    @Override
    @Nonnull
    public CategoryJson createCategory(CategoryJson category) {
        final Response<CategoryJson> response;
        try {
            response = spendApi.addCategory(category)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        CategoryJson result = requireNonNull(response.body());

        return category.archived()
                ? updateCategory(
                new CategoryJson(
                        result.id(),
                        result.name(),
                        result.username(),
                        true
                )
        ) : result;
    }

    @Override
    @Nonnull
    public CategoryJson updateCategory(CategoryJson category) {
        final Response<CategoryJson> response;
        try {
            response = spendApi.updateCategory(category)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return requireNonNull(response.body());
    }

    @Override
    public void removeCategory(CategoryJson category) {
        throw new UnsupportedOperationException("Can`t remove category using API");
    }

    @Nonnull
    public List<SpendJson> getAllSpends(String username, CurrencyValues filterCurrency, String from, String to) {
        final Response<List<SpendJson>> response;
        try {
            response = spendApi.allSpends(username, filterCurrency, from, to)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return requireNonNull(response.body());
    }

    @Nonnull
    public SpendJson getSpendById(String id) {
        final Response<SpendJson> response;
        try {
            response = spendApi.getSpend(id)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return requireNonNull(response.body());
    }

    public void removeSpends(String username, List<String> ids) {
        final Response<Void> response;
        try {
            response = spendApi.removeSpends(username, ids)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(204, response.code());
    }

    @Nonnull
    public List<CategoryJson> getAllCategories(String username) {
        final Response<List<CategoryJson>> response;
        try {
            response = spendApi.allCategories(username)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return requireNonNull(response.body());
    }
}