package com.example.quizme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SearchFriendsActivity extends AppCompatActivity {

    private EditText searchEditText;
    private RecyclerView searchResultsRecyclerView;
    private ImageView backBtn, friendRequestsBtn;
    private LinearLayout emptyStateContainer, loadingContainer;
    private DatabaseHelper databaseHelper;
    private SearchUserAdapter adapter;
    private List<User> searchResults;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends_simple);

        // Initialize views
        searchEditText = findViewById(R.id.searchEditText);
        searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView);
        backBtn = findViewById(R.id.backBtn);
        friendRequestsBtn = findViewById(R.id.friendRequestsBtn);
        emptyStateContainer = findViewById(R.id.emptyStateContainer);
        loadingContainer = findViewById(R.id.loadingContainer);

        databaseHelper = new DatabaseHelper(this);
        searchResults = new ArrayList<>();

        // Lấy user ID hiện tại
        SharedPreferences prefs = getSharedPreferences("QuizApp", MODE_PRIVATE);
        currentUserId = prefs.getInt("user_id", -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "Lỗi: Không thể xác định user", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupUI();
        setupRecyclerView();
        setupSearch();
    }

    private void setupUI() {
        // Back button
        backBtn.setOnClickListener(v -> finish());

        // Friend requests button
        friendRequestsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, FriendRequestsActivity.class);
            startActivity(intent);
        });

        // Show empty state initially
        showEmptyState();
    }

    private void setupRecyclerView() {
        adapter = new SearchUserAdapter(searchResults, new SearchUserAdapter.OnUserClickListener() {
            @Override
            public void onAddFriendClick(User user, int position) {
                sendFriendRequest(user, position);
            }
        }, databaseHelper, currentUserId);

        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchResultsRecyclerView.setAdapter(adapter);
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                if (query.length() >= 2) {
                    searchUsers(query);
                } else if (query.length() == 0) {
                    showEmptyState();
                }
            }
        });
    }

    private void searchUsers(String query) {
        showLoadingState();

        // Tìm kiếm trong database
        new Thread(() -> {
            try {
                List<User> results = databaseHelper.searchUsers(query, currentUserId);
                
                runOnUiThread(() -> {
                    searchResults.clear();
                    searchResults.addAll(results);
                    adapter.notifyDataSetChanged();
                    
                    if (results.isEmpty()) {
                        showNoResultsState();
                    } else {
                        showResultsState();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi tìm kiếm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    showEmptyState();
                });
            }
        }).start();
    }

    private void sendFriendRequest(User user, int position) {
        System.out.println("SearchFriendsActivity: Sending friend request to " + user.getName());
        
        // Show loading state immediately
        adapter.showLoadingState(position);
        
        new Thread(() -> {
            try {
                long result = databaseHelper.sendFriendRequest(currentUserId, user.getUserId());
                System.out.println("SearchFriendsActivity: Friend request result: " + result);
                
                runOnUiThread(() -> {
                    if (result != -1) {
                        Toast.makeText(this, "✅ Đã gửi lời mời kết bạn đến " + user.getName(), Toast.LENGTH_SHORT).show();
                        System.out.println("SearchFriendsActivity: Updating adapter for position " + position);
                        // Cập nhật UI để hiển thị trạng thái đã gửi lời mời
                        adapter.markAsFriendRequestSent(position);
                    } else {
                        System.out.println("SearchFriendsActivity: Failed to send friend request");
                        Toast.makeText(this, "❌ Không thể gửi lời mời. Có thể đã tồn tại lời mời trước đó.", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                System.out.println("SearchFriendsActivity: Error sending friend request: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void showEmptyState() {
        emptyStateContainer.setVisibility(View.VISIBLE);
        loadingContainer.setVisibility(View.GONE);
        searchResultsRecyclerView.setVisibility(View.GONE);
    }

    private void showLoadingState() {
        emptyStateContainer.setVisibility(View.GONE);
        loadingContainer.setVisibility(View.VISIBLE);
        searchResultsRecyclerView.setVisibility(View.GONE);
    }

    private void showResultsState() {
        emptyStateContainer.setVisibility(View.GONE);
        loadingContainer.setVisibility(View.GONE);
        searchResultsRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showNoResultsState() {
        emptyStateContainer.setVisibility(View.VISIBLE);
        loadingContainer.setVisibility(View.GONE);
        searchResultsRecyclerView.setVisibility(View.GONE);
        
        // TODO: Cập nhật text để hiển thị "Không tìm thấy kết quả"
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
