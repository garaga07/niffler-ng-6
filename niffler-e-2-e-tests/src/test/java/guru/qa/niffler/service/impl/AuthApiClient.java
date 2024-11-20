package guru.qa.niffler.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.internal.lang3.StringUtils;
import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.utils.OAuthUtils;
import lombok.SneakyThrows;
import retrofit2.Response;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static okhttp3.logging.HttpLoggingInterceptor.Level.HEADERS;

public class AuthApiClient extends RestClient {
    private static final Config CFG = Config.getInstance();
    private final AuthApi authApi;

    public AuthApiClient() {
        super(CFG.authUrl(), true, ScalarsConverterFactory.create(), HEADERS);
        this.authApi = create(AuthApi.class);
    }

    @SneakyThrows
    public String login(String username, String password) throws IOException {
        final String codeVerifier = OAuthUtils.generateCodeVerifier();
        final String codeChallenge = OAuthUtils.generateCodeChallenge(codeVerifier);
        final String redirectUri = CFG.frontUrl() + "authorized";
        final String clientId = "client";

        authApi.authorize(
                "code",
                clientId,
                "openid",
                redirectUri,
                codeChallenge,
                "S256"
        ).execute();

        final String code;

        Response<String> loginResponse = authApi.login(
                username,
                password,
                ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
        ).execute();

        String url = loginResponse.raw().request().url().toString();
        code = StringUtils.substringAfter(url, "code=");

        Response<String> tokenResponse = authApi.token(
                clientId,
                redirectUri,
                "authorization_code",
                code,
                codeVerifier
        ).execute();

        return new ObjectMapper().readTree(
                tokenResponse.body().getBytes(StandardCharsets.UTF_8)
        ).get("id_token").asText();
    }
}
