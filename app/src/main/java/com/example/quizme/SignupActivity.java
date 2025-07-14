package com.example.quizme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizme.databinding.ActivitySignupBinding;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SignupActivity extends AppCompatActivity {

    ActivitySignupBinding binding;
    DatabaseHelper databaseHelper;
    ProgressDialog dialog;
    private Map<String, String> verificationCodes = new HashMap<>();
    private String currentEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Đang xử lý...");

        // Hiển thị màn hình đăng ký
        showSignupForm();

        binding.createNewBtn.setOnClickListener(v -> {
            String email = binding.emailBox.getText().toString().trim();
            String password = binding.passwordBox.getText().toString().trim();
            String name = binding.nameBox.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
                Toast.makeText(SignupActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(SignupActivity.this, "Mật khẩu phải có ít nhất 6 ký tự!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (databaseHelper.isEmailExists(email)) {
                Toast.makeText(SignupActivity.this, "Email đã tồn tại trong hệ thống!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gửi mã xác nhận
            sendVerificationCode(email, name, password);
        });

        binding.verifyCodeBtn.setOnClickListener(v -> {
            String code = binding.verificationCodeInput.getText().toString().trim();
            if (code.isEmpty()) {
                Toast.makeText(SignupActivity.this, "Vui lòng nhập mã xác nhận!", Toast.LENGTH_SHORT).show();
                return;
            }

            verifyCode(code);
        });

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });
    }

    private void showSignupForm() {
        binding.signupLayout.setVisibility(View.VISIBLE);
        binding.verificationLayout.setVisibility(View.GONE);
    }

    public void showSignupForm(View view) {
        showSignupForm();
    }

    private void showVerificationForm() {
        binding.signupLayout.setVisibility(View.GONE);
        binding.verificationLayout.setVisibility(View.VISIBLE);
    }

    private void sendVerificationCode(String email, String name, String password) {
        dialog.show();
        currentEmail = email;

        // Tạo mã xác nhận 6 số
        String verificationCode = generateVerificationCode();
        verificationCodes.put(email, verificationCode);

        // Log để debug
        System.out.println("Sending verification code: " + verificationCode + " to: " + email);

        EmailHelper.sendVerificationCodeEmail(email, verificationCode, new EmailHelper.EmailCallback() {
            @Override
            public void onSuccess() {
                dialog.dismiss();
                Toast.makeText(SignupActivity.this, "Mã xác nhận đã được gửi đến email của bạn!", Toast.LENGTH_LONG).show();
                showVerificationForm();
            }

            @Override
            public void onFailure(String error) {
                dialog.dismiss();
                System.err.println("Email sending failed: " + error);
                Toast.makeText(SignupActivity.this, "Lỗi gửi email: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private String generateVerificationCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    private void verifyCode(String code) {
        String savedCode = verificationCodes.get(currentEmail);
        if (savedCode != null && savedCode.equals(code)) {
            // Mã xác nhận đúng, tiến hành đăng ký
            completeRegistration();
        } else {
            Toast.makeText(SignupActivity.this, "Mã xác nhận không đúng!", Toast.LENGTH_SHORT).show();
        }
    }

    private void completeRegistration() {
        dialog.show();
        dialog.setMessage("Đang tạo tài khoản...");

        // Lấy thông tin từ input
        String email = binding.emailBox.getText().toString().trim();
        String password = binding.passwordBox.getText().toString().trim();
        String name = binding.nameBox.getText().toString().trim();

        final User user = new User(name, email, password, ""); // Không có referCode

        // Đăng ký user vào SQLite
        long userId = databaseHelper.registerUser(user);
        if (userId != -1) {
            // Lưu thông tin user vào SharedPreferences
            SharedPreferences prefs = getSharedPreferences("QuizApp", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("user_id", (int) userId);
            editor.putString("user_name", user.getName());
            editor.putString("user_email", user.getEmail());
            editor.putString("user_role", user.getRole());
            editor.apply();

            dialog.dismiss();
            Toast.makeText(SignupActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
            
            // Xóa mã xác nhận
            verificationCodes.remove(currentEmail);
            
            startActivity(new Intent(SignupActivity.this, MainActivity.class));
            finish();
        } else {
            dialog.dismiss();
            Toast.makeText(SignupActivity.this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
        }
    }
}