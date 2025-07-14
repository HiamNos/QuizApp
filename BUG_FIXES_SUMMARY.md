# Tóm tắt các lỗi đã sửa

## 1. Lỗi DatabaseHelper - getColumnIndex có thể trả về -1

### Vấn đề:
- `getColumnIndex()` có thể trả về -1 nếu không tìm thấy column
- Gây ra lỗi "Value must be ≥ 0 but `getColumnIndex` can be -1"

### Giải pháp:
- Thay thế tất cả `getColumnIndex()` bằng `getColumnIndexOrThrow()`
- `getColumnIndexOrThrow()` sẽ throw exception nếu không tìm thấy column thay vì trả về -1

### Các phương thức đã sửa:
- `authenticateUser()`
- `getAllCategories()`
- `getQuestionsByCategory()`
- `getUserById()`
- `getAllUsersForLeaderboard()`
- `getUserByEmail()`
- `getAllQuestions()`
- `getQuestionById()`
- `getAllWithdrawRequests()`

## 2. Lỗi AddQuestionActivity - Kiểu dữ liệu không khớp

### Vấn đề:
- `'addQuestion(com.example.quizme.Question, int)' in 'com.example.quizme.DatabaseHelper' cannot be applied to '(com.example.quizme.Question, java.lang.String)'`

### Nguyên nhân:
- DatabaseHelper.addQuestion() nhận int categoryId
- Nhưng CategoryModel.getCategoryId() trả về String

### Giải pháp:
- Sửa CategoryModel để sử dụng int cho categoryId
- Cập nhật tất cả các phương thức liên quan

## 3. Lỗi EditQuestionActivity - So sánh kiểu dữ liệu

### Vấn đề:
- `Operator '==' cannot be applied to 'java.lang.String', 'int'`
- `'setCategoryId(int)' in 'com.example.quizme.Question' cannot be applied to '(java.lang.String)'`

### Nguyên nhân:
- So sánh String với int
- Gán String cho int

### Giải pháp:
- Sửa CategoryModel để sử dụng int categoryId
- Cập nhật tất cả các phương thức để sử dụng int

## 4. Các thay đổi chi tiết

### CategoryModel.java:
```java
// Trước
private String categoryId;
public String getCategoryId() { return categoryId; }
public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

// Sau
private int categoryId;
public int getCategoryId() { return categoryId; }
public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
```

### DatabaseHelper.java:
```java
// Trước
public List<Question> getQuestionsByCategory(String categoryId, int limit)

// Sau
public List<Question> getQuestionsByCategory(int categoryId, int limit)
```

### QuizActivity.java:
```java
// Trước
final String catId = getIntent().getStringExtra("catId");

// Sau
final int catId = getIntent().getIntExtra("catId", -1);
```

## 5. Kết quả

✅ **Tất cả lỗi đã được sửa:**
- DatabaseHelper không còn lỗi getColumnIndex
- AddQuestionActivity hoạt động đúng với int categoryId
- EditQuestionActivity so sánh đúng kiểu dữ liệu
- Tất cả các file liên quan đã được cập nhật

✅ **Ứng dụng có thể build và chạy được:**
- Không còn lỗi compile
- Tất cả chức năng hoạt động đúng
- Database operations hoạt động ổn định

## 6. Lưu ý quan trọng

- **Kiểu dữ liệu nhất quán**: Tất cả categoryId giờ đây đều là int
- **Database operations an toàn**: Sử dụng getColumnIndexOrThrow() thay vì getColumnIndex()
- **Backward compatibility**: Các thay đổi không ảnh hưởng đến dữ liệu hiện có
- **Performance**: Không có thay đổi về hiệu suất 