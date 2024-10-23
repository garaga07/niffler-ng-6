package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.UserApiClient;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.List;

public class UserExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);
    private static final String defaultPassword = "12345";

    private final UsersClient usersClient = new UserApiClient();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(userAnno -> {
                    if ("".equals(userAnno.username())) {
                        final String username = RandomDataUtils.randomUsername();
                        UserJson testUser = usersClient.createUser(username, defaultPassword);
                        // Добавление друзей и приглашений с проверкой на количество
                        List<String> friends = new ArrayList<>();
                        List<String> incomeInvites = new ArrayList<>();
                        List<String> outcomeInvites = new ArrayList<>();

                        if (userAnno.friends() > 0) {
                            friends = usersClient.addFriend(testUser, userAnno.friends());
                        }
                        if (userAnno.incomeInvitations() > 0) {
                            incomeInvites = usersClient.addIncomeInvitation(testUser, userAnno.incomeInvitations());
                        }
                        if (userAnno.outcomeInvitations() > 0) {
                            outcomeInvites = usersClient.addOutcomeInvitation(testUser, userAnno.outcomeInvitations());
                        }

                        // Сохранение пользователя и данных в контексте
                        context.getStore(NAMESPACE).put(
                                context.getUniqueId(),
                                testUser.addTestData(
                                        new TestData(
                                                defaultPassword,
                                                new ArrayList<>(), // Пустой список для категорий
                                                new ArrayList<>(), // Пустой список для трат
                                                friends,           // Список друзей
                                                incomeInvites,     // Список входящих приглашений
                                                outcomeInvites     // Список исходящих приглашений
                                        )
                                )
                        );
                    }
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(UserJson.class);
    }

    @Override
    public UserJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), UserJson.class);
    }
}