package guru.qa.niffler.config;

public enum DockerConfig implements Config {
    INSTANCE;

    @Override
    public String frontUrl() {
        return "http://niffler-front:3000/";
    }

    @Override
    public String authUrl() {
        return "http://niffler-auth:9000/";
    }

    @Override
    public String authJdbcUrl() {
        return "jdbc:postgresql://niffler-auth-db:5432/niffler-auth";
    }

    @Override
    public String gatewayUrl() {
        return "http://niffler-gateway:8090/";
    }

    @Override
    public String userdataUrl() {
        return "http://niffler-userdata:8089/";
    }

    @Override
    public String userdataJdbcUrl() {
        return "jdbc:postgresql://niffler-userdata-db:5432/niffler-userdata";
    }

    @Override
    public String spendUrl() {
        return "http://niffler-spend:8093/";
    }

    @Override
    public String spendJdbcUrl() {
        return "jdbc:postgresql://niffler-spend-db:5432/niffler-spend";
    }

    @Override
    public String currencyJdbcUrl() {
        return "jdbc:postgresql://niffler-currency-db:5432/niffler-currency";
    }

    @Override
    public String ghUrl() {
        return "https://api.github.com/";
    }
}