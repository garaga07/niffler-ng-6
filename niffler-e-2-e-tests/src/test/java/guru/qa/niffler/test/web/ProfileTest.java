package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.utils.RandomDataUtils.randomName;

@WebTest
public class ProfileTest {
    private static final Config CFG = Config.getInstance();

    @User(
            username = "duck",
            categories = @Category(
                    archived = false
            )
    )
    @Test
    void archivedCategoryShouldNotPresentInCategoriesList(CategoryJson[] categories) throws InterruptedException {
        CategoryJson category = categories[0];
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login("duck", "12345")
                .getHeader()
                .toProfilePage()
                .clickArchiveButtonForCategoryName(category.name())
                .clickArchiveButtonSubmit()
                .shouldBeVisibleArchiveSuccessMessage(category.name())
                .shouldNotVisibleArchiveCategory(category.name());
    }

    @User(
            username = "duck",
            categories = @Category(
                    archived = true
            )
    )
    @Test
    void activeCategoryShouldPresentInCategoriesList(CategoryJson[] categories) throws InterruptedException {
        CategoryJson category = categories[0];
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login("duck", "12345")
                .getHeader()
                .toProfilePage()
                .clickShowArchiveCategoryButton()
                .clickUnarchiveButtonForCategoryName(category.name())
                .clickUnarchiveButtonSubmit()
                .shouldBeVisibleUnarchiveSuccessMessage(category.name())
                .clickShowArchiveCategoryButton()
                .shouldVisibleActiveCategory(category.name());
    }

    @User
    @Test
    void changeName(UserJson user) {
        String name = randomName();
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .getHeader()
                .toProfilePage()
                .setName(name)
                .clickSaveButton()
                .shouldBeVisibleSaveChangesSuccessMessage()
                .checkName(name);
    }
}