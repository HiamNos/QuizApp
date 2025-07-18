package com.example.quizme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.quizme.SpinWheel.LuckyWheelView;
import com.example.quizme.SpinWheel.model.LuckyItem;
import com.example.quizme.databinding.ActivitySpinnerBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class SpinnerActivity extends AppCompatActivity {

    ActivitySpinnerBinding binding;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySpinnerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);

        List<LuckyItem> data = new ArrayList<>();

        LuckyItem luckyItem1 = new LuckyItem();
        luckyItem1.topText = "5";
        luckyItem1.secondaryText = "XU";
        luckyItem1.textColor = Color.parseColor("#212121");
        luckyItem1.color = Color.parseColor("#eceff1");
        data.add(luckyItem1);

        LuckyItem luckyItem2 = new LuckyItem();
        luckyItem2.topText = "10";
        luckyItem2.secondaryText = "XU";
        luckyItem2.color = Color.parseColor("#00cf00");
        luckyItem2.textColor = Color.parseColor("#ffffff");
        data.add(luckyItem2);

        LuckyItem luckyItem3 = new LuckyItem();
        luckyItem3.topText = "15";
        luckyItem3.secondaryText = "XU";
        luckyItem3.textColor = Color.parseColor("#212121");
        luckyItem3.color = Color.parseColor("#eceff1");
        data.add(luckyItem3);

        LuckyItem luckyItem4 = new LuckyItem();
        luckyItem4.topText = "20";
        luckyItem4.secondaryText = "XU";
        luckyItem4.color = Color.parseColor("#7f00d9");
        luckyItem4.textColor = Color.parseColor("#ffffff");
        data.add(luckyItem4);

        LuckyItem luckyItem5 = new LuckyItem();
        luckyItem5.topText = "25";
        luckyItem5.secondaryText = "XU";
        luckyItem5.textColor = Color.parseColor("#212121");
        luckyItem5.color = Color.parseColor("#eceff1");
        data.add(luckyItem5);

        LuckyItem luckyItem6 = new LuckyItem();
        luckyItem6.topText = "30";
        luckyItem6.secondaryText = "XU";
        luckyItem6.color = Color.parseColor("#dc0000");
        luckyItem6.textColor = Color.parseColor("#ffffff");
        data.add(luckyItem6);

        LuckyItem luckyItem7 = new LuckyItem();
        luckyItem7.topText = "35";
        luckyItem7.secondaryText = "XU";
        luckyItem7.textColor = Color.parseColor("#212121");
        luckyItem7.color = Color.parseColor("#eceff1");
        data.add(luckyItem7);

        LuckyItem luckyItem8 = new LuckyItem();
        luckyItem8.topText = "0";
        luckyItem8.secondaryText = "XU";
        luckyItem8.color = Color.parseColor("#008bff");
        luckyItem8.textColor = Color.parseColor("#ffffff");
        data.add(luckyItem8);

        binding.wheelview.setData(data);
        binding.wheelview.setRound(5);

        // Hiển thị trạng thái spin
        updateSpinStatus();

        binding.spinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kiểm tra xem hôm nay đã quay chưa
                if (hasSpunToday()) {
                    Toast.makeText(SpinnerActivity.this, "Hôm nay bạn đã quay rồi! Hãy quay lại vào ngày mai.", Toast.LENGTH_LONG).show();
                    return;
                }

                Random r = new Random();
                int randomNumber = r.nextInt(8);

                binding.wheelview.startLuckyWheelWithTargetIndex(randomNumber);
            }
        });

        binding.wheelview.setLuckyRoundItemSelectedListener(new LuckyWheelView.LuckyRoundItemSelectedListener() {
            @Override
            public void LuckyRoundItemSelected(int index) {
                updateCash(index);
            }
        });
    }

    void updateCash(int index) {
        long cash = 0;
        switch (index) {
            case 0:
                cash = 5;
                break;
            case 1:
                cash = 10;
                break;
            case 2:
                cash = 15;
                break;
            case 3:
                cash = 20;
                break;
            case 4:
                cash = 25;
                break;
            case 5:
                cash = 30;
                break;
            case 6:
                cash = 35;
                break;
            case 7:
                cash = 0;
                break;
        }

        // Cập nhật coins cho user trong SQLite
        SharedPreferences prefs = getSharedPreferences("QuizApp", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        
        if (userId != -1) {
            User user = databaseHelper.getUserById(userId);
            if (user != null) {
                long newCoins = user.getCoins() + cash;
                databaseHelper.updateUserCoins(userId, newCoins);
                
                // Cập nhật SharedPreferences
                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong("user_coins", newCoins);
                editor.apply();
                
                // Lưu ngày đã quay hôm nay
                saveSpinDate();
                
                Toast.makeText(SpinnerActivity.this, "Đã thêm " + cash + " xu vào tài khoản.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    
    // Kiểm tra xem hôm nay đã quay chưa
    private boolean hasSpunToday() {
        SharedPreferences prefs = getSharedPreferences("QuizApp", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        
        if (userId == -1) {
            return false;
        }
        
        String lastSpinDate = prefs.getString("last_spin_date_" + userId, "");
        String today = getCurrentDate();
        
        return today.equals(lastSpinDate);
    }
    
    // Lưu ngày đã quay
    private void saveSpinDate() {
        SharedPreferences prefs = getSharedPreferences("QuizApp", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        
        if (userId != -1) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("last_spin_date_" + userId, getCurrentDate());
            editor.apply();
        }
    }
    
    // Lấy ngày hiện tại dưới dạng string
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
    
    // Cập nhật hiển thị trạng thái spin
    private void updateSpinStatus() {
        if (hasSpunToday()) {
            binding.spinStatus.setText("Hôm nay bạn đã quay rồi!\nHãy quay lại vào ngày mai");
            binding.spinStatus.setBackground(getResources().getDrawable(R.drawable.status_unavailable));
            binding.spinBtn.setAlpha(0.5f);
            binding.spinBtn.setEnabled(false);
        } else {
            binding.spinStatus.setText("Hôm nay bạn có thể quay!");
            binding.spinStatus.setBackground(getResources().getDrawable(R.drawable.status_available));
            binding.spinBtn.setAlpha(1.0f);
            binding.spinBtn.setEnabled(true);
        }
    }
}