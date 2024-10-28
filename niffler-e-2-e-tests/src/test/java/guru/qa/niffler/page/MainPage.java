package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.SearchField;
import guru.qa.niffler.page.component.SpendingTable;
import io.qameta.allure.Step;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

@ParametersAreNonnullByDefault
public class MainPage extends BasePage<MainPage> {
    private final ElementsCollection tableRows = $("#spendings tbody").$$("tr");
    private final SelenideElement statisticsHeader = $x("//h2[text()='Statistics']");
    private final SelenideElement historyOfSpendingHeader = $x("//h2[text()='History of Spendings']");
    private final SelenideElement searchInput = $("input[type='text']");
    @Getter
    private final SpendingTable<MainPage> spendingTable = new SpendingTable<>($(".MuiTableContainer-root"), this);
    @Getter
    private final SearchField<MainPage> searchField = new SearchField<>(searchInput, this);

    @Nonnull
    @Step("Редактировать трату с описанием: {spendingDescription}")
    public EditSpendingPage editSpending(String spendingDescription) {
        searchField.clearIfNotEmpty()
                .search(spendingDescription);
        tableRows.find(text(spendingDescription)).$$("td").get(5).click();
        return new EditSpendingPage();
    }

    @Step("Проверить, что таблица содержит трату с описанием: {spendingDescription}")
    public void checkThatTableContainsSpending(String spendingDescription) {
        searchField.clearIfNotEmpty()
                .search(spendingDescription);
        tableRows.find(text(spendingDescription)).should(visible);
    }

    @Nonnull
    @Step("Проверить, что заголовок статистики содержит текст: {value}")
    public MainPage checkStatisticsHeaderContainsText(String value) {
        statisticsHeader.shouldHave(text(value)).shouldBe(visible);
        return this;
    }

    @Step("Проверить, что заголовок истории трат содержит текст: {value}")
    public void checkHistoryOfSpendingHeaderContainsText(String value) {
        historyOfSpendingHeader.shouldHave(text(value)).shouldBe(visible);
    }
}