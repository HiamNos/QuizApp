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

public class UpdateInfoActivity extends AppCompatActivity {

    private EditText nameBox, emailBox, phoneBox;
    private Button updateInfoBtn;
    private ImageView backButton;
    private TextView toolbarTitle;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info);

        initViews();
        setupClickListeners();
        
        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("QuizApp", MODE_PRIVATE);
        
        loadCurrentInfo();
    }

    private void initViews() {
        nameBox = findViewById(R.id.nameBox);
        emailBox = findViewById(R.id.emailBox);
        phoneBox = findViewById(R.id.phoneBox);
        updateInfoBtn = findViewById(R.id.updateInfoBtn);
        backButton = findViewById(R.id.backButton);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        
        // Set toolbar title
        toolbarTitle.setText("Cập nhật thông tin");
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        updateInfoBtn.setOnClickListener(v -> updateInfo());
    }

    private void loadCurrentInfo() {
        // Lấy thông tin user hiện tại
        int userId = sharedPreferences.getInt("user_id", -1);
        if (userId != -1) {
            User user = databaseHelper.getUserById(userId);
            if (user != null) {
                nameBox.setText(user.getName());
                emailBox.setText(user.getEmail());
                phoneBox.setText(user.getPhone() != null ? user.getPhone() : "");
            }
        }
    }

    private void updateInfo() {
        String name = nameBox.getText().toString().trim();
        String email = emailBox.getText().toString().trim();
        String phone = phoneBox.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(name)) {
            nameBox.setError("Vui lòng nhập họ tên");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            emailBox.setError("Vui lòng nhập email");
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailBox.setError("Email không hợp lệ");
            return;
        }

        if (!TextUtils.isEmpty(phone) && phone.length() < 10) {
            phoneBox.setError("Số điện thoại không hợp lệ");
            return;
        }

        // Lấy userId từ SharedPreferences
        int userId = sharedPreferences.getInt("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật thông tin user trong database
        User currentUser = databaseHelper.getUserById(userId);
        if (currentUser != null) {
            currentUser.setName(name);
            currentUser.setEmail(email);
            currentUser.setPhone(phone);
            
            databaseHelper.updateUser(currentUser);
            
            // Cập nhật SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("user_name", name);
            editor.putString("user_email", email);
            editor.apply();
            
            Toast.makeText(this, "Thông tin đã được cập nhật thành công!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
        }
    }
}
