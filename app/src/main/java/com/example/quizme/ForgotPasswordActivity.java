package com.example.quizme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizme.databinding.ActivityForgotPasswordBinding;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;
    private DatabaseHelper databaseHelper;
    private ProgressDialog progressDialog;
    private Map<String, String> resetCodes = new HashMap<>(); // Lưu mã reset tạm thời
    private String currentEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang xử lý...");

        // Hiển thị màn hình nhập email
        showEmailInput();

        binding.sendCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.emailInput.getText().toString().trim();
                if (email.isEmpty()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Vui lòng nhập email!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!databaseHelper.isEmailExists(email)) {
                    Toast.makeText(ForgotPasswordActivity.this, "Email không tồn tại trong hệ thống!", Toast.LENGTH_SHORT).show();
                    return;
                }

                sendResetCode(email);
            }
        });

        binding.verifyCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = binding.codeInput.getText().toString().trim();
                if (code.isEmpty()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Vui lòng nhập mã xác nhận!", Toast.LENGTH_SHORT).show();
                    return;
                }

                verifyResetCode(code);
            }
        });

        binding.resetPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword = binding.newPasswordInput.getText().toString().trim();
                String confirmPassword = binding.confirmPasswordInput.getText().toString().trim();

                if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!newPassword.equals(confirmPassword)) {
                    Toast.makeText(ForgotPasswordActivity.this, "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (newPassword.length() < 6) {
                    Toast.makeText(ForgotPasswordActivity.this, "Mật khẩu phải có ít nhất 6 ký tự!", Toast.LENGTH_SHORT).show();
                    return;
                }

                resetPassword(newPassword);
            }
        });

        binding.backToLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void showEmailInput() {
        binding.emailLayout.setVisibility(View.VISIBLE);
        binding.codeLayout.setVisibility(View.GONE);
        binding.passwordLayout.setVisibility(View.GONE);
        binding.titleText.setText("Quên mật khẩu");
        
        // Add animation
        binding.emailLayout.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.slide_in_right));
    }

    private void showCodeInput() {
        binding.emailLayout.setVisibility(View.GONE);
        binding.codeLayout.setVisibility(View.VISIBLE);
        binding.passwordLayout.setVisibility(View.GONE);
        binding.titleText.setText("Xác nhận mã");
        
        // Add animation
        binding.codeLayout.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.slide_in_right));
    }

    private void showPasswordInput() {
        binding.emailLayout.setVisibility(View.GONE);
        binding.codeLayout.setVisibility(View.GONE);
        binding.passwordLayout.setVisibility(View.VISIBLE);
        binding.titleText.setText("Đặt mật khẩu mới");
        
        // Add animation
        binding.passwordLayout.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.slide_in_right));
    }

    private void sendResetCode(String email) {
        progressDialog.show();
        currentEmail = email;

        // Tạo mã reset ngẫu nhiên 6 số
        String resetCode = generateResetCode();
        resetCodes.put(email, resetCode);

        EmailHelper.sendResetCodeEmail(email, resetCode, new EmailHelper.EmailCallback() {
            @Override
            public void onSuccess() {
                progressDialog.dismiss();
                Toast.makeText(ForgotPasswordActivity.this, "Mã xác nhận đã được gửi đến email của bạn!", Toast.LENGTH_LONG).show();
                showCodeInput();
            }

            @Override
            public void onFailure(String error) {
                progressDialog.dismiss();
                Toast.makeText(ForgotPasswordActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private String generateResetCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    private void verifyResetCode(String code) {
        String savedCode = resetCodes.get(currentEmail);
        if (savedCode != null && savedCode.equals(code)) {
            Toast.makeText(ForgotPasswordActivity.this, "Mã xác nhận đúng!", Toast.LENGTH_SHORT).show();
            showPasswordInput();
        } else {
            Toast.makeText(ForgotPasswordActivity.this, "Mã xác nhận không đúng!", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetPassword(String newPassword) {
        progressDialog.show();

        // Lấy user từ database
        User user = databaseHelper.getUserByEmail(currentEmail);
        if (user != null) {
            user.setPassword(newPassword);
            databaseHelper.updateUser(user);
            
            progressDialog.dismiss();
            Toast.makeText(ForgotPasswordActivity.this, "Đặt lại mật khẩu thành công!", Toast.LENGTH_SHORT).show();
            
            // Xóa mã reset
            resetCodes.remove(currentEmail);
            
            // Chuyển về màn hình đăng nhập
            startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
            finish();
        } else {
            progressDialog.dismiss();
            Toast.makeText(ForgotPasswordActivity.this, "Không tìm thấy tài khoản!", Toast.LENGTH_SHORT).show();
        }
    }
} 