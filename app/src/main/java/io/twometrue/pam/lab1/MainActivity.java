package io.twometrue.pam.lab1;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private CryptoCoinsAdapter adapter;

    private ImageButton refreshButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView dogeLogo = findViewById(R.id.dogeLogo);

        EditText editText = findViewById(R.id.editText);
        Button searchButton = findViewById(R.id.searchButton);
        Button gameButton = findViewById(R.id.gameButton);
        progressBar = findViewById(R.id.progressBar);
        refreshButton = findViewById(R.id.refreshButton);
        recyclerView = findViewById(R.id.recyclerViewNews);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CryptoCoinsAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);

        refreshButton.setOnClickListener(v -> loadAllCoins());

        // Search button functionality
        searchButton.setOnClickListener(v -> {
            String query = editText.getText().toString();
            if (!query.isEmpty()) {
                Uri searchUri = Uri.parse("https://beincrypto.com/?s=" + query);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, searchUri);
                startActivity(browserIntent);
            }
        });

        // Game button functionality
        gameButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            startActivity(intent);
        });
        loadAllCoins();
    }

    private void loadAllCoins() {
        refreshButton.setEnabled(false);
        progressBar.setVisibility(VISIBLE);
        CryptoCoinsApi apiService = ApiClient.getClient().create(CryptoCoinsApi.class);
        Call<List<CoinGeckoResponse.CoinItem>> call = apiService.getAllCoins("usd", "market_cap_desc", 100, 1, false);

        call.enqueue(new Callback<List<CoinGeckoResponse.CoinItem>>() {
            @Override
            public void onResponse(Call<List<CoinGeckoResponse.CoinItem>> call, Response<List<CoinGeckoResponse.CoinItem>> response) {
                refreshButton.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    List<CoinGeckoResponse.CoinItem> coins = response.body();
                    adapter = new CryptoCoinsAdapter(MainActivity.this, coins);
                    recyclerView.setAdapter(adapter);
                    progressBar.setVisibility(GONE);
                }
            }

            @Override
            public void onFailure(Call<List<CoinGeckoResponse.CoinItem>> call, Throwable t) {
                refreshButton.setEnabled(true);
                progressBar.setVisibility(GONE);
            }
        });
    }



}