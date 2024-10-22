package guru.qa.niffler.config;

import javax.annotation.Nonnull;

public enum DockerConfig implements Config {
    INSTANCE;

    @Nonnull
    @Override
    public String frontUrl() {
        return "http://niffler-front:3000/";
    }

    @Nonnull
    @Override
    public String authUrl() {
        return "http://niffler-auth:9000/";
    }

    @Nonnull
    @Override
    public String authJdbcUrl() {
        return "jdbc:postgresql://niffler-auth-db:5432/niffler-auth";
    }

    @Nonnull
    @Override
    public String gatewayUrl() {
        return "http://niffler-gateway:8090/";
    }

    @Nonnull
    @Override
    public String userdataUrl() {
        return "http://niffler-userdata:8089/";
    }

    @Nonnull
    @Override
    public String userdataJdbcUrl() {
        return "jdbc:postgresql://niffler-userdata-db:5432/niffler-userdata";
    }

    @Nonnull
    @Override
    public String spendUrl() {
        return "http://niffler-spend:8093/";
    }

    @Nonnull
    @Override
    public String spendJdbcUrl() {
        return "jdbc:postgresql://niffler-spend-db:5432/niffler-spend";
    }

    @Nonnull
    @Override
    public String currencyJdbcUrl() {
        return "jdbc:postgresql://niffler-currency-db:5432/niffler-currency";
    }

    @Nonnull
    @Override
    public String ghUrl() {
        return "https://api.github.com/";
    }
}