package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.repository.impl.SpendRepositoryJdbc;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class SpendDbClient implements SpendClient {

    private static final Config CFG = Config.getInstance();

    private final SpendRepository spendRepository = new SpendRepositoryJdbc();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    @Nonnull
    @Override
    @Step("Создание новой траты")
    public SpendJson createSpend(SpendJson spend) {
        return Objects.requireNonNull(
                xaTransactionTemplate.execute(() ->
                        SpendJson.fromEntity(spendRepository.create(SpendEntity.fromJson(spend)))
                ), "Transaction result is null"
        );
    }

    @Nonnull
    @Override
    @Step("Создание новой категории")
    public CategoryJson createCategory(CategoryJson category) {
        return Objects.requireNonNull(
                xaTransactionTemplate.execute(() ->
                        CategoryJson.fromEntity(spendRepository.createCategory(CategoryEntity.fromJson(category)))
                ), "Transaction result is null"
        );
    }

    @Override
    @Step("Удаление категории")
    public void removeCategory(CategoryJson category) {
        xaTransactionTemplate.execute(() -> {
            spendRepository.removeCategory(CategoryEntity.fromJson(category));
            return null;
        });
    }
}