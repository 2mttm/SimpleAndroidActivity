package io.twometrue.pam.lab1;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CryptoCoinDetailActivity extends AppCompatActivity {
    private LineChart chartSparkline;
    private CoinGeckoResponse.CoinItem coin;
    private ProgressBar progressBar;
    private TextView priceText, priceChangeText, marketCapText, totalVolumeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        ImageView imageView = findViewById(R.id.newsDetailImage);
        TextView titleText = findViewById(R.id.newsDetailTitle);
        TextView symbolText = findViewById(R.id.newsDetailSymbol);
        priceText = findViewById(R.id.newsDetailPrice);
        priceChangeText = findViewById(R.id.newsDetailPriceChange);
        marketCapText = findViewById(R.id.newsDetailMarketCap);
        totalVolumeText = findViewById(R.id.newsDetailTotalVolume);
        chartSparkline = findViewById(R.id.chartSparkline);
        Button backButton = findViewById(R.id.backButton);
        progressBar = findViewById(R.id.progressBar);

        // Показываем загрузку графика
        progressBar.setVisibility(View.VISIBLE);
        chartSparkline.setVisibility(View.GONE);

        // Получаем переданный объект CoinItem
        coin = getIntent().getParcelableExtra("coin");

        if (coin != null) {
            titleText.setText(coin.getName());
            symbolText.setText("Symbol: " + coin.getSymbol());

            // ✅ Исправленный формат: Показываем полную цену без округления
            String fullPrice = formatDouble(coin.getCurrentPrice());
            priceText.setText(fullPrice);

            // ✅ Исправленный формат: Форматируем изменение цены в %
            double priceChange = coin.getPriceChangePercentage24h();
            String priceChangeFormatted = String.format(Locale.US, "%.2f%%", priceChange);
            priceChangeText.setText(priceChangeFormatted);

            // ✅ Исправленный цвет изменения цены
            if (priceChange > 0) {
                priceChangeText.setTextColor(Color.GREEN);  // Рост
            } else if (priceChange < 0) {
                priceChangeText.setTextColor(Color.RED);    // Падение
            } else {
                priceChangeText.setTextColor(Color.GRAY);   // Без изменений
            }

            // ✅ Исправленный формат: Капитализация и объём (используем `%.0f` вместо `%d`)
            marketCapText.setText("Market Cap: $" + String.format(Locale.US, "%,d", coin.getMarketCap()));
            totalVolumeText.setText("Total Volume: $" + String.format(Locale.US, "%,.0f", coin.getTotalVolume()));

            Glide.with(this)
                    .load(coin.getImage())
                    .placeholder(R.drawable.doge_logo)
                    .into(imageView);

            // Асинхронно загружаем график
            loadMarketChart(coin.getId());
        }

        // Кнопка назад
        backButton.setOnClickListener(v -> finish());
    }

    // Метод загрузки истории цен и построения графика
    private void loadMarketChart(String coinId) {
        CryptoCoinsApi apiService = ApiClient.getClient().create(CryptoCoinsApi.class);
        Call<MarketChartResponse> call = apiService.getMarketChart(coinId, "usd", 60, "daily");

        call.enqueue(new Callback<MarketChartResponse>() {
            @Override
            public void onResponse(Call<MarketChartResponse> call, Response<MarketChartResponse> response) {
                progressBar.setVisibility(View.GONE);
                chartSparkline.setVisibility(View.VISIBLE);

                if (response.isSuccessful() && response.body() != null) {
                    List<List<Double>> prices = response.body().getPrices();
                    List<Entry> entries = new ArrayList<>();

                    for (int i = 0; i < prices.size(); i++) {
                        entries.add(new Entry(i, prices.get(i).get(1).floatValue()));
                    }

                    setupChart(entries);
                }
            }

            @Override
            public void onFailure(Call<MarketChartResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                chartSparkline.setVisibility(View.VISIBLE);
            }
        });
    }

    // Метод настройки и отрисовки графика
    private void setupChart(List<Entry> entries) {
        LineDataSet dataSet = new LineDataSet(entries, "Price Trend");
        dataSet.setColor(Color.YELLOW);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(3f);
        dataSet.setDrawCircleHole(false);
        dataSet.setDrawCircles(true);
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);
        chartSparkline.setData(lineData);
        chartSparkline.invalidate(); // Перерисовка графика

        // Настройки осей
        XAxis xAxis = chartSparkline.getXAxis();
        xAxis.setTextColor(Color.WHITE);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis yAxisLeft = chartSparkline.getAxisLeft();
        yAxisLeft.setTextColor(Color.WHITE);

        YAxis yAxisRight = chartSparkline.getAxisRight();
        yAxisRight.setEnabled(false);

        // Легенда
        Legend legend = chartSparkline.getLegend();
        legend.setTextColor(Color.WHITE);

        // Описание
        Description description = new Description();
        description.setText("Price Trend (30 days)");
        description.setTextColor(Color.WHITE);
        chartSparkline.setDescription(description);
    }

    public static String formatDouble(double value) {
        String formatted = String.format(Locale.US, "%.2f", value); // Минимум 2 знака после запятой
        String strValue = Double.toString(value);

        // Если в оригинальном числе больше 2 знаков после запятой — добавляем их
        if (strValue.contains(".")) {
            int decimalPlaces = strValue.length() - strValue.indexOf('.') - 1;
            if (decimalPlaces > 2) {
                formatted = String.format(Locale.US, "%." + decimalPlaces + "f", value);
            }
        }
        return formatted;
    }
}
