package guru.qa.niffler.test.grpc;

import com.google.protobuf.Empty;
import guru.qa.niffler.grpc.CalculateRequest;
import guru.qa.niffler.grpc.CalculateResponse;
import guru.qa.niffler.grpc.Currency;
import guru.qa.niffler.grpc.CurrencyResponse;
import guru.qa.niffler.grpc.CurrencyValues;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class CurrencyGrpcTest extends BaseGrpcTest {

    @Test
    void allCurrenciesShouldReturned() {
        final CurrencyResponse response = blockingStub.getAllCurrencies(Empty.getDefaultInstance());
        final List<Currency> allCurrenciesList = response.getAllCurrenciesList();
        Assertions.assertEquals(4, allCurrenciesList.size());
    }

    @Test
    void calculateRateShouldReturnCorrectAmount() {
        double amount = 100.0;
        CurrencyValues spendCurrency = CurrencyValues.USD;
        CurrencyValues desiredCurrency = CurrencyValues.EUR;
        double expectedAmount = 92.59;

        CalculateRequest request = CalculateRequest.newBuilder()
                .setAmount(amount)
                .setSpendCurrency(spendCurrency)
                .setDesiredCurrency(desiredCurrency)
                .build();

        CalculateResponse response = blockingStub.calculateRate(request);
        Assertions.assertEquals(expectedAmount, response.getCalculatedAmount(), 0.01);
    }

    @Test
    void calculateRateShouldHandleDifferentCurrencies() {
        double amount = 150.0;
        CurrencyValues spendCurrency = CurrencyValues.EUR;
        CurrencyValues desiredCurrency = CurrencyValues.RUB;
        double expectedAmount = 10800.0;

        CalculateRequest request = CalculateRequest.newBuilder()
                .setAmount(amount)
                .setSpendCurrency(spendCurrency)
                .setDesiredCurrency(desiredCurrency)
                .build();

        CalculateResponse response = blockingStub.calculateRate(request);
        Assertions.assertEquals(expectedAmount, response.getCalculatedAmount(), 0.01);
    }

    @Test
    void calculateRateShouldHandleSameCurrencyConversion() {
        double amount = 200.0;
        CurrencyValues spendCurrency = CurrencyValues.USD;
        CurrencyValues desiredCurrency = CurrencyValues.USD;
        double expectedAmount = 200.0;

        CalculateRequest request = CalculateRequest.newBuilder()
                .setAmount(amount)
                .setSpendCurrency(spendCurrency)
                .setDesiredCurrency(desiredCurrency)
                .build();

        CalculateResponse response = blockingStub.calculateRate(request);
        Assertions.assertEquals(expectedAmount, response.getCalculatedAmount(), 0.01);
    }
}