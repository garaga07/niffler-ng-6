package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.jupiter.annotation.CrossBrowserWebTest;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.Browser;
import guru.qa.niffler.utils.BrowserArgumentConverter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.EnumSource;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

@CrossBrowserWebTest
public class CrossBrowserTest {
    @ParameterizedTest
    @EnumSource(Browser.class)
    void userShouldStayOnLoginPageAfterLoginWithBadCredentials(
            @ConvertWith(BrowserArgumentConverter.class) SelenideDriver driver) {
        driver.open(LoginPage.URL);
        new LoginPage(driver)
                .fillLoginPage(randomUsername(), "BAD_PASSWORD")
                .submit(new LoginPage(driver))
                .checkError("Неверные учетные данные пользователя");
    }
}