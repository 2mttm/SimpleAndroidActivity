package io.twometrue.pam.lab1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText editText = findViewById(R.id.editText);
        Button searchButton = findViewById(R.id.searchButton);
        Button gameButton = findViewById(R.id.gameButton);

        // Search button functionality
        searchButton.setOnClickListener(v -> {
            String query = editText.getText().toString();
            if (!query.isEmpty()) {
                Uri searchUri = Uri.parse("https://www.google.com/search?q=" + query);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, searchUri);
                startActivity(browserIntent);
            }
        });

        // Game button functionality
        gameButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            startActivity(intent);
        });
    }
}