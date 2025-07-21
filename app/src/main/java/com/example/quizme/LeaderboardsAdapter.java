package com.example.quizme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quizme.databinding.RowLeaderboardsBinding;

import java.io.File;
import java.util.ArrayList;

public class LeaderboardsAdapter extends RecyclerView.Adapter<LeaderboardsAdapter.LeaderboardViewHolder> {

    Context context;
    ArrayList<User> users;
    ArrayList<User> allUsers; // Để tính rank thật

    public LeaderboardsAdapter(Context context, ArrayList<User> users, ArrayList<User> allUsers) {
        this.context = context;
        this.users = users;
        this.allUsers = allUsers;
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_leaderboards, parent, false);
        return new LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        User user = users.get(position);

        holder.binding.userName.setText(user.getName());
        holder.binding.userEmail.setText(user.getEmail());
        holder.binding.coins.setText(String.valueOf(user.getCoins()));
        
        // Tính rank thật trong toàn bộ danh sách
        int realRank = getRealRank(user);
        holder.binding.index.setText(String.format("#%d", realRank));

        // Set rank badge based on real rank (not filtered position)
        switch (realRank - 1) { // Chuyển về index 0-based
            case 0: // 1st place - Gold
                holder.binding.rankBadge.setImageResource(R.drawable.rank_badge_gold);
                break;
            case 1: // 2nd place - Silver
                holder.binding.rankBadge.setImageResource(R.drawable.rank_badge_silver);
                break;
            case 2: // 3rd place - Bronze
                holder.binding.rankBadge.setImageResource(R.drawable.rank_badge_bronze);
                break;
            default: // Other positions - Default
                holder.binding.rankBadge.setImageResource(R.drawable.rank_badge);
                break;
        }

        // Load user avatar
        loadUserAvatar(holder, user);
    }

    private void loadUserAvatar(LeaderboardViewHolder holder, User user) {
        String imagePath = user.getAvatarImage();
        
        System.out.println("LeaderboardsAdapter: Loading avatar for user " + user.getName() + ", path: " + imagePath);
        
        if (imagePath != null && !imagePath.isEmpty()) {
            // Kiểm tra nếu là drawable resource name (không chứa "/")
            if (!imagePath.contains("/") && !imagePath.contains("\\")) {
                // Load từ drawable resource
                try {
                    int resourceId = context.getResources().getIdentifier(
                        imagePath, "drawable", context.getPackageName());
                    if (resourceId != 0) {
                        System.out.println("LeaderboardsAdapter: Loading drawable resource: " + imagePath);
                        Glide.with(context)
                                .load(resourceId)
                                .placeholder(R.drawable.avatar)
                                .error(R.drawable.avatar)
                                .into(holder.binding.userAvatar);
                    } else {
                        // Resource không tồn tại, dùng default
                        System.out.println("LeaderboardsAdapter: Drawable resource not found: " + imagePath);
                        holder.binding.userAvatar.setImageResource(R.drawable.avatar);
                    }
                } catch (Exception e) {
                    System.out.println("LeaderboardsAdapter: Error loading drawable resource: " + e.getMessage());
                    holder.binding.userAvatar.setImageResource(R.drawable.avatar);
                }
            } else {
                // Load từ file path (existing logic)
                File imageFile = new File(imagePath);
                System.out.println("LeaderboardsAdapter: File exists: " + imageFile.exists() + ", path: " + imageFile.getAbsolutePath());
                if (imageFile.exists()) {
                    // Load user's custom avatar
                    System.out.println("LeaderboardsAdapter: Loading custom avatar with Glide");
                    Glide.with(context)
                            .load(imageFile)
                            .placeholder(R.drawable.avatar)
                            .error(R.drawable.avatar)
                            .into(holder.binding.userAvatar);
                } else {
                    // Use default avatar if file doesn't exist
                    System.out.println("LeaderboardsAdapter: Using default avatar - file not found");
                    holder.binding.userAvatar.setImageResource(R.drawable.avatar);
                }
            }
        } else {
            // Use default avatar if no image path
            System.out.println("LeaderboardsAdapter: Using default avatar - no image path");
            holder.binding.userAvatar.setImageResource(R.drawable.avatar);
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
    
    private int getRealRank(User user) {
        // Tìm vị trí thật của user trong danh sách allUsers (đã sắp xếp theo coins)
        for (int i = 0; i < allUsers.size(); i++) {
            if (allUsers.get(i).getUserId() == user.getUserId()) {
                return i + 1; // Rank bắt đầu từ 1
            }
        }
        return 1; // Default fallback
    }

    public class LeaderboardViewHolder extends RecyclerView.ViewHolder {

        RowLeaderboardsBinding binding;
        public LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RowLeaderboardsBinding.bind(itemView);
        }
    }
}
