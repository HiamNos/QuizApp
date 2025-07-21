package com.example.quizme;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import com.bumptech.glide.Glide;
import de.hdodenhof.circleimageview.CircleImageView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UserProfileModal {
    
    public interface OnProfileActionListener {
        void onSendMessage(Friend friend);
    }
    
    public static void showUserProfile(Context context, Friend friend, int currentUserId, OnProfileActionListener listener) {
        Dialog dialog = new Dialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.modal_user_profile, null);
        dialog.setContentView(view);
        
        // Make dialog fullscreen with rounded corners
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(
            (int)(context.getResources().getDisplayMetrics().widthPixels * 0.9),
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        );
        
        // Find views
        CircleImageView profileAvatarImageView = view.findViewById(R.id.profileAvatarImageView);
        TextView profileNameTextView = view.findViewById(R.id.profileNameTextView);
        TextView profileEmailTextView = view.findViewById(R.id.profileEmailTextView);
        TextView profileCoinsTextView = view.findViewById(R.id.profileCoinsTextView);
        TextView profileFriendsCountTextView = view.findViewById(R.id.profileFriendsCountTextView);
        TextView profileJoinDateTextView = view.findViewById(R.id.profileJoinDateTextView);
        TextView profilePhoneTextView = view.findViewById(R.id.profilePhoneTextView);
        ImageView closeButton = view.findViewById(R.id.closeButton);
        Button sendMessageButton = view.findViewById(R.id.sendMessageButton);
        
        // Load user data
        loadUserProfile(context, friend, currentUserId, profileAvatarImageView, profileNameTextView, 
                       profileEmailTextView, profileCoinsTextView, profileFriendsCountTextView,
                       profileJoinDateTextView, profilePhoneTextView);
        
        // Set click listeners
        closeButton.setOnClickListener(v -> dialog.dismiss());
        
        sendMessageButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSendMessage(friend);
            }
            dialog.dismiss();
        });
        
        dialog.show();
    }
    
    private static int determineFriendUserId(Friend friend, int currentUserId) {
        // Determine which user ID is the actual friend (not the current user)
        if (friend.getUser1Id() == currentUserId) {
            return friend.getUser2Id(); // Current user is user1, so friend is user2
        } else {
            return friend.getUser1Id(); // Current user is user2, so friend is user1
        }
    }
    
    private static void loadUserProfile(Context context, Friend friend, int currentUserId,
                                      CircleImageView avatarImageView, TextView nameTextView,
                                      TextView emailTextView, TextView coinsTextView,
                                      TextView friendsCountTextView, TextView joinDateTextView,
                                      TextView phoneTextView) {        // Load basic info
        nameTextView.setText(friend.getFriendName());
        emailTextView.setText(friend.getFriendEmail());
        
        // Load avatar
        loadUserAvatar(context, friend.getFriendAvatar(), avatarImageView);
        
        // Load additional user details from database
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        
        // Get the actual friend's user ID
        int friendUserId = determineFriendUserId(friend, currentUserId);
        
        User userDetails = dbHelper.getUserById(friendUserId);
        
        if (userDetails != null) {
            // Display friend's coins
            coinsTextView.setText(String.valueOf(userDetails.getCoins()));
            
            // Display friend's phone
            String phone = userDetails.getPhone();
            if (phone != null && !phone.isEmpty()) {
                phoneTextView.setText("📱 Số điện thoại: " + phone);
            } else {
                phoneTextView.setText("📱 Số điện thoại: Chưa cập nhật");
            }
        } else {
            // Fallback to friend's coins if user details not found
            coinsTextView.setText(String.valueOf(friend.getFriendCoins()));
            phoneTextView.setText("📱 Số điện thoại: Chưa cập nhật");
        }
        
        // Get friend's friends count
        int friendsCount = dbHelper.getFriendsCount(friendUserId);
        friendsCountTextView.setText(String.valueOf(friendsCount));
        
        // Format join date (using friendship date as approximation)
        if (friend.getFriendshipDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy", Locale.getDefault());
            joinDateTextView.setText(sdf.format(friend.getFriendshipDate()));
        } else {
            joinDateTextView.setText("2024");
        }
    }
    
    private static void loadUserAvatar(Context context, String imagePath, CircleImageView avatarImageView) {
        if (imagePath != null && !imagePath.isEmpty()) {
            // Check if it's a drawable resource name (no "/" or "\")
            if (!imagePath.contains("/") && !imagePath.contains("\\")) {
                // Load from drawable resource
                try {
                    int resourceId = context.getResources().getIdentifier(
                        imagePath, "drawable", context.getPackageName());
                    if (resourceId != 0) {
                        Glide.with(context)
                                .load(resourceId)
                                .placeholder(R.drawable.avatar)
                                .error(R.drawable.avatar)
                                .into(avatarImageView);
                    } else {
                        avatarImageView.setImageResource(R.drawable.avatar);
                    }
                } catch (Exception e) {
                    avatarImageView.setImageResource(R.drawable.avatar);
                }
            } else {
                // Load from file path
                java.io.File imageFile = new java.io.File(imagePath);
                if (imageFile.exists()) {
                    Glide.with(context)
                            .load(imageFile)
                            .placeholder(R.drawable.avatar)
                            .error(R.drawable.avatar)
                            .into(avatarImageView);
                } else {
                    avatarImageView.setImageResource(R.drawable.avatar);
                }
            }
        } else {
            avatarImageView.setImageResource(R.drawable.avatar);
        }
    }
}
