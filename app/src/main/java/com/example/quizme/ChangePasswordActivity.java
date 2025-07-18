package com.example.quizme;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText currentPasswordBox, newPasswordBox, confirmPasswordBox;
    private Button changePasswordBtn;
    private ImageView backButton;
    private TextView toolbarTitle;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        initViews();
        setupClickListeners();
        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("QuizApp", MODE_PRIVATE);
    }

    private void initViews() {
        currentPasswordBox = findViewById(R.id.currentPasswordBox);
        newPasswordBox = findViewById(R.id.newPasswordBox);
        confirmPasswordBox = findViewById(R.id.confirmPasswordBox);
        changePasswordBtn = findViewById(R.id.changePasswordBtn);
        backButton = findViewById(R.id.backButton);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        
        // Set toolbar title
        toolbarTitle.setText("Đổi mật khẩu");
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        changePasswordBtn.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String currentPassword = currentPasswordBox.getText().toString().trim();
        String newPassword = newPasswordBox.getText().toString().trim();
        String confirmPassword = confirmPasswordBox.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(currentPassword)) {
            currentPasswordBox.setError("Vui lòng nhập mật khẩu hiện tại");
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            newPasswordBox.setError("Vui lòng nhập mật khẩu mới");
            return;
        }

        if (newPassword.length() < 6) {
            newPasswordBox.setError("Mật khẩu phải có ít nhất 6 ký tự");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            confirmPasswordBox.setError("Mật khẩu xác nhận không khớp");
            return;
        }

        // Lấy userId từ SharedPreferences
        int userId = sharedPreferences.getInt("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật mật khẩu trong database
        boolean success = databaseHelper.updateUserPassword(userId, currentPassword, newPassword);
        
        if (success) {
            Toast.makeText(this, "Mật khẩu đã được thay đổi thành công!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            currentPasswordBox.setError("Mật khẩu hiện tại không đúng");
        }
    }
}
