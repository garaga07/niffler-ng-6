package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.*;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class Header {
    private final SelenideElement menu = $("ul[role='menu']");
    private final SelenideElement header = $("#root header");

    @Step("Перейти на \"Friends\" страницу")
    public FriendsPage toFriendsPage() {
        header.$("button").click();
        menu.$$("li").find(text("Friends")).click();
        return new FriendsPage();
    }

    @Step("Перейти на \"All People\" страницу")
    public FriendsPage toAllPeoplesPage() {
        header.$("button").click();
        menu.$$("li").find(text("All People")).click();
        return new FriendsPage();
    }

    @Step("Перейти на страницу профиля")
    public ProfilePage toProfilePage() {
        header.$("[aria-label='Menu']").click();
        menu.$(byText("Profile")).click();
        return new ProfilePage();
    }

    @Step("Перейти на главную страницу")
    public MainPage toMainPage() {
        header.$(".MuiToolbar-gutters").click();
        return new MainPage();
    }

    @Step("Разлогинить пользователя")
    public LoginPage signOut() {
        header.$("[aria-label='Menu']").click();
        menu.$(byText("Sign out")).click();
        return new LoginPage();
    }

    @Step("Добавить новую трату")
    public EditSpendingPage addSpendingPage() {
        header.$(byText("New spending")).click();
        return new EditSpendingPage();
    }
}