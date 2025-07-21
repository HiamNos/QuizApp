package com.example.quizme;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FriendRequestsActivity extends AppCompatActivity {

    private RecyclerView friendRequestsRecyclerView;
    private ImageView backBtn;
    private LinearLayout emptyStateContainer, loadingContainer;
    private DatabaseHelper databaseHelper;
    private FriendRequestAdapter adapter;
    private List<FriendRequest> friendRequests;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);

        // Initialize views
        friendRequestsRecyclerView = findViewById(R.id.friendRequestsRecyclerView);
        backBtn = findViewById(R.id.backBtn);
        emptyStateContainer = findViewById(R.id.emptyStateContainer);
        loadingContainer = findViewById(R.id.loadingContainer);

        databaseHelper = new DatabaseHelper(this);
        friendRequests = new ArrayList<>();

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
        loadFriendRequests();
    }

    private void setupUI() {
        // Back button
        backBtn.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new FriendRequestAdapter(friendRequests, new FriendRequestAdapter.OnRequestActionListener() {
            @Override
            public void onAcceptRequest(FriendRequest request, int position) {
                acceptFriendRequest(request, position);
            }

            @Override
            public void onDeclineRequest(FriendRequest request, int position) {
                declineFriendRequest(request, position);
            }
        });

        friendRequestsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        friendRequestsRecyclerView.setAdapter(adapter);
    }

    private void loadFriendRequests() {
        showLoadingState();

        new Thread(() -> {
            try {
                List<FriendRequest> requests = databaseHelper.getPendingFriendRequests(currentUserId);
                
                runOnUiThread(() -> {
                    friendRequests.clear();
                    friendRequests.addAll(requests);
                    adapter.notifyDataSetChanged();
                    
                    if (requests.isEmpty()) {
                        showEmptyState();
                    } else {
                        showResultsState();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    showEmptyState();
                });
            }
        }).start();
    }

    private void acceptFriendRequest(FriendRequest request, int position) {
        new Thread(() -> {
            try {
                boolean success = databaseHelper.acceptFriendRequest(request.getRequestId());
                
                runOnUiThread(() -> {
                    if (success) {
                        Toast.makeText(this, "✅ Đã chấp nhận lời mời từ " + request.getSenderName(), Toast.LENGTH_SHORT).show();
                        removeRequestFromList(position);
                    } else {
                        Toast.makeText(this, "❌ Không thể chấp nhận lời mời", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void declineFriendRequest(FriendRequest request, int position) {
        new Thread(() -> {
            try {
                boolean success = databaseHelper.declineFriendRequest(request.getRequestId());
                
                runOnUiThread(() -> {
                    if (success) {
                        Toast.makeText(this, "❌ Đã từ chối lời mời từ " + request.getSenderName(), Toast.LENGTH_SHORT).show();
                        removeRequestFromList(position);
                    } else {
                        Toast.makeText(this, "❌ Không thể từ chối lời mời", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void removeRequestFromList(int position) {
        if (position >= 0 && position < friendRequests.size()) {
            friendRequests.remove(position);
            adapter.notifyItemRemoved(position);
            adapter.notifyItemRangeChanged(position, friendRequests.size());
            
            if (friendRequests.isEmpty()) {
                showEmptyState();
            }
        }
    }

    private void showEmptyState() {
        emptyStateContainer.setVisibility(View.VISIBLE);
        loadingContainer.setVisibility(View.GONE);
        friendRequestsRecyclerView.setVisibility(View.GONE);
    }

    private void showLoadingState() {
        emptyStateContainer.setVisibility(View.GONE);
        loadingContainer.setVisibility(View.VISIBLE);
        friendRequestsRecyclerView.setVisibility(View.GONE);
    }

    private void showResultsState() {
        emptyStateContainer.setVisibility(View.GONE);
        loadingContainer.setVisibility(View.GONE);
        friendRequestsRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
