package guru.qa.niffler.api;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserApiClient implements UsersClient {

    // Настройка Retrofit для User API
    private final Retrofit userRetrofit = new Retrofit.Builder()
            .baseUrl(Config.getInstance().userdataUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .build();
    private final UserApi userApi = userRetrofit.create(UserApi.class);

    // Настройка Retrofit для Auth API
    private final Retrofit authRetrofit = new Retrofit.Builder()
            .baseUrl(Config.getInstance().authUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .build();
    private final AuthApi authApi = authRetrofit.create(AuthApi.class);

    public UserJson getCurrentUser(String username) {
        final Response<UserJson> response;
        try {
            response = userApi.getCurrentUser(username)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

    public UserJson sendInvitation(String username, String targetUsername) {
        final Response<UserJson> response;
        try {
            response = userApi.sendInvitation(username, targetUsername)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

    public UserJson acceptInvitation(String username, String targetUsername) {
        final Response<UserJson> response;
        try {
            response = userApi.acceptInvitation(username, targetUsername)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

    @Override
    public UserJson createUser(String username, String password) {
        // Шаг 1: Запрос формы регистрации для получения CSRF токена
        final Response<Void> formResponse;
        try {
            formResponse = authApi.requestRegisterForm().execute();
        } catch (IOException e) {
            throw new AssertionError("Ошибка при выполнении запроса формы регистрации", e);
        }

        // Убедиться, что запрос формы выполнен успешно
        assertEquals(200, formResponse.code(), "Ожидался код 200 при запросе формы регистрации");

        // Шаг 2: Извлечение CSRF токена из заголовка ответа
        String cookieHeader = formResponse.headers().get("Set-Cookie");
        if (cookieHeader == null || !cookieHeader.contains("XSRF-TOKEN")) {
            throw new AssertionError("Не удалось получить XSRF-TOKEN из заголовка Set-Cookie");
        }

        String csrfToken = null;
        for (String cookie : cookieHeader.split(";")) {
            if (cookie.contains("XSRF-TOKEN")) {
                csrfToken = cookie.split("=")[1].trim();
                break;
            }
        }

        if (csrfToken == null) {
            throw new AssertionError("XSRF-TOKEN не найден в заголовке Set-Cookie");
        }

        // Шаг 3: Отправка запроса на регистрацию пользователя
        final Response<Void> registerResponse;
        try {
            registerResponse = authApi.register(username, password, password, csrfToken).execute();
        } catch (IOException e) {
            throw new AssertionError("Ошибка при выполнении запроса регистрации пользователя", e);
        }

        assertEquals(201, registerResponse.code(), "Ожидался код 201 для успешной регистрации");

        // Шаг 4: Получение информации о созданном пользователе
        return getCurrentUser(username);
    }

    @Override
    public List<String> addIncomeInvitation(UserJson targetUser, int count) {
        List<String> incomeUsers = new ArrayList<>();

        if (count > 0) {
            // Шаг 1: Проверка, существует ли целевой пользователь (targetUser)
            UserJson user = getCurrentUser(targetUser.username());

            if (user == null || user.id() == null) {
                throw new AssertionError("Пользователь с именем " + targetUser.username() + " не найден");
            }

            for (int i = 0; i < count; i++) {
                // Шаг 2: Создание рандомного пользователя
                UserJson newUser = createUser(randomUsername(), "12345");

                // Шаг 3: Отправка приглашения в друзья
                sendInvitation(newUser.username(), user.username());

                incomeUsers.add(newUser.username());
            }
        }
        return incomeUsers;
    }

    @Override
    public List<String> addOutcomeInvitation(UserJson targetUser, int count) {
        List<String> outcomeUsers = new ArrayList<>();

        if (count > 0) {
            // Шаг 1: Проверка, существует ли целевой пользователь (targetUser)
            UserJson user = getCurrentUser(targetUser.username());

            if (user == null || user.id() == null) {
                throw new AssertionError("Пользователь с именем " + targetUser.username() + " не найден");
            }

            for (int i = 0; i < count; i++) {
                // Шаг 2: Создание рандомного пользователя
                UserJson newUser = createUser(randomUsername(), "12345");

                // Шаг 3: Отправка приглашения в друзья
                sendInvitation(user.username(), newUser.username());

                // Добавляем созданного пользователя в список
                outcomeUsers.add(newUser.username());
            }
        }
        return outcomeUsers;
    }

    @Override
    public List<String> addFriend(UserJson targetUser, int count) {
        List<String> friends = new ArrayList<>();

        if (count > 0) {
            // Шаг 1: Проверка, существует ли целевой пользователь (targetUser)
            UserJson user = getCurrentUser(targetUser.username());

            if (user == null || user.id() == null) {
                throw new AssertionError("Пользователь с именем " + targetUser.username() + " не найден");
            }

            for (int i = 0; i < count; i++) {
                // Шаг 2: Отправка входящего приглашения в друзья
                List<String> incomeUsers = addIncomeInvitation(targetUser, 1);

                // Шаг 3: Принятие входящего приглашения в друзья
                acceptInvitation(user.username(), incomeUsers.get(0));

                // Добавляем созданного друга в список
                friends.add(incomeUsers.get(0));
            }
        }
        return friends;
    }
}