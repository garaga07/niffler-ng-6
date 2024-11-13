package guru.qa.niffler.utils;

import com.codeborne.selenide.SelenideConfig;

public class SelenideUtils {

    public static final SelenideConfig chromeConfig = createConfig("chrome");
    public static final SelenideConfig firefoxConfig = createConfig("firefox");

    private static SelenideConfig createConfig(String browserName) {
        return new SelenideConfig()
                .browser(browserName)
                .pageLoadStrategy("eager")
                .timeout(5000L);
    }
}