package guru.qa.niffler.test.web;

import guru.qa.niffler.condition.Bubble;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.jupiter.annotation.*;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.component.SpendingTable;
import guru.qa.niffler.page.component.StatComponent;
import guru.qa.niffler.utils.RandomDataUtils;
import guru.qa.niffler.utils.ScreenDiffResult;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

@WebTest
public class SpendingWebTest {

    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 79990
            )
    )
    @ApiLogin
    @Test
    void categoryDescriptionShouldBeChangedFromTable() {
        final String newDescription = "Обучение Niffler Next Generation";
        new MainPage()
                .getSpendingTable()
                .editSpending("Обучение Advanced 2.0")
                .setNewSpendingDescription(newDescription)
                .saveSpending();
        new MainPage().getSpendingTable()
                .checkTableContains(newDescription);
    }

    @User
    @ApiLogin
    @Test
    void shouldAddNewSpending() {
        String category = "Friends";
        int amount = 100;
        Date currentDate = new Date();
        String description = RandomDataUtils.randomSentence(3);

        new MainPage()
                .getHeader()
                .addSpendingPage()
                .setNewSpendingCategory(category)
                .setNewSpendingAmount(amount)
                .setNewSpendingDate(currentDate)
                .setNewSpendingDescription(description)
                .saveSpending()
                .checkAlertMessage("New spending is successfully created");

        new MainPage().getSpendingTable()
                .checkTableContains(description);
    }

    @User
    @ApiLogin
    @Test
    void shouldNotAddSpendingWithEmptyCategory() {
        new MainPage()
                .getHeader()
                .addSpendingPage()
                .setNewSpendingAmount(100)
                .setNewSpendingDate(new Date())
                .saveSpending()
                .checkFormErrorMessage("Please choose category");
    }

    @User
    @ApiLogin
    @Test
    void shouldNotAddSpendingWithEmptyAmount() {
        new MainPage()
                .getHeader()
                .addSpendingPage()
                .setNewSpendingCategory("Friends")
                .setNewSpendingDate(new Date())
                .saveSpending()
                .checkFormErrorMessage("Amount has to be not less then 0.01");
    }

    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 79990
            )
    )
    @ApiLogin
    @Test
    void deleteSpendingTest() {
        new MainPage()
                .getSpendingTable()
                .deleteSpending("Обучение Advanced 2.0")
                .checkTableSize(0);
    }


    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 79990
            )
    )
    @ApiLogin
    @ScreenShotTest(value = "img/expected-stat.png")
    void checkStatComponentTest(BufferedImage expected) throws IOException, InterruptedException {
        StatComponent statComponent = new StatComponent();
        new MainPage()
                .getStatComponent();
        Thread.sleep(3000);
        assertFalse(new ScreenDiffResult(
                expected,
                statComponent.chartScreenshot()
        ), "Screen comparison failure");
        statComponent.checkBubblesColor(Color.yellow);
    }

    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 79990
            )
    )
    @ApiLogin
    @Test
    void checkStatBubbleContent() throws InterruptedException {
        StatComponent statComponent = new StatComponent();
        new MainPage()
                .getStatComponent();
        Thread.sleep(3000);
        Bubble bubble = new Bubble(Color.yellow, "Обучение 79990 ₽");
        statComponent.checkBubbles(bubble);
    }

    @User(
            spendings = {
                    @Spending(
                            category = "Обучение",
                            description = "Обучение Advanced 2.0",
                            amount = 1000
                    ),
                    @Spending(
                            category = "Развлечения",
                            description = "Поход в кино",
                            amount = 100
                    )
            }
    )
    @ApiLogin
    @Test
    void checkStatBubblesInAnyOrder() throws InterruptedException {
        StatComponent statComponent = new StatComponent();
        new MainPage()
                .getStatComponent();
        Thread.sleep(3000);
        Bubble bubble1 = new Bubble(Color.yellow, "Обучение 1000 ₽");
        Bubble bubble2 = new Bubble(Color.green, "Развлечения 100 ₽");
        statComponent.checkBubblesInAnyOrder(bubble2, bubble1);
    }

    @User(
            spendings = {
                    @Spending(
                            category = "Обучение",
                            description = "Обучение Advanced 2.0",
                            amount = 1000
                    ),
                    @Spending(
                            category = "Развлечения",
                            description = "Поход в кино",
                            amount = 100
                    )
            }
    )
    @ApiLogin
    @Test
    void checkStatBubbleContainsAmongOtherBubbles() throws InterruptedException {
        StatComponent statComponent = new StatComponent();
        new MainPage()
                .getStatComponent();
        Thread.sleep(3000);
        Bubble bubble = new Bubble(Color.yellow, "Обучение 1000 ₽");
        statComponent.checkBubblesContains(bubble);
    }

    @User(
            spendings = {
                    @Spending(
                            category = "Обучение",
                            description = "Обучение Advanced 2.0",
                            amount = 1000
                    ),
                    @Spending(
                            category = "Развлечения",
                            description = "Поход в кино",
                            amount = 100
                    )
            }
    )
    @ApiLogin
    @Test
    void checkSpendsExistInTable(UserJson user) throws InterruptedException {
        SpendingTable spendingTable = new SpendingTable();
        new MainPage()
                .getSpendingTable();
        Thread.sleep(3000);
        // Извлекаем список SpendJson и передаем его в checkSpendingTable
        List<SpendJson> expectedSpends = user.testData().spends();
        spendingTable.checkSpendingTable(expectedSpends.toArray(new SpendJson[0]));
    }
}