package guru.qa.niffler.api;

import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendClient;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class SpendApiClient extends RestClient implements SpendClient {

    private final SpendApi spendApi;

    public SpendApiClient() {
        super(CFG.spendUrl());
        this.spendApi = retrofit.create(SpendApi.class);
    }

    @Nonnull
    @Override
    public SpendJson createSpend(@Nonnull SpendJson spend) {
        final Response<SpendJson> response;
        try {
            response = spendApi.addSpend(spend)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(201, response.code(), "Ожидался код 201 для создания траты");
        return requireNonNull(response.body(), "Ответ API вернул null при создании траты");
    }

    @Nullable
    public SpendJson editSpend(@Nonnull SpendJson spend) {
        final Response<SpendJson> response;
        try {
            response = spendApi.editSpend(spend)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

    @Nullable
    public SpendJson getSpend(@Nonnull String id) {
        final Response<SpendJson> response;
        try {
            response = spendApi.getSpend(id)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

    @Nonnull
    public List<SpendJson> allSpends(@Nonnull String username,
                                     @Nullable CurrencyValues currency,
                                     @Nullable String from,
                                     @Nullable String to) {
        final Response<List<SpendJson>> response;
        try {
            response = spendApi.allSpends(username, currency, from, to)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body() != null
                ? response.body()
                : Collections.emptyList();
    }

    public void removeSpends(@Nonnull String username, @Nonnull String... ids) {
        final Response<Void> response;
        try {
            response = spendApi.removeSpends(username, Arrays.asList(ids))
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
    }

    @Nonnull
    @Override
    public CategoryJson createCategory(@Nonnull CategoryJson category) {
        final Response<CategoryJson> response;
        try {
            response = spendApi.addCategory(category)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code(), "Ожидался код 200 для создания категории");
        return requireNonNull(response.body(), "Ответ API вернул null при создании категории");
    }

    @Override
    public void removeCategory(@Nonnull CategoryJson category) {
        throw new UnsupportedOperationException("Этот метод API не поддерживается.");
    }

    @Nullable
    public CategoryJson updateCategory(@Nonnull CategoryJson category) {
        final Response<CategoryJson> response;
        try {
            response = spendApi.updateCategory(category)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

    @Nonnull
    public List<CategoryJson> allCategory(@Nonnull String username) {
        final Response<List<CategoryJson>> response;
        try {
            response = spendApi.allCategories(username)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body() != null
                ? response.body()
                : Collections.emptyList();
    }
}