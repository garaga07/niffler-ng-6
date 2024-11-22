package guru.qa.niffler.api;

import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.CurrencyValues;
import guru.qa.niffler.model.rest.SpendJson;
import retrofit2.Call;
import retrofit2.http.*;

import javax.annotation.Nullable;
import java.util.List;

public interface SpendApi {

    @POST("internal/spends/add")
    Call<SpendJson> addSpend(@Body SpendJson spend);

    @PATCH("internal/spends/edit")
    Call<SpendJson> editSpend(@Body SpendJson spend);

    @GET("internal/spends/{id}")
    Call<SpendJson> getSpend(@Path("id") String id);

    @GET("internal/spends/all")
    Call<List<SpendJson>> allSpends(@Query("username") String username,
                                    @Query("filterCurrency") @Nullable CurrencyValues filterCurrency,
                                    @Query("from") @Nullable String from,
                                    @Query("to") @Nullable String to);

    @DELETE("internal/spends/remove")
    Call<Void> removeSpends(@Query("username") String username, @Query("ids") List<String> ids);

    @POST("internal/categories/add")
    Call<CategoryJson> addCategory(@Body CategoryJson category);

    @PATCH("internal/categories/update")
    Call<CategoryJson> updateCategory(@Body CategoryJson category);

    @GET("internal/categories/all")
    Call<List<CategoryJson>> allCategories(@Query("username") String username);
}