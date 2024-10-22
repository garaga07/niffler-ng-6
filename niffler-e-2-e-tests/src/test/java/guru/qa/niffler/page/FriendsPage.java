package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;

@ParametersAreNonnullByDefault
public class FriendsPage {
    private final SelenideElement emptyFriends = $x("//p[text()='There are no users yet']");
    private final SelenideElement myFriendsListHeader = $x("//h2[text()='My friends']");
    private final SelenideElement friendsRequestListHeader = $x("//h2[text()='Friend requests']");
    private final ElementsCollection friendsRows = $$x("//tbody[@id='friends']/tr");
    private final ElementsCollection requestsRows = $$x("//tbody[@id='requests']/tr");
    private final ElementsCollection allPeopleRows = $$("tbody#all tr");
    private final SelenideElement searchInput = $("input[type='text']");
    private final SelenideElement acceptButton = $(byText("Accept"));
    private final SelenideElement declineButton = $(byText("Decline"));
    private final SelenideElement confirmDeclineButton =
            $(".MuiPaper-root button.MuiButtonBase-root.MuiButton-containedPrimary");
    private final SelenideElement unfriendButton = $("button[class*='MuiButton-containedSecondary']");

    @Nonnull
    @Step("Проверка отображения заголовка списка друзей")
    public FriendsPage shouldHaveMyFriendsListHeader(String expectedHeaderText) {
        myFriendsListHeader.shouldHave(text(expectedHeaderText)).shouldBe(visible);
        return this;
    }

    @Step("Проверка наличия друга {friendName} в списке запросов")
    public void shouldBePresentInRequestsTable(String friendName) {
        searchFriend(friendName);
        requestsRows.findBy(text(friendName)).shouldBe(visible);
    }

    @Step("Проверка наличия друга {friendName} в списке друзей")
    public void shouldBePresentInFriendsTable(String friendName) {
        searchFriend(friendName);
        friendsRows.findBy(text(friendName)).shouldBe(visible);
    }

    @Step("Проверка пустого списка друзей с сообщением: {message}")
    public void shouldHaveEmptyFriendsTable(String message) {
        emptyFriends.shouldHave(text(message)).shouldBe(visible);
    }

    @Nonnull
    @Step("Проверка отображения заголовка списка запросов")
    public FriendsPage shouldFriendRequestList(String expectedHeaderText) {
        friendsRequestListHeader.shouldHave(text(expectedHeaderText)).shouldBe(visible);
        return this;
    }

    @Step("Проверка наличия {name} с статусом {status} в таблице \"all people\"")
    public void shouldBePresentInAllPeopleTableAndCheckStatus(String name, String status) {
        searchFriend(name);
        allPeopleRows.findBy(text(name)).$("span").shouldHave(text(status)).shouldBe(visible);
    }

    @Step("Поиск пользователя с именем {friendName}")
    public void searchFriend(String friendName) {
        searchInput.setValue(friendName).pressEnter();
    }

    @Nonnull
    @Step("Принять заявку в друзья")
    public FriendsPage acceptFriend() {
        acceptButton.click();
        return this;
    }

    @Nonnull
    @Step("Отклонить заявку в друзья")
    public FriendsPage declineFriend() {
        declineButton.click();
        confirmDeclineButton.click();
        return this;
    }

    @Step("Проверить наличие кнопки 'Удалить из друзей'")
    public void checkUnfriendButtonIsVisible() {
        unfriendButton.shouldBe(visible);
    }
}