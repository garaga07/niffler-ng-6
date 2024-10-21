package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class FriendsPage {
    private final SelenideElement emptyFriends = $x("//p[text()='There are no users yet']");
    private final SelenideElement myFriendsListHeader = $x("//h2[text()='My friends']");
    private final SelenideElement friendsRequestListHeader = $x("//h2[text()='Friend requests']");
    private final ElementsCollection friendsRows = $$x("//tbody[@id='friends']/tr");
    private final ElementsCollection requestsRows = $$x("//tbody[@id='requests']/tr");
    private final ElementsCollection allPeopleRows = $$("tbody#all tr");
    private final SelenideElement searchInput = $("input[type='text']");

    // Проверка отображения заголовка таблицы друзей
    public FriendsPage shouldHaveMyFriendsListHeader(String expectedHeaderText) {
        myFriendsListHeader.shouldHave(text(expectedHeaderText)).shouldBe(visible);
        return this;
    }

    // Проверка, что запись с именем отображается в таблице запросов
    public void shouldBePresentInRequestsTable(String friendName) {
        searchFriend(friendName);
        requestsRows.findBy(text(friendName)).shouldBe(visible);
    }

    // Проверка, что запись с именем отображается в таблице друзей
    public void shouldBePresentInFriendsTable(String friendName) {
        searchFriend(friendName);
        friendsRows.findBy(text(friendName)).shouldBe(visible);
    }

    // Проверка текста заглушки для пустой таблицы друзей
    public void shouldHaveEmptyFriendsTable(String message) {
        emptyFriends.shouldHave(text(message)).shouldBe(visible);
    }

    // Проверка отображения заголовка таблицы запросов
    public FriendsPage shouldFriendRequestList(String expectedHeaderText) {
        friendsRequestListHeader.shouldHave(text(expectedHeaderText)).shouldBe(visible);
        return this;
    }

    // Проверка, что запись с именем и соответствующим статусом отображается в таблице "all people"
    public FriendsPage shouldBePresentInAllPeopleTableAndCheckStatus(String name, String status) {
        searchFriend(name);
        allPeopleRows.findBy(text(name)).$("span").shouldHave(text(status)).shouldBe(visible);
        return this;
    }

    public void searchFriend(String friendName) {
        searchInput.setValue(friendName).pressEnter();
    }
}