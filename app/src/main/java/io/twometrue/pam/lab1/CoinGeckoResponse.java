package io.twometrue.pam.lab1;

import android.os.Parcel;
import android.os.Parcelable;

public class CoinGeckoResponse {
    public static class CoinItem implements Parcelable {
        private String id;
        private String symbol;
        private String name;
        private String image;
        private double current_price;
        private long market_cap;
        private int market_cap_rank;
        private double total_volume;
        private double high_24h;
        private double low_24h;
        private double price_change_24h;
        private double price_change_percentage_24h;

        public String getId() {
            return id;
        }

        public String getSymbol() {
            return symbol;
        }

        public String getName() {
            return name;
        }

        public String getImage() {
            return image;
        }

        public double getCurrentPrice() {
            return current_price;
        }

        public long getMarketCap() {
            return market_cap;
        }

        public int getMarketCapRank() {
            return market_cap_rank;
        }

        public double getTotalVolume() {
            return total_volume;
        }

        public double getHigh24h() {
            return high_24h;
        }

        public double getLow24h() {
            return low_24h;
        }

        public double getPriceChange24h() {
            return price_change_24h;
        }

        public double getPriceChangePercentage24h() {
            return price_change_percentage_24h;
        }

        protected CoinItem(Parcel in) {
            id = in.readString();
            symbol = in.readString();
            name = in.readString();
            image = in.readString();
            current_price = in.readDouble();
            market_cap = in.readLong();
            market_cap_rank = in.readInt();
            total_volume = in.readDouble();
            high_24h = in.readDouble();
            low_24h = in.readDouble();
            price_change_24h = in.readDouble();
            price_change_percentage_24h = in.readDouble();
        }

        public static final Creator<CoinItem> CREATOR = new Creator<CoinItem>() {
            @Override
            public CoinItem createFromParcel(Parcel in) {
                return new CoinItem(in);
            }

            @Override
            public CoinItem[] newArray(int size) {
                return new CoinItem[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(id);
            parcel.writeString(symbol);
            parcel.writeString(name);
            parcel.writeString(image);
            parcel.writeDouble(current_price);
            parcel.writeLong(market_cap);
            parcel.writeInt(market_cap_rank);
            parcel.writeDouble(total_volume);
            parcel.writeDouble(high_24h);
            parcel.writeDouble(low_24h);
            parcel.writeDouble(price_change_24h);
            parcel.writeDouble(price_change_percentage_24h);
        }
    }
}
