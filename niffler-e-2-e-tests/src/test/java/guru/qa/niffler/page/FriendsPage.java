package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Calendar;
import guru.qa.niffler.page.component.SearchField;
import io.qameta.allure.Step;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;

@ParametersAreNonnullByDefault
public class FriendsPage extends BasePage<FriendsPage> {
    private final SelenideElement emptyFriends = $x("//p[text()='There are no users yet']");
    private final SelenideElement myFriendsListHeader = $x("//h2[text()='My friends']");
    private final SelenideElement friendsRequestListHeader = $x("//h2[text()='Friend requests']");
    private final ElementsCollection friendsRows = $$x("//tbody[@id='friends']/tr");
    private final ElementsCollection requestsRows = $$x("//tbody[@id='requests']/tr");
    private final ElementsCollection allPeopleRows = $$("tbody#all tr");
    private final SelenideElement acceptButton = $(byText("Accept"));
    private final SelenideElement declineButton = $(byText("Decline"));
    private final SelenideElement confirmDeclineButton =
            $(".MuiPaper-root button.MuiButtonBase-root.MuiButton-containedPrimary");
    private final SelenideElement unfriendButton = $("button[class*='MuiButton-containedSecondary']");
    private final SelenideElement searchInput = $("input[type='text']");

    @Getter
    private final SearchField<FriendsPage> searchField = new SearchField<>(searchInput, this);

    @Nonnull
    @Step("Проверка отображения заголовка списка друзей")
    public FriendsPage shouldHaveMyFriendsListHeader(String expectedHeaderText) {
        myFriendsListHeader.shouldHave(text(expectedHeaderText)).shouldBe(visible);
        return this;
    }

    @Step("Проверка наличия друга {friendName} в списке запросов")
    public void shouldBePresentInRequestsTable(String friendName) {
        searchField.clearIfNotEmpty()
                .search(friendName);
        requestsRows.findBy(text(friendName)).shouldBe(visible);
    }

    @Step("Проверка наличия друга {friendName} в списке друзей")
    public void shouldBePresentInFriendsTable(String friendName) {
        searchField.clearIfNotEmpty()
                .search(friendName);
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
        searchField.clearIfNotEmpty()
                .search(name);
        allPeopleRows.findBy(text(name)).$("span").shouldHave(text(status)).shouldBe(visible);
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