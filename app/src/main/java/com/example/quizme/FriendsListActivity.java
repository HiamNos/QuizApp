package com.example.quizme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import de.hdodenhof.circleimageview.CircleImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FriendsListActivity extends AppCompatActivity {

    private RecyclerView friendsRecyclerView;
    private ImageView backBtn, searchFriendsBtn;
    private CircleImageView currentUserAvatar;
    private LinearLayout emptyStateContainer, loadingContainer;
    private LinearLayout friendsTab, searchTab, requestsTab;
    private Button findFriendsBtn;
    private DatabaseHelper databaseHelper;
    private FriendsListAdapter adapter;
    private List<Friend> friends;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        // Initialize views
        friendsRecyclerView = findViewById(R.id.friendsRecyclerView);
        backBtn = findViewById(R.id.backBtn);
        searchFriendsBtn = findViewById(R.id.searchFriendsBtn);
        currentUserAvatar = findViewById(R.id.currentUserAvatar);
        emptyStateContainer = findViewById(R.id.emptyStateContainer);
        loadingContainer = findViewById(R.id.loadingContainer);
        findFriendsBtn = findViewById(R.id.findFriendsBtn);
        
        // Bottom tab bar
        friendsTab = findViewById(R.id.friendsTab);
        searchTab = findViewById(R.id.searchTab);
        requestsTab = findViewById(R.id.requestsTab);

        databaseHelper = new DatabaseHelper(this);
        friends = new ArrayList<>();

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
        loadCurrentUserAvatar();
        loadFriends();
        updateBottomTabState();
    }
    
    private void updateBottomTabState() {
        // Set Friends tab as selected (since this is FriendsListActivity)
        // Friends tab - selected
        friendsTab.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
        friendsTab.setAlpha(0.1f);
        
        // Other tabs - unselected  
        searchTab.setBackgroundColor(getResources().getColor(android.R.color.transparent, null));
        searchTab.setAlpha(1.0f);
        requestsTab.setBackgroundColor(getResources().getColor(android.R.color.transparent, null));
        requestsTab.setAlpha(1.0f);
    }

    private void setupUI() {
        // Back button
        backBtn.setOnClickListener(v -> finish());

        // Search Friends button
        searchFriendsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, SearchFriendsActivity.class);
            startActivity(intent);
        });

        // Find Friends button in empty state
        findFriendsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, SearchFriendsActivity.class);
            startActivity(intent);
        });
        
        // Bottom tab bar click listeners
        friendsTab.setOnClickListener(v -> {
            // Already on friends tab - do nothing or refresh
            loadFriends();
        });
        
        searchTab.setOnClickListener(v -> {
            Intent intent = new Intent(this, SearchFriendsActivity.class);
            startActivity(intent);
        });
        
        requestsTab.setOnClickListener(v -> {
            Intent intent = new Intent(this, FriendRequestsActivity.class);
            startActivity(intent);
        });
    }

    private void loadCurrentUserAvatar() {
        new Thread(() -> {
            try {
                User currentUser = databaseHelper.getUserById(currentUserId);
                if (currentUser != null) {
                    runOnUiThread(() -> {
                        String imagePath = currentUser.getAvatarImage();
                        
                        if (imagePath != null && !imagePath.isEmpty()) {
                            // Kiểm tra nếu là drawable resource name (không chứa "/")
                            if (!imagePath.contains("/") && !imagePath.contains("\\")) {
                                // Load từ drawable resource
                                try {
                                    int resourceId = getResources().getIdentifier(
                                        imagePath, "drawable", getPackageName());
                                    if (resourceId != 0) {
                                        Glide.with(this)
                                                .load(resourceId)
                                                .placeholder(R.drawable.avatar)
                                                .error(R.drawable.avatar)
                                                .into(currentUserAvatar);
                                    } else {
                                        // Resource không tồn tại, dùng default
                                        currentUserAvatar.setImageResource(R.drawable.avatar);
                                    }
                                } catch (Exception e) {
                                    currentUserAvatar.setImageResource(R.drawable.avatar);
                                }
                            } else {
                                // Load từ file path (existing logic)
                                File imageFile = new File(imagePath);
                                if (imageFile.exists()) {
                                    // Load user's custom avatar
                                    Glide.with(this)
                                            .load(imageFile)
                                            .placeholder(R.drawable.avatar)
                                            .error(R.drawable.avatar)
                                            .into(currentUserAvatar);
                                } else {
                                    // Use default avatar if file doesn't exist
                                    currentUserAvatar.setImageResource(R.drawable.avatar);
                                }
                            }
                        } else {
                            // Use default avatar if no image path
                            currentUserAvatar.setImageResource(R.drawable.avatar);
                        }
                    });
                }
            } catch (Exception e) {
                runOnUiThread(() -> {
                    currentUserAvatar.setImageResource(R.drawable.avatar);
                });
            }
        }).start();
    }

    private void setupRecyclerView() {
        adapter = new FriendsListAdapter(friends, new FriendsListAdapter.OnFriendActionListener() {
            @Override
            public void onChatClick(Friend friend) {
                // TODO: Mở ChatActivity
                Toast.makeText(FriendsListActivity.this, "Chat với " + friend.getFriendName() + " (Coming soon)", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onViewProfileClick(Friend friend) {
                // TODO: Mở ProfileActivity
                Toast.makeText(FriendsListActivity.this, "Xem profile " + friend.getFriendName() + " (Coming soon)", Toast.LENGTH_SHORT).show();
            }
        });

        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        friendsRecyclerView.setAdapter(adapter);
    }

    private void loadFriends() {
        showLoadingState();

        new Thread(() -> {
            try {
                List<Friend> friendsList = databaseHelper.getFriends(currentUserId);
                
                runOnUiThread(() -> {
                    friends.clear();
                    friends.addAll(friendsList);
                    adapter.notifyDataSetChanged();
                    
                    if (friendsList.isEmpty()) {
                        showEmptyState();
                    } else {
                        showResultsState();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi tải danh sách bạn bè: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    showEmptyState();
                });
            }
        }).start();
    }

    private void showEmptyState() {
        emptyStateContainer.setVisibility(View.VISIBLE);
        loadingContainer.setVisibility(View.GONE);
        friendsRecyclerView.setVisibility(View.GONE);
    }

    private void showLoadingState() {
        emptyStateContainer.setVisibility(View.GONE);
        loadingContainer.setVisibility(View.VISIBLE);
        friendsRecyclerView.setVisibility(View.GONE);
    }

    private void showResultsState() {
        emptyStateContainer.setVisibility(View.GONE);
        loadingContainer.setVisibility(View.GONE);
        friendsRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload friends when returning from other activities
        loadFriends();
    }
}
