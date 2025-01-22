package guru.qa.niffler.service.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.repository.impl.SpendRepositoryJdbc;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.service.SpendClient;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class SpendDbClient implements SpendClient {

    private static final Config CFG = Config.getInstance();

    private final SpendRepository spendRepository = new SpendRepositoryJdbc();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    @Nonnull
    @Override
    public SpendJson createSpend(SpendJson spend) {
        return requireNonNull(
                xaTransactionTemplate.execute(
                        () -> SpendJson.fromEntity(
                                spendRepository.create(
                                        SpendEntity.fromJson(spend)
                                )
                        )
                )
        );
    }

    @Nonnull
    @Override
    public CategoryJson createCategory(CategoryJson category) {
        return requireNonNull(
                xaTransactionTemplate.execute(
                        () -> CategoryJson.fromEntity(
                                spendRepository.createCategory(
                                        CategoryEntity.fromJson(category)
                                )
                        )
                )
        );
    }

    @NotNull
    @Override
    public CategoryJson updateCategory(CategoryJson category) {
        return requireNonNull(
                xaTransactionTemplate.execute(
                        () -> CategoryJson.fromEntity(
                                spendRepository.updateCategory(
                                        CategoryEntity.fromJson(category)
                                )
                        )
                )
        );
    }

    @Override
    public void removeCategory(CategoryJson category) {
        xaTransactionTemplate.execute(
                () -> {
                    spendRepository.removeCategory(
                            CategoryEntity.fromJson(category)
                    );
                    return null;
                }
        );
    }

    @Nonnull
    public CategoryJson getOrCreateCategory(String username, String categoryName, boolean archived) {
        return requireNonNull(
                xaTransactionTemplate.execute(() -> {
                    Optional<CategoryEntity> existingCategory = spendRepository.findCategoryByUsernameAndCategoryName(username, categoryName);

                    if (existingCategory.isPresent()) {
                        // Если категория уже существует, возвращаем её
                        return CategoryJson.fromEntity(existingCategory.get());
                    }

                    // Если категории нет, создаем новую с использованием конструктора
                    CategoryEntity newCategory = new CategoryEntity();
                    newCategory.setUsername(username);
                    newCategory.setName(categoryName);
                    newCategory.setArchived(archived);

                    CategoryEntity createdCategory = spendRepository.createCategory(newCategory);
                    return CategoryJson.fromEntity(createdCategory);
                })
        );
    }
}