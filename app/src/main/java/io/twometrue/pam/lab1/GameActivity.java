package io.twometrue.pam.lab1;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private int score = 0;
    private int highScore = 0;
    private Handler handler = new Handler();
    private int timeLeft = 10;
    private Random random = new Random();
    private boolean isGameRunning = false;
    private Runnable timerRunnable;

    private ImageView dogeImageView;
    private TextView timerTextView;
    private Button backButton;
    private Button retryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        dogeImageView = findViewById(R.id.dogeImageView);
        timerTextView = findViewById(R.id.timerTextView);
        backButton = findViewById(R.id.backButton);
        retryButton = findViewById(R.id.retryButton);

        // Load HighScore from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);
        highScore = sharedPreferences.getInt("HighScore", 0);

        // Initialize Retry button as hidden
        retryButton.setVisibility(View.GONE);

        // Doge Click Me Button functionality
        dogeImageView.setOnClickListener(v -> {
            if (isGameRunning) {
                score++;
                // Move Doge to a random position
                int x = random.nextInt(800);
                int y = random.nextInt(1600);
                dogeImageView.setX(x);
                dogeImageView.setY(y);
            }
        });

        // Timer logic
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (timeLeft > 0) {
                    timeLeft--;
                    timerTextView.setText("Time Left: " + timeLeft);
                    handler.postDelayed(this, 1000);
                } else {
                    endGame();
                }
            }
        };

        // Back button functionality
        backButton.setOnClickListener(v -> {
            handler.removeCallbacksAndMessages(null);
            finish();
        });

        // Retry button functionality
        retryButton.setOnClickListener(v -> restartGame());
    }

    @Override
    protected void onResume() {
        super.onResume();
        startGame();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null); // Остановка таймера при выходе
        isGameRunning = false;
    }

    private void startGame() {
        score = 0;
        timeLeft = 10;
        dogeImageView.setEnabled(true);
        retryButton.setVisibility(View.GONE);
        timerTextView.setText("Time Left: " + timeLeft);
        isGameRunning = true;

        // Сбрасываем цветовое изменение (убираем серый фильтр)
        dogeImageView.clearColorFilter();

        handler.post(timerRunnable); // Запуск таймера при возврате на активити
    }

    private void restartGame() {
        startGame();
    }

    private void endGame() {
        isGameRunning = false;
        dogeImageView.setEnabled(false);
        retryButton.setVisibility(View.VISIBLE);

        // Применяем серый фильтр к картинке доги
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0); // Убираем цвет
        dogeImageView.setColorFilter(new ColorMatrixColorFilter(matrix));

        // Check and update HighScore
        if (score > highScore) {
            highScore = score;
            saveHighScore();
        }
        showNotification("Game Over", "Score: " + score + " | High Score: " + highScore);
    }

    private void saveHighScore() {
        SharedPreferences sharedPreferences = getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("HighScore", highScore);
        editor.apply();
    }

    private void showNotification(String title, String content) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "game_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        NotificationChannel channel = new NotificationChannel(channelId, "Game Notifications", NotificationManager.IMPORTANCE_HIGH);
        notificationManager.createNotificationChannel(channel);

        Notification notification = new Notification.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build();

        notificationManager.notify(1, notification);
    }
}
