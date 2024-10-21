package guru.qa.niffler.api;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendClient;
import io.qameta.allure.Step;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class SpendApiClient implements SpendClient {

    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Config.getInstance().spendUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final SpendApi spendApi = retrofit.create(SpendApi.class);

    @Step("Создание новой траты")
    public @Nullable SpendJson createSpend(SpendJson spend) {
        final Response<SpendJson> response;
        try {
            response = spendApi.addSpend(spend)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(201, response.code());
        return response.body();
    }

    @Step("Редактирование траты")
    public @Nullable SpendJson editSpend(SpendJson spend) {
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

    @Step("Получение траты")
    public @Nullable SpendJson getSpend(String id) {
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

    @Step("Получение всех трат пользователя: {username}")
    public List<SpendJson> allSpends(String username,
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
        return response.body();
    }

    @Step("Удаление траты")
    public void removeSpends(@Nonnull String username, @Nonnull String... ids) {
        final Response<Void> response;
        try {
            response = spendApi.removeSpends(username, Arrays.stream(ids).toList())
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
    }

    @Step("Создание новой категории")
    public @Nullable CategoryJson createCategory(CategoryJson category) {
        final Response<CategoryJson> response;
        try {
            response = spendApi.addCategory(category)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

    @Override
    @Step("Удаление категории")
    public void removeCategory(CategoryJson category) {
        throw new UnsupportedOperationException("Category deletion is not supported with API");
    }

    @Step("Обновление категории")
    public @Nullable CategoryJson updateCategory(CategoryJson category) {
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

    @Step("Получение всех категорий пользователя: {username}")
    public List<CategoryJson> allCategory(String username) {
        final Response<List<CategoryJson>> response;
        try {
            response = spendApi.allCategories(username)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }
}