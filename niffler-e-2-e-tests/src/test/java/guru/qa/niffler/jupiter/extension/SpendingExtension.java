package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.service.impl.SpendDbClient;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SpendingExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(SpendingExtension.class);

    private final SpendClient spendClient = new SpendDbClient();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(userAnno -> {
                    if (ArrayUtils.isNotEmpty(userAnno.spendings())) {
                        List<SpendJson> result = new ArrayList<>();
                        UserJson user = context.getStore(UserExtension.NAMESPACE)
                                .get(context.getUniqueId(), UserJson.class);

                        String username = user != null ? user.username() : userAnno.username();

                        for (Spending spendAnno : userAnno.spendings()) {
                            // Получение или создание категории
                            CategoryJson category = spendClient.getOrCreateCategory(
                                    username,
                                    spendAnno.category(),
                                    false // Категория не архивная по умолчанию
                            );

                            // Создание объекта SpendJson с использованием найденной/созданной категории
                            SpendJson spend = new SpendJson(
                                    null,
                                    new Date(),
                                    spendAnno.amount(),
                                    spendAnno.currency(),
                                    category,
                                    spendAnno.description(),
                                    username
                            );

                            SpendJson createdSpend = spendClient.createSpend(spend);
                            result.add(createdSpend);
                        }

                        if (user != null) {
                            user.testData().spends().addAll(result);
                        } else {
                            context.getStore(NAMESPACE).put(
                                    context.getUniqueId(),
                                    result
                            );
                        }
                    }
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(SpendJson[].class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public SpendJson[] resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return (SpendJson[]) extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), List.class).toArray();
    }
}