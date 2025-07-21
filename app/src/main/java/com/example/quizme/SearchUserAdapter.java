package com.example.quizme;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.UserViewHolder> {

    private List<User> users;
    private OnUserClickListener listener;
    private DatabaseHelper databaseHelper;
    private int currentUserId;
    private java.util.Set<Integer> loadingPositions = new java.util.HashSet<>();

    public interface OnUserClickListener {
        void onAddFriendClick(User user, int position);
    }

    public SearchUserAdapter(List<User> users, OnUserClickListener listener, DatabaseHelper databaseHelper, int currentUserId) {
        this.users = users;
        this.listener = listener;
        this.databaseHelper = databaseHelper;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_user_simple, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user, position, listener, databaseHelper, currentUserId, loadingPositions);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void markAsFriendRequestSent(int position) {
        if (position >= 0 && position < users.size()) {
            // Remove from loading state
            loadingPositions.remove(position);
            // Delay để user có thể thấy processing state
            new android.os.Handler().postDelayed(() -> {
                notifyItemChanged(position);
            }, 1000); // 1 giây delay
        }
    }

    public void showLoadingState(int position) {
        if (position >= 0 && position < users.size()) {
            loadingPositions.add(position);
            notifyItemChanged(position);
        }
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatarImageView;
        private TextView nameTextView;
        private TextView emailTextView;
        private TextView coinsTextView;
        private Button addFriendButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.avatarImageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
            coinsTextView = itemView.findViewById(R.id.userCoins);
            addFriendButton = itemView.findViewById(R.id.addFriendBtn);
        }

        public void bind(User user, int position, OnUserClickListener listener, DatabaseHelper databaseHelper, int currentUserId, java.util.Set<Integer> loadingPositions) {
            // Set user info
            nameTextView.setText(user.getName());
            emailTextView.setText(user.getEmail());
            coinsTextView.setText(user.getCoins() + " coins");

            // Load user avatar với Glide
            loadUserAvatar(user);

            // Check if this position is in loading state
            if (loadingPositions.contains(position)) {
                addFriendButton.setText("⏳ Đang gửi...");
                addFriendButton.setEnabled(false);
                addFriendButton.setBackgroundColor(
                    itemView.getContext().getResources().getColor(android.R.color.darker_gray)
                );
                addFriendButton.setTextColor(
                    itemView.getContext().getResources().getColor(android.R.color.white)
                );
                return;
            }

            // Check friendship status
            String friendshipStatus = databaseHelper.getFriendshipStatus(currentUserId, user.getUserId());
            System.out.println("SearchUserAdapter: Friendship status for " + user.getName() + ": " + friendshipStatus);
            
            // Update button based on status
            switch (friendshipStatus) {
                case "FRIENDS":
                    addFriendButton.setText("👥 Bạn bè");
                    addFriendButton.setEnabled(false);
                    addFriendButton.setBackgroundColor(
                        itemView.getContext().getResources().getColor(R.color.colorPrimary)
                    );
                    addFriendButton.setTextColor(
                        itemView.getContext().getResources().getColor(android.R.color.white)
                    );
                    break;
                    
                case "REQUEST_SENT":
                    addFriendButton.setText("✓ Đã gửi");
                    addFriendButton.setEnabled(false);
                    addFriendButton.setBackgroundColor(
                        itemView.getContext().getResources().getColor(android.R.color.darker_gray)
                    );
                    addFriendButton.setTextColor(
                        itemView.getContext().getResources().getColor(android.R.color.white)
                    );
                    break;
                    
                case "REQUEST_RECEIVED":
                    addFriendButton.setText("📨 Nhận mời");
                    addFriendButton.setEnabled(true);
                    addFriendButton.setBackgroundColor(
                        itemView.getContext().getResources().getColor(android.R.color.darker_gray)
                    );
                    addFriendButton.setTextColor(
                        itemView.getContext().getResources().getColor(android.R.color.white)
                    );
                    addFriendButton.setOnClickListener(v -> {
                        // Navigate to friend requests to accept
                        if (listener != null) {
                            listener.onAddFriendClick(user, position);
                        }
                    });
                    break;
                    
                default: // "NONE"
                    addFriendButton.setText("+ Kết bạn");
                    addFriendButton.setEnabled(true);
                    addFriendButton.setBackgroundColor(
                        itemView.getContext().getResources().getColor(R.color.primary_color)
                    );
                    addFriendButton.setTextColor(
                        itemView.getContext().getResources().getColor(android.R.color.white)
                    );
                    addFriendButton.setOnClickListener(v -> {
                        if (listener != null) {
                            listener.onAddFriendClick(user, position);
                        }
                    });
                    break;
            }
        }

        private void loadUserAvatar(User user) {
            String imagePath = user.getAvatarImage();
            
            System.out.println("SearchUserAdapter: Loading avatar for " + user.getName() + " with path: " + imagePath);
            
            if (imagePath != null && !imagePath.isEmpty()) {
                // Kiểm tra nếu là drawable resource name (không chứa "/")
                if (!imagePath.contains("/") && !imagePath.contains("\\")) {
                    // Load từ drawable resource
                    try {
                        int resourceId = itemView.getContext().getResources().getIdentifier(
                            imagePath, "drawable", itemView.getContext().getPackageName());
                        System.out.println("SearchUserAdapter: Resource ID for " + imagePath + " is " + resourceId);
                        if (resourceId != 0) {
                            Glide.with(itemView.getContext())
                                    .load(resourceId)
                                    .placeholder(R.drawable.avatar)
                                    .error(R.drawable.avatar)
                                    .into(avatarImageView);
                            System.out.println("SearchUserAdapter: Loaded drawable resource: " + imagePath);
                        } else {
                            // Resource không tồn tại, dùng default
                            avatarImageView.setImageResource(R.drawable.avatar);
                            System.out.println("SearchUserAdapter: Resource not found, using default avatar");
                        }
                    } catch (Exception e) {
                        avatarImageView.setImageResource(R.drawable.avatar);
                        System.out.println("SearchUserAdapter: Error loading drawable: " + e.getMessage());
                    }
                } else {
                    // Load từ file path (existing logic)
                    File imageFile = new File(imagePath);
                    if (imageFile.exists()) {
                        // Load user's custom avatar
                        Glide.with(itemView.getContext())
                                .load(imageFile)
                                .placeholder(R.drawable.avatar)
                                .error(R.drawable.avatar)
                                .into(avatarImageView);
                        System.out.println("SearchUserAdapter: Loaded file avatar: " + imagePath);
                    } else {
                        // Use default avatar if file doesn't exist
                        avatarImageView.setImageResource(R.drawable.avatar);
                        System.out.println("SearchUserAdapter: File not found, using default avatar");
                    }
                }
            } else {
                // Use default avatar if no image path
                avatarImageView.setImageResource(R.drawable.avatar);
                System.out.println("SearchUserAdapter: No image path, using default avatar");
            }
        }
    }
}
