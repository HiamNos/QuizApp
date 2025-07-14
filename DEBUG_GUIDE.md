# Hướng dẫn Debug Ứng dụng Quiz

## Tình trạng hiện tại
✅ **Build thành công** - Ứng dụng đã build được APK
❓ **Chạy không hoạt động** - Cần kiểm tra runtime issues

## Các vấn đề đã sửa

### 1. MainActivity - Bottom Bar Navigation
**Vấn đề**: Xử lý navigation theo index thay vì ID
**Sửa**: 
```java
// Trước
switch (i) {
    case 0: // home
    case 1: // rank
    case 2: // wallet
    case 3: // profile
}

// Sau
switch (i) {
    case R.id.home:
    case R.id.rank:
    case R.id.wallet:
    case R.id.profile:
}
```

### 2. MainActivity - Logout Intent
**Vấn đề**: Intent flags không đúng
**Sửa**:
```java
intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
```

## Hướng dẫn kiểm tra

### 1. Kiểm tra Logcat
Chạy ứng dụng và xem logcat để tìm lỗi:
```bash
adb logcat | grep "com.example.quizme"
```

### 2. Kiểm tra từng màn hình

#### LoginActivity
- Email: `admin@quizapp.com`
- Password: `admin123`
- Hoặc đăng ký tài khoản mới

#### MainActivity
- Kiểm tra bottom navigation
- Kiểm tra menu toolbar
- Kiểm tra fragment switching

#### HomeFragment
- Kiểm tra danh sách categories
- Kiểm tra click vào category
- Kiểm tra spin wheel

### 3. Kiểm tra Database
```java
// Trong MainActivity onCreate()
DatabaseHelper db = new DatabaseHelper(this);
List<CategoryModel> categories = db.getAllCategories();
Log.d("DEBUG", "Categories count: " + categories.size());
```

### 4. Kiểm tra SharedPreferences
```java
// Kiểm tra user session
SharedPreferences prefs = getSharedPreferences("QuizApp", MODE_PRIVATE);
int userId = prefs.getInt("user_id", -1);
String userRole = prefs.getString("user_role", "user");
Log.d("DEBUG", "User ID: " + userId + ", Role: " + userRole);
```

## Các bước debug

### Bước 1: Kiểm tra Login
1. Chạy ứng dụng
2. Đăng nhập với admin account
3. Kiểm tra có chuyển sang MainActivity không

### Bước 2: Kiểm tra Navigation
1. Kiểm tra bottom bar có hiển thị không
2. Click vào từng tab (Home, Rank, Wallet, Profile)
3. Kiểm tra fragment có chuyển đổi không

### Bước 3: Kiểm tra HomeFragment
1. Kiểm tra danh sách categories có hiển thị không
2. Click vào một category
3. Kiểm tra có chuyển sang QuizActivity không

### Bước 4: Kiểm tra Admin
1. Đăng nhập với admin account
2. Kiểm tra menu có "Quản lý" không
3. Click vào "Quản lý" để mở AdminActivity

## Lỗi thường gặp

### 1. Database không tạo
**Triệu chứng**: Không có categories hiển thị
**Giải pháp**: Xóa app và cài lại để tạo database mới

### 2. Fragment không hiển thị
**Triệu chứng**: Màn hình trống
**Giải pháp**: Kiểm tra layout binding

### 3. Navigation không hoạt động
**Triệu chứng**: Click không phản hồi
**Giải pháp**: Kiểm tra OnItemSelectedListener

## Thông tin test

### Admin Account
- Email: `admin@quizapp.com`
- Password: `admin123`
- Role: `admin`

### Sample Data
- 5 categories: Lịch sử, Địa lý, Văn hóa, Khoa học, Toán học
- 25 questions tổng cộng
- 1 admin user mặc định

## Kết luận
Ứng dụng đã build thành công và các lỗi compile đã được sửa. Vấn đề runtime có thể do:
1. Database chưa được tạo đúng
2. Navigation logic có lỗi
3. Fragment binding có vấn đề

Hãy chạy ứng dụng và kiểm tra logcat để tìm lỗi cụ thể. 