package com.example.quizme;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import de.hdodenhof.circleimageview.CircleImageView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    
    private List<Message> messages;
    private int currentUserId;

    public MessageAdapter(List<Message> messages, int currentUserId) {
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.bind(message, currentUserId);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void updateMessages(List<Message> newMessages) {
        this.messages = newMessages;
        notifyDataSetChanged();
    }

    public void addMessage(Message message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout sentMessageContainer;
        private LinearLayout receivedMessageContainer;
        private TextView sentMessageText;
        private TextView sentMessageTime;
        private TextView receivedMessageText;
        private TextView receivedMessageTime;
        private CircleImageView senderAvatarImageView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            sentMessageContainer = itemView.findViewById(R.id.sentMessageContainer);
            receivedMessageContainer = itemView.findViewById(R.id.receivedMessageContainer);
            sentMessageText = itemView.findViewById(R.id.sentMessageText);
            sentMessageTime = itemView.findViewById(R.id.sentMessageTime);
            receivedMessageText = itemView.findViewById(R.id.receivedMessageText);
            receivedMessageTime = itemView.findViewById(R.id.receivedMessageTime);
            senderAvatarImageView = itemView.findViewById(R.id.senderAvatarImageView);
        }

        public void bind(Message message, int currentUserId) {
            boolean isSentByCurrentUser = message.getSenderUserId() == currentUserId;
            
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String timeText = timeFormat.format(message.getMessageDate());

            if (isSentByCurrentUser) {
                // Show sent message
                sentMessageContainer.setVisibility(View.VISIBLE);
                receivedMessageContainer.setVisibility(View.GONE);
                
                sentMessageText.setText(message.getMessageContent());
                sentMessageTime.setText(timeText);
            } else {
                // Show received message
                sentMessageContainer.setVisibility(View.GONE);
                receivedMessageContainer.setVisibility(View.VISIBLE);
                
                receivedMessageText.setText(message.getMessageContent());
                receivedMessageTime.setText(timeText);
                
                // Load sender avatar
                loadSenderAvatar(message);
            }
        }

        private void loadSenderAvatar(Message message) {
            String avatarPath = message.getSenderAvatar();
            
            if (avatarPath != null && !avatarPath.isEmpty()) {
                // Check if it's a drawable resource name
                if (!avatarPath.contains("/") && !avatarPath.contains("\\")) {
                    try {
                        int resourceId = itemView.getContext().getResources().getIdentifier(
                            avatarPath, "drawable", itemView.getContext().getPackageName());
                        if (resourceId != 0) {
                            Glide.with(itemView.getContext())
                                    .load(resourceId)
                                    .placeholder(R.drawable.avatar)
                                    .error(R.drawable.avatar)
                                    .into(senderAvatarImageView);
                        } else {
                            senderAvatarImageView.setImageResource(R.drawable.avatar);
                        }
                    } catch (Exception e) {
                        senderAvatarImageView.setImageResource(R.drawable.avatar);
                    }
                } else {
                    // Load from file path
                    java.io.File imageFile = new java.io.File(avatarPath);
                    if (imageFile.exists()) {
                        Glide.with(itemView.getContext())
                                .load(imageFile)
                                .placeholder(R.drawable.avatar)
                                .error(R.drawable.avatar)
                                .into(senderAvatarImageView);
                    } else {
                        senderAvatarImageView.setImageResource(R.drawable.avatar);
                    }
                }
            } else {
                senderAvatarImageView.setImageResource(R.drawable.avatar);
            }
        }
    }
}
