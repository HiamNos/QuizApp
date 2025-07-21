package com.example.quizme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    
    private RecyclerView messagesRecyclerView;
    private MessageAdapter messageAdapter;
    private EditText messageEditText;
    private ImageButton sendButton;
    private ImageView backButton;
    private CircleImageView friendAvatarImageView;
    private TextView friendNameTextView;
    private TextView onlineStatusTextView;
    
    private DatabaseHelper databaseHelper;
    private int currentUserId;
    private int friendUserId;
    private String friendName;
    private String friendAvatar;
    private List<Message> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        
        System.out.println("ChatActivity: Starting onCreate");

        // Initialize database
        databaseHelper = new DatabaseHelper(this);
        
        // Get current user ID - fixed to use consistent SharedPreferences name
        SharedPreferences sharedPreferences = getSharedPreferences("QuizApp", MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt("user_id", -1);
        
        System.out.println("ChatActivity: Current user ID from prefs: " + currentUserId);
        
        if (currentUserId == -1) {
            System.out.println("ChatActivity: Error - No user ID found in SharedPreferences");
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin user", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Get friend info from intent
        Intent intent = getIntent();
        friendUserId = intent.getIntExtra("friend_user_id", -1);
        friendName = intent.getStringExtra("friend_name");
        friendAvatar = intent.getStringExtra("friend_avatar");

        System.out.println("ChatActivity: Friend user ID from intent: " + friendUserId);
        System.out.println("ChatActivity: Friend name from intent: " + friendName);
        System.out.println("ChatActivity: Friend avatar from intent: " + friendAvatar);

        if (friendUserId == -1 || friendName == null) {
            System.out.println("ChatActivity: Error - Invalid friend info");
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin bạn bè", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        System.out.println("ChatActivity: Initializing views and loading data");
        initializeViews();
        setupRecyclerView();
        loadMessages();
        setupClickListeners();
        
        System.out.println("ChatActivity: onCreate completed successfully");
    }

    private void initializeViews() {
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        backButton = findViewById(R.id.backButton);
        friendAvatarImageView = findViewById(R.id.friendAvatarImageView);
        friendNameTextView = findViewById(R.id.friendNameTextView);
        onlineStatusTextView = findViewById(R.id.onlineStatusTextView);

        // Set friend info in header
        friendNameTextView.setText(friendName);
        onlineStatusTextView.setText("Đang hoạt động"); // Placeholder for now
        
        // Load friend avatar
        loadFriendAvatar();
    }

    private void setupRecyclerView() {
        messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(messages, currentUserId);
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // Start from bottom
        messagesRecyclerView.setLayoutManager(layoutManager);
        messagesRecyclerView.setAdapter(messageAdapter);
    }

    private void loadMessages() {
        new Thread(() -> {
            try {
                System.out.println("ChatActivity: Loading messages between " + currentUserId + " and " + friendUserId);
                List<Message> loadedMessages = databaseHelper.getMessagesBetweenUsers(currentUserId, friendUserId);
                System.out.println("ChatActivity: Loaded " + loadedMessages.size() + " messages");
                
                runOnUiThread(() -> {
                    messageAdapter.updateMessages(loadedMessages);
                    if (!loadedMessages.isEmpty()) {
                        messagesRecyclerView.scrollToPosition(loadedMessages.size() - 1);
                    }
                });
            } catch (Exception e) {
                System.out.println("ChatActivity: Error loading messages: " + e.getMessage());
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi tải tin nhắn: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
        
        sendButton.setOnClickListener(v -> sendMessage());
        
        messageEditText.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == android.view.KeyEvent.KEYCODE_ENTER && 
                event.getAction() == android.view.KeyEvent.ACTION_DOWN) {
                sendMessage();
                return true;
            }
            return false;
        });
    }

    private void sendMessage() {
        String messageContent = messageEditText.getText().toString().trim();
        
        if (messageContent.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tin nhắn", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                System.out.println("ChatActivity: Sending message from " + currentUserId + " to " + friendUserId + ": " + messageContent);
                long messageId = databaseHelper.sendMessage(currentUserId, friendUserId, messageContent);
                System.out.println("ChatActivity: Message ID returned: " + messageId);
                
                if (messageId > 0) {
                    runOnUiThread(() -> {
                        messageEditText.setText("");
                        Toast.makeText(this, "Đã gửi tin nhắn", Toast.LENGTH_SHORT).show();
                        loadMessages(); // Reload to show new message
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Gửi tin nhắn thất bại", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                System.out.println("ChatActivity: Error sending message: " + e.getMessage());
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi gửi tin nhắn: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void loadFriendAvatar() {
        if (friendAvatar != null && !friendAvatar.isEmpty()) {
            // Check if it's a drawable resource name
            if (!friendAvatar.contains("/") && !friendAvatar.contains("\\")) {
                try {
                    int resourceId = getResources().getIdentifier(
                        friendAvatar, "drawable", getPackageName());
                    if (resourceId != 0) {
                        Glide.with(this)
                                .load(resourceId)
                                .placeholder(R.drawable.avatar)
                                .error(R.drawable.avatar)
                                .into(friendAvatarImageView);
                    } else {
                        friendAvatarImageView.setImageResource(R.drawable.avatar);
                    }
                } catch (Exception e) {
                    friendAvatarImageView.setImageResource(R.drawable.avatar);
                }
            } else {
                // Load from file path
                java.io.File imageFile = new java.io.File(friendAvatar);
                if (imageFile.exists()) {
                    Glide.with(this)
                            .load(imageFile)
                            .placeholder(R.drawable.avatar)
                            .error(R.drawable.avatar)
                            .into(friendAvatarImageView);
                } else {
                    friendAvatarImageView.setImageResource(R.drawable.avatar);
                }
            }
        } else {
            friendAvatarImageView.setImageResource(R.drawable.avatar);
        }
    }
}
