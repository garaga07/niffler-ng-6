package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.repository.impl.AuthUserRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.UserdataUserRepositoryJdbc;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;


public class UsersDbClient implements UsersClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserRepository authUserRepository = new AuthUserRepositoryJdbc();
    private final UserdataUserRepository userdataUserRepository = new UserdataUserRepositoryJdbc();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    @Override
    public UserJson createUser(String username, String password) {
        return xaTransactionTemplate.execute(() -> UserJson.fromEntity(
                        createNewUser(username, password),
                        null
                )
        );
    }

    @Override
    public List<String> addIncomeInvitation(UserJson targetUser, int count) {
        List<String> incomes = new ArrayList<>();
        if (count > 0) {
            UserEntity targetEntity = userdataUserRepository.findById(targetUser.id()).orElseThrow();
            for (int i = 0; i < count; i++) {
                String username = randomUsername();
                xaTransactionTemplate.execute(() -> {
                    userdataUserRepository.addFriendshipRequest(createNewUser(username, "12345"), targetEntity);
                    return null;
                });
                incomes.add(username);  // Сохраняем имя пользователя, который отправил входящее приглашение
            }
        }
        return incomes;
    }

    @Override
    public List<String> addOutcomeInvitation(UserJson targetUser, int count) {
        List<String> outcomes = new ArrayList<>();
        if (count > 0) {
            UserEntity targetEntity = userdataUserRepository.findById(targetUser.id()).orElseThrow();
            for (int i = 0; i < count; i++) {
                String username = randomUsername();
                xaTransactionTemplate.execute(() -> {
                    userdataUserRepository.addFriendshipRequest(targetEntity, createNewUser(username, "12345"));
                    return null;
                });
                outcomes.add(username);  // Сохраняем имя пользователя, которому отправили исходящее приглашение
            }
        }
        return outcomes;
    }

    @Override
    public List<String> addFriend(UserJson targetUser, int count) {
        List<String> friends = new ArrayList<>();
        if (count > 0) {
            UserEntity targetEntity = userdataUserRepository.findById(targetUser.id()).orElseThrow();
            for (int i = 0; i < count; i++) {
                String username = randomUsername();
                xaTransactionTemplate.execute(() -> {
                    userdataUserRepository.addFriend(targetEntity, createNewUser(username, "12345"));
                    return null;
                });
                friends.add(username);  // Сохраняем имя нового друга
            }
        }
        return friends;
    }

    private UserEntity createNewUser(String username, String password) {
        AuthUserEntity authUser = authUserEntity(username, password);
        authUserRepository.create(authUser);
        return userdataUserRepository.create(userEntity(username));
    }

    private UserEntity userEntity(String username) {
        UserEntity ue = new UserEntity();
        ue.setUsername(username);
        ue.setCurrency(CurrencyValues.RUB);
        return ue;
    }

    private AuthUserEntity authUserEntity(String username, String password) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(username);
        authUser.setPassword(pe.encode(password));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);
        authUser.setAuthorities(
                Arrays.stream(Authority.values()).map(
                        e -> {
                            AuthorityEntity ae = new AuthorityEntity();
                            ae.setUser(authUser);
                            ae.setAuthority(e);
                            return ae;
                        }
                ).toList()
        );
        return authUser;
    }
}