# Hệ Thống Kết Bạn & Chat - Database Design Hoàn Thành

## 🎯 Tổng Quan
Đã thiết kế và triển khai thành công **hệ thống kết bạn và chat** hoàn chỉnh cho QuizApp với đầy đủ tính năng:

✅ **Tìm kiếm bạn bè** bằng email hoặc tên  
✅ **Gửi/nhận lời mời kết bạn**  
✅ **Chấp nhận/từ chối lời mời**  
✅ **Quản lý danh sách bạn bè**  
✅ **Hệ thống chat/tin nhắn** 

## 🗂️ Database Schema

### 1. Bảng `friend_requests` (Lời mời kết bạn)
```sql
CREATE TABLE friend_requests (
    request_id INTEGER PRIMARY KEY AUTOINCREMENT,
    sender_id INTEGER NOT NULL,           -- Người gửi lời mời
    receiver_id INTEGER NOT NULL,         -- Người nhận lời mời  
    status TEXT DEFAULT 'pending',        -- pending/accepted/rejected
    request_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    response_date DATETIME,               -- Thời điểm phản hồi
    FOREIGN KEY(sender_id) REFERENCES users(user_id),
    FOREIGN KEY(receiver_id) REFERENCES users(user_id),
    UNIQUE(sender_id, receiver_id)        -- Tránh duplicate requests
);
```

### 2. Bảng `friends` (Danh sách bạn bè)
```sql  
CREATE TABLE friends (
    friendship_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user1_id INTEGER NOT NULL,            -- User có ID nhỏ hơn
    user2_id INTEGER NOT NULL,            -- User có ID lớn hơn  
    friendship_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(user1_id) REFERENCES users(user_id),
    FOREIGN KEY(user2_id) REFERENCES users(user_id),
    UNIQUE(user1_id, user2_id)            -- Tránh duplicate friendships
);
```

### 3. Bảng `messages` (Tin nhắn)
```sql
CREATE TABLE messages (
    message_id INTEGER PRIMARY KEY AUTOINCREMENT,
    sender_user_id INTEGER NOT NULL,      -- Người gửi
    receiver_user_id INTEGER NOT NULL,    -- Người nhận
    message_content TEXT NOT NULL,        -- Nội dung tin nhắn
    message_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    is_read INTEGER DEFAULT 0,            -- 0=chưa đọc, 1=đã đọc
    FOREIGN KEY(sender_user_id) REFERENCES users(user_id),
    FOREIGN KEY(receiver_user_id) REFERENCES users(user_id)
);
```

## 📦 Model Classes

### FriendRequest.java
```java
public class FriendRequest {
    private int requestId, senderId, receiverId;
    private String status; // pending, accepted, rejected
    private Date requestDate, responseDate;
    
    // Thông tin bổ sung từ JOIN với bảng users
    private String senderName, senderEmail, senderAvatar;
    private String receiverName, receiverEmail, receiverAvatar;
    // ... getters & setters
}
```

### Friend.java  
```java
public class Friend {
    private int friendshipId, user1Id, user2Id;
    private Date friendshipDate;
    
    // Thông tin bạn bè (người còn lại)
    private String friendName, friendEmail, friendAvatar;
    private long friendCoins;
    // ... getters & setters  
}
```

### Message.java
```java
public class Message {
    private int messageId, senderUserId, receiverUserId;
    private String messageContent;
    private Date messageDate;
    private boolean isRead;
    
    // Thông tin bổ sung
    private String senderName, senderAvatar;
    private String receiverName, receiverAvatar;
    // ... getters & setters
}
```

## 🛠️ Database Methods Implemented

### 🔍 Tìm Kiếm & Kết Bạn
```java
// Tìm kiếm user bằng email hoặc name (loại trừ bản thân và admin)
public List<User> searchUsers(String searchQuery, int currentUserId)

// Gửi lời mời kết bạn (kiểm tra duplicate)
public long sendFriendRequest(int senderId, int receiverId)

// Kiểm tra lời mời đã tồn tại (cả 2 chiều)
private boolean friendRequestExists(int senderId, int receiverId)
```

### 📨 Quản Lý Lời Mời
```java
// Lấy danh sách lời mời nhận được (với thông tin người gửi)
public List<FriendRequest> getReceivedFriendRequests(int userId)

// Chấp nhận lời mời (transaction: update request + insert friend)
public boolean acceptFriendRequest(int requestId)

// Từ chối lời mời
public boolean rejectFriendRequest(int requestId)

// Lấy thông tin request theo ID
private FriendRequest getFriendRequestById(int requestId)
```

### 👥 Danh Sách Bạn Bè  
```java
// Lấy danh sách bạn bè (với thông tin chi tiết)
public List<Friend> getFriends(int userId)
```

### 💬 Hệ Thống Chat
```java
// Gửi tin nhắn
public long sendMessage(int senderId, int receiverId, String messageContent)

// Lấy tin nhắn giữa 2 user (sắp xếp theo thời gian)
public List<Message> getMessagesBetweenUsers(int user1Id, int user2Id)

// Đánh dấu 1 tin nhắn đã đọc
public void markMessageAsRead(int messageId)

// Đánh dấu tất cả tin nhắn từ user đã đọc
public void markAllMessagesAsRead(int senderId, int receiverId)

// Lấy số tin nhắn chưa đọc
public int getUnreadMessageCount(int userId)
```

## 🔒 Tính Năng Bảo Mật

### Tránh Duplicate
- **UNIQUE constraints** trong database ngăn duplicate requests và friendships
- **Bidirectional check** trong `friendRequestExists()`

### Data Integrity  
- **Foreign Key constraints** đảm bảo referential integrity
- **Transaction** trong `acceptFriendRequest()` đảm bảo consistency
- **Status validation** (pending/accepted/rejected)

### Friendship Logic
- **Normalized friendship**: user1_id < user2_id luôn để tránh duplicate (A-B và B-A)
- **Smart friend lookup**: sử dụng CASE trong SQL để lấy đúng thông tin bạn bè

## 📊 Database Version
- **Cập nhật DATABASE_VERSION từ 7 → 8**
- **Auto-migration** với onUpgrade() method  
- **Backwards compatibility** với existing data

## 🎮 Workflow Hoạt Động

### 1. Tìm Kiếm & Kết Bạn
```
User A search "john" → searchUsers() → Hiện danh sách users
→ User A click "Add Friend" → sendFriendRequest() → Insert vào friend_requests
```

### 2. Quản Lý Lời Mời  
```
User B mở "Friend Requests" → getReceivedFriendRequests()  
→ User B click "Accept" → acceptFriendRequest() → Update status + Insert friendship
```

### 3. Chat
```
User A chọn friend → getMessagesBetweenUsers() → Hiện chat history
→ User A gửi tin nhắn → sendMessage() → Insert message
→ User B mở chat → markAllMessagesAsRead() → Update is_read = 1
```

## 🚀 Status: Ready for Implementation
✅ **Database Design Complete**  
✅ **Model Classes Ready**  
✅ **All CRUD Methods Implemented**  
✅ **Build Successful**  
✅ **Ready for UI Development**

Hệ thống database hoàn chỉnh, sẵn sàng cho việc phát triển UI components! 🎉
