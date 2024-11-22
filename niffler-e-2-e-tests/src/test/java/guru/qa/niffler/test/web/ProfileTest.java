package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static guru.qa.niffler.utils.RandomDataUtils.randomCategoryName;
import static guru.qa.niffler.utils.RandomDataUtils.randomName;

@WebTest
public class ProfileTest {

    @User(
            categories = @Category(
                    archived = true
            )
    )
    @ApiLogin
    @Test
    void archivedCategoryShouldPresentInCategoriesList(UserJson user) {
        final String categoryName = user.testData().categoryDescriptions()[0];
        Selenide.open(ProfilePage.URL, ProfilePage.class)
                .checkArchivedCategoryExists(categoryName);
    }

    @User(
            categories = @Category(
                    archived = false
            )
    )
    @ApiLogin
    @Test
    void activeCategoryShouldPresentInCategoriesList(UserJson user) {
        final String categoryName = user.testData().categoryDescriptions()[0];
        Selenide.open(ProfilePage.URL, ProfilePage.class)
                .checkCategoryExists(categoryName);
    }

    @User
    @ApiLogin
    @Test
    void shouldUpdateProfileWithAllFieldsSet() {
        final String newName = randomName();
        ProfilePage profilePage = Selenide.open(ProfilePage.URL, ProfilePage.class)
                .uploadPhotoFromClasspath("img/cat.png")
                .setName(newName)
                .submitProfile()
                .checkAlertMessage("Profile successfully updated");
        Selenide.refresh();
        profilePage.checkName(newName)
                .checkPhotoExist();
    }

    @User
    @ApiLogin
    @Test
    void shouldUpdateProfileWithOnlyRequiredFields() {
        final String newName = randomName();
        ProfilePage profilePage = Selenide.open(ProfilePage.URL, ProfilePage.class)
                .setName(newName)
                .submitProfile()
                .checkAlertMessage("Profile successfully updated");
        Selenide.refresh();
        profilePage.checkName(newName);
    }

    @User
    @ApiLogin
    @Test
    void shouldAddNewCategory() {
        String newCategory = randomCategoryName();
        Selenide.open(ProfilePage.URL, ProfilePage.class)
                .addCategory(newCategory)
                .checkAlertMessage("You've added new category:")
                .checkCategoryExists(newCategory);
    }

    @User(
            categories = {
                    @Category(name = "Food"),
                    @Category(name = "Bars"),
                    @Category(name = "Clothes"),
                    @Category(name = "Friends"),
                    @Category(name = "Music"),
                    @Category(name = "Sports"),
                    @Category(name = "Walks"),
                    @Category(name = "Books")
            }
    )
    @ApiLogin
    @Test
    void shouldForbidAddingMoreThat8Categories() {
        Selenide.open(ProfilePage.URL, ProfilePage.class)
                .checkThatCategoryInputDisabled();
    }

    @User
    @ApiLogin
    @ScreenShotTest(value = "img/profile-expected.png")
    void checkProfileImageTest(BufferedImage expectedProfileImage) throws IOException {
        Selenide.open(ProfilePage.URL, ProfilePage.class)
                .uploadPhotoFromClasspath("img/cat.png")
                .submitProfile()
                .checkProfileImage(expectedProfileImage);
    }
}