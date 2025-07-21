package com.example.quizme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.quizme.databinding.ActivityMainBinding;
import com.example.quizme.databinding.ActivityMainAdminBinding;
import com.example.quizme.databinding.ActivityMainUserBinding;

import me.ibrahimsn.lib.OnItemSelectedListener;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    com.example.quizme.databinding.ActivityMainAdminBinding adminBinding;
    com.example.quizme.databinding.ActivityMainUserBinding userBinding;
    private String userRole = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Kiểm tra session - nếu chưa đăng nhập thì chuyển về LoginActivity
        SharedPreferences prefs = getSharedPreferences("QuizApp", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);
        int userId = prefs.getInt("user_id", -1);
        
        if (!isLoggedIn || userId == -1) {
            // Chưa đăng nhập, chuyển về LoginActivity
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }
        
        // Lấy thông tin user role
        userRole = prefs.getString("user_role", "user");

        // Chọn layout theo role
        if ("admin".equals(userRole)) {
            adminBinding = com.example.quizme.databinding.ActivityMainAdminBinding.inflate(getLayoutInflater());
            setContentView(adminBinding.getRoot());
            setSupportActionBar(adminBinding.toolbar);
            adminBinding.bottomBar.setOnItemSelectedListener(index -> {
                FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
                switch (index) {
                    case 0:
                        transaction1.replace(R.id.content, new HomeFragment());
                        break;
                    case 1:
                        transaction1.replace(R.id.content, new LeaderboardsFragment());
                        break;
                    case 2:
                        transaction1.replace(R.id.content, new ProfileFragment());
                        break;
                }
                transaction1.commit();
                return false;
            });
        } else {
            userBinding = com.example.quizme.databinding.ActivityMainUserBinding.inflate(getLayoutInflater());
            setContentView(userBinding.getRoot());
            setSupportActionBar(userBinding.toolbar);
            userBinding.bottomBar.setOnItemSelectedListener(index -> {
                switch (index) {
                    case 0:
                        FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
                        transaction1.replace(R.id.content, new HomeFragment());
                        transaction1.commit();
                        break;
                    case 1:
                        FragmentTransaction friendsTransaction = getSupportFragmentManager().beginTransaction();
                        friendsTransaction.replace(R.id.content, new FriendsFragment());
                        friendsTransaction.commit();
                        break;
                    case 2:
                        FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
                        transaction2.replace(R.id.content, new LeaderboardsFragment());
                        transaction2.commit();
                        break;
                    case 3:
                        FragmentTransaction transaction3 = getSupportFragmentManager().beginTransaction();
                        transaction3.replace(R.id.content, new HistoryFragment());
                        transaction3.commit();
                        break;
                    case 4:
                        FragmentTransaction transaction4 = getSupportFragmentManager().beginTransaction();
                        transaction4.replace(R.id.content, new ProfileFragment());
                        transaction4.commit();
                        break;
                }
                return false;
            });
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, new HomeFragment());
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);

        // Ẩn menu quản trị nếu không phải admin
        if (!"admin".equals(userRole)) {
            menu.findItem(R.id.admin_menu).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.chat_list) {
            // Mở danh sách tin nhắn
            Intent intent = new Intent(this, ChatListActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.logout) {
            // Đăng xuất - xóa thông tin user khỏi SharedPreferences
            SharedPreferences prefs = getSharedPreferences("QuizApp", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.admin_manage_questions) {
            // Xử lý lựa chọn quản lý câu hỏi cho admin
            Intent intent = new Intent(this, AdminActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}