package com.example.quizme;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

import java.io.File;
import java.util.List;

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.FriendViewHolder> {

    private List<Friend> friends;
    private OnFriendActionListener listener;

    public interface OnFriendActionListener {
        void onChatClick(Friend friend);
        void onViewProfileClick(Friend friend);
    }

    public FriendsListAdapter(List<Friend> friends, OnFriendActionListener listener) {
        this.friends = friends;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        Friend friend = friends.get(position);
        holder.bind(friend, listener);
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView avatarImageView;
        private TextView friendNameTextView;
        private TextView friendEmailTextView;
        private TextView friendScoreTextView;
        private Button chatButton;
        private Button profileButton;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.avatarImageView);
            friendNameTextView = itemView.findViewById(R.id.nameTextView);
            friendEmailTextView = itemView.findViewById(R.id.emailTextView);
            friendScoreTextView = itemView.findViewById(R.id.friendSinceTextView);
            chatButton = itemView.findViewById(R.id.chatButton);
            profileButton = itemView.findViewById(R.id.viewProfileButton);
        }

        public void bind(Friend friend, OnFriendActionListener listener) {
            // Set friend information
            friendNameTextView.setText(friend.getFriendName());
            friendEmailTextView.setText(friend.getFriendEmail());
            
            // Format friendSince display instead of score
            if (friend.getFriendshipDate() != null) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
                friendScoreTextView.setText("Bạn bè từ: " + sdf.format(friend.getFriendshipDate()));
                friendScoreTextView.setVisibility(View.VISIBLE);
            } else {
                friendScoreTextView.setText("Coins: " + friend.getFriendCoins());
                friendScoreTextView.setVisibility(View.VISIBLE);
            }

            // Load friend avatar với Glide
            loadFriendAvatar(friend);

            // Set button click listeners
            chatButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onChatClick(friend);
                }
            });

            profileButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewProfileClick(friend);
                }
            });
        }

        private void loadFriendAvatar(Friend friend) {
            String imagePath = friend.getFriendAvatar();
            
            System.out.println("FriendsListAdapter: Loading avatar for " + friend.getFriendName() + " with path: " + imagePath);
            
            if (imagePath != null && !imagePath.isEmpty()) {
                // Kiểm tra nếu là drawable resource name (không chứa "/")
                if (!imagePath.contains("/") && !imagePath.contains("\\")) {
                    // Load từ drawable resource
                    try {
                        int resourceId = itemView.getContext().getResources().getIdentifier(
                            imagePath, "drawable", itemView.getContext().getPackageName());
                        System.out.println("FriendsListAdapter: Resource ID for " + imagePath + " is " + resourceId);
                        if (resourceId != 0) {
                            Glide.with(itemView.getContext())
                                    .load(resourceId)
                                    .placeholder(R.drawable.avatar)
                                    .error(R.drawable.avatar)
                                    .into(avatarImageView);
                            System.out.println("FriendsListAdapter: Loaded drawable resource: " + imagePath);
                        } else {
                            // Resource không tồn tại, dùng default
                            avatarImageView.setImageResource(R.drawable.avatar);
                            System.out.println("FriendsListAdapter: Resource not found, using default avatar");
                        }
                    } catch (Exception e) {
                        avatarImageView.setImageResource(R.drawable.avatar);
                        System.out.println("FriendsListAdapter: Error loading drawable: " + e.getMessage());
                    }
                } else {
                    // Load từ file path (existing logic)
                    File imageFile = new File(imagePath);
                    if (imageFile.exists()) {
                        // Load friend's custom avatar
                        Glide.with(itemView.getContext())
                                .load(imageFile)
                                .placeholder(R.drawable.avatar)
                                .error(R.drawable.avatar)
                                .into(avatarImageView);
                        System.out.println("FriendsListAdapter: Loaded file avatar: " + imagePath);
                    } else {
                        // Use default avatar if file doesn't exist
                        avatarImageView.setImageResource(R.drawable.avatar);
                        System.out.println("FriendsListAdapter: File not found, using default avatar");
                    }
                }
            } else {
                // Use default avatar if no image path
                avatarImageView.setImageResource(R.drawable.avatar);
                System.out.println("FriendsListAdapter: No image path, using default avatar");
            }
        }
    }
}
