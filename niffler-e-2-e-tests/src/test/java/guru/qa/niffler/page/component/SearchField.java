package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;

public class SearchField {
    private final SelenideElement searchField = $("input[type='text']");

    @Step("Поиск по значению: {value}")
    public SearchField search(String value) {
        searchField.setValue(value).pressEnter();
        return this;
    }

    @Step("Очистить строку поиска")
    public SearchField clearIfNotEmpty() {
        searchField.clear();
        return this;
    }
}