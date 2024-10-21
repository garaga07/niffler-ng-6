package guru.qa.niffler.model;

import java.util.List;

public record TestData(
        String password,
        List<CategoryJson> categories,
        List<SpendJson> spendings,
        List<String> friends,   // Список друзей
        List<String> incomeInvites,   // Список входящих приглашений
        List<String> outcomeInvites   // Список исходящих приглашений
) {
}