package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Calendar;
import io.qameta.allure.Step;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class EditSpendingPage {

    private final SelenideElement descriptionInput = $("#description");
    private final SelenideElement saveBtn = $("#save");
    private final SelenideElement amountInput = $("#amount");
    private final SelenideElement categoryInput = $("#category");

    @Getter
    private final Calendar calendar = new Calendar($("input[name='date']"));

    @Nonnull
    @Step("Установить новое описание траты: {description}")
    public EditSpendingPage setNewSpendingDescription(String description) {
        descriptionInput.clear();
        descriptionInput.setValue(description);
        return this;
    }

    @Step("Ввести стоимость траты: {amount}")
    public EditSpendingPage setSpendingAmount(String amount) {
        amountInput.setValue(amount);
        return this;
    }

    @Step("Ввести название категории: {category}")
    public EditSpendingPage setSpendingCategory(String category) {
        categoryInput.setValue(category);
        return this;
    }

    @Step("Сохранить изменения по трате")
    public void save() {
        saveBtn.click();
    }
}