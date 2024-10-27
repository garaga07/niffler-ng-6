package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Header;
import guru.qa.niffler.page.component.SearchField;
import io.qameta.allure.Step;
import lombok.Getter;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public abstract class BasePage<T extends BasePage<?>> {
    protected final SelenideElement alert = $x("//div[contains(@class,'MuiTypography-root MuiTypography-body1')]");
    @Getter
    protected final Header<T> header;
    protected final SearchField<T> searchField;  // Добавляем SearchField

    @SuppressWarnings("unchecked")
    public BasePage() {
        this.header = new Header<>($("#root header"), (T) this);
        this.searchField = new SearchField<>($("input[type='text']"), (T) this);  // Инициализируем объект поиска
    }

    @SuppressWarnings("unchecked")
    @Step("Проверка всплывающего сообщения: {message}")
    public T checkAlert(String message) {
        alert.shouldHave(text(message));
        return (T) this;
    }
}