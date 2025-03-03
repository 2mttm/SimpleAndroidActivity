package io.twometrue.pam.lab1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;

public class CryptoCoinsAdapter extends RecyclerView.Adapter<CryptoCoinsAdapter.ViewHolder> {
    private final Context context;
    private final List<CoinGeckoResponse.CoinItem> coinList;

    public CryptoCoinsAdapter(Context context, List<CoinGeckoResponse.CoinItem> coinList) {
        this.context = context;
        this.coinList = coinList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_crypto_news, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CoinGeckoResponse.CoinItem coin = coinList.get(position);

        // Устанавливаем название и символ
        holder.coinName.setText(coin.getName());
        holder.coinSymbol.setText(coin.getSymbol().toUpperCase());

        // Загружаем изображение
        Glide.with(context)
                .load(coin.getImage())
                .placeholder(R.drawable.doge_logo)
                .into(holder.coinImage);

        // Форматируем цену с двумя знаками после запятой
        String priceFormatted = String.format(Locale.US, "$%,.2f", coin.getCurrentPrice());
        holder.coinPrice.setText(priceFormatted);

        // Форматируем изменение цены в процентах
        double priceChange = coin.getPriceChangePercentage24h();
        String priceChangeText = String.format(Locale.US, "%.2f%%", priceChange);
        holder.coinPriceChange.setText(priceChangeText);

        // Устанавливаем цвет изменения цены
        if (priceChange > 0) {
            holder.coinPriceChange.setTextColor(Color.GREEN);  // Рост
        } else if (priceChange < 0) {
            holder.coinPriceChange.setTextColor(Color.RED);    // Падение
        } else {
            holder.coinPriceChange.setTextColor(Color.GRAY);   // Без изменений
        }

        // Обработчик нажатия на элемент
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CryptoCoinDetailActivity.class);
            intent.putExtra("coin", coin);
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return coinList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView coinImage;
        TextView coinName, coinSymbol, coinPrice, coinPriceChange;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            coinImage = itemView.findViewById(R.id.coinImage);
            coinName = itemView.findViewById(R.id.coinName);
            coinSymbol = itemView.findViewById(R.id.coinSymbol);
            coinPrice = itemView.findViewById(R.id.coinPrice);
            coinPriceChange = itemView.findViewById(R.id.coinPriceChange);
        }
    }
}
