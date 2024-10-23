package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Header;
import guru.qa.niffler.page.component.SpendingTable;
import io.qameta.allure.Step;
import lombok.Getter;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class MainPage {
    private final ElementsCollection tableRows = $("#spendings tbody").$$("tr");
    private final SelenideElement statisticsHeader = $x("//h2[text()='Statistics']");
    private final SelenideElement historyOfSpendingHeader = $x("//h2[text()='History of Spendings']");
    private final SelenideElement searchInput = $("input[type='text']");
    @Getter
    private final Header header = new Header();
    @Getter
    private final SpendingTable spendingTable = new SpendingTable();

    @Step("Редактировать трату с описанием: {spendingDescription}")
    public EditSpendingPage editSpending(String spendingDescription) {
        searchSpend(spendingDescription);
        tableRows.find(text(spendingDescription)).$$("td").get(5).click();
        return new EditSpendingPage();
    }

    @Step("Проверить, что таблица содержит трату с описанием: {spendingDescription}")
    public void checkThatTableContainsSpending(String spendingDescription) {
        searchSpend(spendingDescription);
        tableRows.find(text(spendingDescription)).should(visible);
    }

    @Step("Проверить, что заголовок статистики содержит текст: {value}")
    public MainPage checkStatisticsHeaderContainsText(String value) {
        statisticsHeader.shouldHave(text(value)).shouldBe(visible);
        return this;
    }

    @Step("Проверить, что заголовок истории трат содержит текст: {value}")
    public void checkHistoryOfSpendingHeaderContainsText(String value) {
        historyOfSpendingHeader.shouldHave(text(value)).shouldBe(visible);
    }

    @Step("Найти трату по описанию: {spendingDescription}")
    public void searchSpend(String spendingDescription) {
        searchInput.setValue(spendingDescription).pressEnter();
    }
}