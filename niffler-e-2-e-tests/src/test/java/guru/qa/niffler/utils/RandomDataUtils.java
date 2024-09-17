package guru.qa.niffler.utils;

import com.github.javafaker.Faker;

public class RandomDataUtils {
    private static final Faker faker = new Faker();

    public static String randomUsername() {
        return faker.name().username();
    }

    public static String randomName() {
        return faker.name().name();
    }

    public static String randomSurname() {
        return faker.name().lastName();
    }

    public static String randomCategoryName() {
        return faker.commerce().department();
    }

    public static String randomPassword() {
        return faker.internet().password(3, 12);
    }

    public static String randomSentence(int wordsCount) {
        if (wordsCount <= 0) {
            throw new IllegalArgumentException("Words count must be greater than zero");
        }
        return faker.lorem().sentence(wordsCount).trim();
    }
}