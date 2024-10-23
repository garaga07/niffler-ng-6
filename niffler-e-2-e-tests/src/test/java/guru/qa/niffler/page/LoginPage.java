package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage {
    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement passwordInput = $("input[name='password']");
    private final SelenideElement submitButton = $("button[type='submit']");
    private final SelenideElement createNewAccountButton = $(".form__register");
    private final SelenideElement formError = $(".form__error");

    @Step("Ввести имя пользователя: {username}")
    public LoginPage setUsername(String username) {
        usernameInput.setValue(username);
        return new LoginPage();
    }

    @Step("Ввести пароль")
    public LoginPage setPassword(String password) {
        passwordInput.setValue(password);
        return new LoginPage();
    }

    @Step("Нажать кнопку отправки формы")
    public LoginPage submitButtonClick() {
        submitButton.click();
        return new LoginPage();
    }

    @Step("Перейти на страницу создания нового аккаунта")
    public void submitCreateNewAccount() {
        createNewAccountButton.click();
        new RegisterPage();
    }

    @Step("Авторизация с именем пользователя: {username} и паролем")
    public MainPage login(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        submitButton.click();
        return new MainPage();
    }

    @Step("Проверка текста ошибки: {value}")
    public void checkFormErrorText(String value) {
        formError.shouldHave(text(value)).shouldBe(visible);
    }
}