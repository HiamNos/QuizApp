package com.example.quizme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ChatListActivity extends AppCompatActivity {
    
    private RecyclerView chatsRecyclerView;
    private LinearLayout emptyStateLayout;
    private ImageView backButton;
    private ImageView newChatButton;
    
    private DatabaseHelper databaseHelper;
    private int currentUserId;
    private List<Friend> chatsData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        
        System.out.println("ChatListActivity: Starting onCreate");

        // Initialize database
        databaseHelper = new DatabaseHelper(this);
        
        // Get current user ID - fixed to use consistent SharedPreferences name
        SharedPreferences sharedPreferences = getSharedPreferences("QuizApp", MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt("user_id", -1);
        
        System.out.println("ChatListActivity: Current user ID: " + currentUserId);

        if (currentUserId == -1) {
            System.out.println("ChatListActivity: Error - No user ID found");
            finish();
            return;
        }

        initializeViews();
        loadChats();
        setupClickListeners();
        
        System.out.println("ChatListActivity: onCreate completed");
    }

    private void initializeViews() {
        chatsRecyclerView = findViewById(R.id.chatsRecyclerView);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        backButton = findViewById(R.id.backButton);
        newChatButton = findViewById(R.id.newChatButton);
        
        chatsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadChats() {
        System.out.println("ChatListActivity: Loading chats for user ID: " + currentUserId);
        // Load friends who have exchanged messages with current user
        new Thread(() -> {
            try {
                // Get all friends first
                List<Friend> allFriends = databaseHelper.getFriends(currentUserId);
                List<Friend> friendsWithMessages = new ArrayList<>();
                
                System.out.println("ChatListActivity: Found " + allFriends.size() + " total friends");
                
                // Filter friends who have messages
                for (Friend friend : allFriends) {
                    int friendUserId = determineFriendUserId(friend);
                    List<Message> messages = databaseHelper.getMessagesBetweenUsers(currentUserId, friendUserId);
                    
                    if (!messages.isEmpty()) {
                        friendsWithMessages.add(friend);
                        System.out.println("ChatListActivity: Friend " + friend.getFriendName() + " has " + messages.size() + " messages");
                    }
                }
                
                System.out.println("ChatListActivity: Found " + friendsWithMessages.size() + " friends with messages");
                
                runOnUiThread(() -> {
                    chatsData = friendsWithMessages;
                    
                    if (friendsWithMessages.isEmpty()) {
                        System.out.println("ChatListActivity: Showing empty state");
                        showEmptyState();
                    } else {
                        System.out.println("ChatListActivity: Showing chats list");
                        showChatsList(friendsWithMessages);
                    }
                });
            } catch (Exception e) {
                System.out.println("ChatListActivity: Error loading chats: " + e.getMessage());
                e.printStackTrace();
                runOnUiThread(() -> {
                    showEmptyState();
                });
            }
        }).start();
    }

    private void showEmptyState() {
        System.out.println("ChatListActivity: Showing empty state - no chats found");
        chatsRecyclerView.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.VISIBLE);
    }

    private void showChatsList(List<Friend> friends) {
        System.out.println("ChatListActivity: Showing chats list with " + friends.size() + " friends");
        
        // Ensure correct visibility
        emptyStateLayout.setVisibility(View.GONE);
        chatsRecyclerView.setVisibility(View.VISIBLE);
        
        // Use FriendsListAdapter for simplicity (can create a specialized ChatListAdapter later)
        FriendsListAdapter adapter = new FriendsListAdapter(friends, new FriendsListAdapter.OnFriendActionListener() {
            @Override
            public void onChatClick(Friend friend) {
                System.out.println("ChatListActivity: Opening chat with " + friend.getFriendName());
                openChatWithFriend(friend);
            }

            @Override
            public void onViewProfileClick(Friend friend) {
                // Handle profile view if needed
            }
        });
        
        chatsRecyclerView.setAdapter(adapter);
        
        // Force layout refresh
        chatsRecyclerView.post(() -> {
            adapter.notifyDataSetChanged();
            System.out.println("ChatListActivity: Adapter data refreshed");
        });
        
        System.out.println("ChatListActivity: Chat list adapter set successfully");
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
        
        newChatButton.setOnClickListener(v -> {
            // Open friends list to start new chat
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("open_friends_tab", true);
            startActivity(intent);
            finish();
        });
    }

    private void openChatWithFriend(Friend friend) {
        Intent intent = new Intent(this, ChatActivity.class);
        
        // Determine friend's actual user ID
        int friendUserId = determineFriendUserId(friend);
        
        intent.putExtra("friend_user_id", friendUserId);
        intent.putExtra("friend_name", friend.getFriendName());
        intent.putExtra("friend_avatar", friend.getFriendAvatar());
        
        startActivity(intent);
    }
    
    private int determineFriendUserId(Friend friend) {
        // Determine which user ID is the actual friend (not the current user)
        if (friend.getUser1Id() == currentUserId) {
            return friend.getUser2Id(); // Current user is user1, so friend is user2
        } else {
            return friend.getUser1Id(); // Current user is user2, so friend is user1
        }
    }
}
