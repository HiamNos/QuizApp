package com.example.quizme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizme.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    DatabaseHelper databaseHelper;
    ProgressDialog dialog;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Đang đăng nhập...");

        // KHÔNG tự động chuyển sang MainActivity ở đây!
        // SharedPreferences prefs = getSharedPreferences("QuizApp", MODE_PRIVATE);
        // int userId = prefs.getInt("user_id", -1);
        // if (userId != -1) {
        //     startActivity(new Intent(LoginActivity.this, MainActivity.class));
        //     finish();
        // }

        SharedPreferences prefs = getSharedPreferences("QuizApp", MODE_PRIVATE);

        // Setup password visibility toggle
        ImageView togglePasswordVisibility = findViewById(R.id.togglePasswordVisibility);
        togglePasswordVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    // Hide password
                    binding.passwordBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    togglePasswordVisibility.setImageResource(R.drawable.ic_visibility_off);
                    isPasswordVisible = false;
                } else {
                    // Show password
                    binding.passwordBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    togglePasswordVisibility.setImageResource(R.drawable.ic_visibility);
                    isPasswordVisible = true;
                }
                // Move cursor to end
                binding.passwordBox.setSelection(binding.passwordBox.getText().length());
            }
        });

        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.emailBox.getText().toString().trim();
                String password = binding.passwordBox.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }

                dialog.show();

                // Xác thực user từ SQLite
                User user = databaseHelper.authenticateUser(email, password);
                if (user != null) {
                    // Lưu thông tin user vào SharedPreferences
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("user_id", user.getUserId());
                    editor.putString("user_name", user.getName());
                    editor.putString("user_email", user.getEmail());
                    editor.putString("user_role", user.getRole());
                    editor.putBoolean("is_logged_in", true);
                    editor.apply();

                    dialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    dialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Email hoặc mật khẩu không đúng!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.createNewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        binding.forgotPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });
    }
}