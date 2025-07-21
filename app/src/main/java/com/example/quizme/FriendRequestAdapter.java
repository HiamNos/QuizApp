package com.example.quizme;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.RequestViewHolder> {

    private List<FriendRequest> requests;
    private OnRequestActionListener listener;

    public interface OnRequestActionListener {
        void onAcceptRequest(FriendRequest request, int position);
        void onDeclineRequest(FriendRequest request, int position);
    }

    public FriendRequestAdapter(List<FriendRequest> requests, OnRequestActionListener listener) {
        this.requests = requests;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        FriendRequest request = requests.get(position);
        holder.bind(request, position, listener);
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public void updateRequests(List<FriendRequest> newRequests) {
        this.requests.clear();
        this.requests.addAll(newRequests);
        notifyDataSetChanged();
    }

    public void removeRequest(int position) {
        if (position >= 0 && position < requests.size()) {
            requests.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, requests.size());
        }
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatarImageView;
        private TextView nameTextView;
        private TextView emailTextView;
        private Button acceptButton;
        private Button declineButton;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.avatarImageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            declineButton = itemView.findViewById(R.id.declineButton);
        }

        public void bind(FriendRequest request, int position, OnRequestActionListener listener) {
            // Set sender info
            nameTextView.setText(request.getSenderName());
            emailTextView.setText(request.getSenderEmail());

            // Load sender avatar properly
            loadSenderAvatar(request);

            // Set button listeners
            acceptButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAcceptRequest(request, position);
                }
            });

            declineButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeclineRequest(request, position);
                }
            });
        }

        private void loadSenderAvatar(FriendRequest request) {
            String imagePath = request.getSenderAvatar();
            
            if (imagePath != null && !imagePath.isEmpty()) {
                // Kiểm tra nếu là drawable resource name (không chứa "/")
                if (!imagePath.contains("/") && !imagePath.contains("\\")) {
                    // Load từ drawable resource
                    try {
                        int resourceId = itemView.getContext().getResources().getIdentifier(
                            imagePath, "drawable", itemView.getContext().getPackageName());
                        if (resourceId != 0) {
                            Glide.with(itemView.getContext())
                                    .load(resourceId)
                                    .placeholder(R.drawable.avatar)
                                    .error(R.drawable.avatar)
                                    .into(avatarImageView);
                        } else {
                            // Resource không tồn tại, dùng default
                            avatarImageView.setImageResource(R.drawable.avatar);
                        }
                    } catch (Exception e) {
                        avatarImageView.setImageResource(R.drawable.avatar);
                    }
                } else {
                    // Load từ file path
                    java.io.File imageFile = new java.io.File(imagePath);
                    if (imageFile.exists()) {
                        // Load sender's custom avatar
                        Glide.with(itemView.getContext())
                                .load(imageFile)
                                .placeholder(R.drawable.avatar)
                                .error(R.drawable.avatar)
                                .into(avatarImageView);
                    } else {
                        // Use default avatar if file doesn't exist
                        avatarImageView.setImageResource(R.drawable.avatar);
                    }
                }
            } else {
                // Use default avatar if no image path
                avatarImageView.setImageResource(R.drawable.avatar);
            }
        }

        private String formatRequestDate(String dateString) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date = inputFormat.parse(dateString);
                
                if (date != null) {
                    long now = System.currentTimeMillis();
                    long requestTime = date.getTime();
                    
                    // Use DateUtils for relative time
                    return DateUtils.getRelativeTimeSpanString(
                            requestTime,
                            now,
                            DateUtils.MINUTE_IN_MILLIS
                    ).toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "Vừa xong";
        }
    }
}
