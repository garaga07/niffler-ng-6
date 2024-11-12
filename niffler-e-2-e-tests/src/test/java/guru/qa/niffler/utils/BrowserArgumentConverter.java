package guru.qa.niffler.utils;

import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideDriver;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;

public class BrowserArgumentConverter implements ArgumentConverter {
    @Override
    public Object convert(Object source, ParameterContext context) throws ArgumentConversionException {
        if (!(source instanceof Browser)) {
            throw new ArgumentConversionException("Expected a Browser enum");
        }

        Browser browser = (Browser) source;
        SelenideConfig config;
        switch (browser) {
            case CHROME:
                config = SelenideUtils.chromeConfig;
                break;
            case FIREFOX:
                config = SelenideUtils.firefoxConfig;
                break;
            default:
                throw new ArgumentConversionException("Unknown browser type");
        }

        // Создаем SelenideDriver с выбранной конфигурацией
        return new SelenideDriver(config);
    }
}