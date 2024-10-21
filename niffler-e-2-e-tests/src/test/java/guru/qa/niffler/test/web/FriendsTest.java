package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

@WebTest
public class FriendsTest {
    private static final Config CFG = Config.getInstance();

    @User(friends = 1)
    @Test
    void friendShouldBePresentInFriendsTable(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .goToFriends()
                .shouldHaveMyFriendsListHeader("My friends")
                .shouldBePresentInFriendsTable(user.testData().friends().get(0));
    }

    @User
    @Test
    void friendsTableShouldBeEmptyForNewUser(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .goToFriends()
                .shouldHaveEmptyFriendsTable("There are no users yet");
    }

    @User(incomeInvitations = 1)
    @Test
    void incomeInvitationBePresentInFriendsTable(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .goToFriends()
                .shouldFriendRequestList("Friend requests")
                .shouldBePresentInRequestsTable(user.testData().incomeInvites().get(0));
    }

    @User(outcomeInvitations = 1)
    @Test
    void outcomeInvitationBePresentInAllPeoplesTable(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .goToAllPeople()
                .shouldBePresentInAllPeopleTableAndCheckStatus(user.testData().outcomeInvites().get(0), "Waiting...");
    }
}