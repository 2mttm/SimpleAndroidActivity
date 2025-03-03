package io.twometrue.pam.lab1;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CryptoCoinsApi {
    // Получение всех криптовалют с их текущими ценами
    @GET("coins/markets")
    Call<List<CoinGeckoResponse.CoinItem>> getAllCoins(
            @Query("vs_currency") String currency,
            @Query("order") String order,
            @Query("per_page") int perPage,
            @Query("page") int page,
            @Query("sparkline") boolean sparkline
    );

    // Получение истории цен для графика
    @GET("coins/{id}/market_chart")
    Call<MarketChartResponse> getMarketChart(
            @Path("id") String coinId,
            @Query("vs_currency") String currency,
            @Query("days") int days,
            @Query("interval") String interval
    );


    @GET("search/trending")
    Call<CoinGeckoResponse> getTrendingCoins();
}
