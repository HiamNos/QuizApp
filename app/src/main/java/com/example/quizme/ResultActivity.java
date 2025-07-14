package com.example.quizme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.quizme.databinding.ActivityResultBinding;

public class ResultActivity extends AppCompatActivity {

    ActivityResultBinding binding;
    int POINTS = 10;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);

        int correctAnswers = getIntent().getIntExtra("correct", 0);
        int totalQuestions = getIntent().getIntExtra("total", 0);

        long points = correctAnswers * POINTS;

        binding.score.setText(String.format("%d/%d", correctAnswers, totalQuestions));
        binding.earnedCoins.setText(String.valueOf(points));

        // Cập nhật coins cho user trong SQLite
        SharedPreferences prefs = getSharedPreferences("QuizApp", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        
        if (userId != -1) {
            User user = databaseHelper.getUserById(userId);
            if (user != null) {
                long newCoins = user.getCoins() + points;
                databaseHelper.updateUserCoins(userId, newCoins);
                
                // Cập nhật SharedPreferences
                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong("user_coins", newCoins);
                editor.apply();
                
                Toast.makeText(this, "Bạn đã kiếm được " + points + " xu!", Toast.LENGTH_SHORT).show();
            }
        }

        binding.restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ResultActivity.this, MainActivity.class));
                finishAffinity();
            }
        });
    }
}