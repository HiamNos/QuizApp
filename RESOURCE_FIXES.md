# Sửa lỗi Android Resource Linking

## Vấn đề
```
Android resource linking failed
com.example.quizme.app-main-41:/drawable/ic_back.xml:6: error: resource attr/colorOnSurface (aka com.example.quizme:attr/colorOnSurface) not found.
com.example.quizme.app-mergeDebugResources-38:/layout/activity_forgot_password.xml:32: error: resource color/white (aka com.example.quizme:color/white) not found.
```

## Nguyên nhân
1. **ic_back.xml**: Sử dụng `?attr/colorOnSurface` không tồn tại trong theme
2. **activity_forgot_password.xml**: Sử dụng `@color/white` không được định nghĩa trong colors.xml

## Giải pháp

### 1. Sửa ic_back.xml
```xml
<!-- Trước -->
android:tint="?attr/colorOnSurface"

<!-- Sau -->
android:tint="@android:color/white"
```

### 2. Thêm màu white vào colors.xml
```xml
<color name="white">#FFFFFF</color>
```

### 3. Sửa activity_forgot_password.xml
Thay thế tất cả `@color/white` bằng `@android:color/white`:

```xml
<!-- Trước -->
android:textColor="@color/white"

<!-- Sau -->
android:textColor="@android:color/white"
```

## Các thay đổi chi tiết

### colors.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="colorPrimary">#673AB7</color>
    <color name="colorPrimaryDark">#673AB7</color>
    <color name="colorAccent">#03DAC5</color>
    <color name="colorPurple">#673AB7</color>
    <color name="colorBlue">#3F51B5</color>
    <color name="orange">#FF9800</color>
    <color name="darkOrange">#FF5722</color>
    <color name="shadow_color">#3C3C3C</color>
    <color name="color_white">#FFFFFF</color>
    <color name="white">#FFFFFF</color>  <!-- Thêm dòng này -->
    <color name="light_grey">#E8E8E8</color>
</resources>
```

### ic_back.xml
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24"
    android:tint="@android:color/white">  <!-- Sửa dòng này -->
  <path
      android:fillColor="@android:color/white"
      android:pathData="M20,11H7.83l5.59,-5.59L12,4l-8,8 8,8 1.41,-1.41L7.83,13H20v-2z"/>
</vector>
```

### activity_forgot_password.xml
Thay thế 13 lần `@color/white` thành `@android:color/white`:
- TextView "Quên mật khẩu"
- TextView "Nhập email của bạn để nhận mã xác nhận"
- EditText emailInput
- Button sendCodeBtn
- TextView "Nhập mã xác nhận đã được gửi đến email"
- EditText codeInput
- Button verifyCodeBtn
- TextView "Nhập mật khẩu mới"
- EditText newPasswordInput
- EditText confirmPasswordInput
- Button resetPasswordBtn
- Button backToLoginBtn

## Kết quả
✅ **Tất cả lỗi resource linking đã được sửa:**
- ic_back.xml sử dụng màu cụ thể thay vì attr không tồn tại
- colors.xml có đầy đủ màu white
- activity_forgot_password.xml sử dụng @android:color/white

✅ **Ứng dụng có thể build thành công:**
- Không còn lỗi resource linking
- Tất cả resource được tìm thấy
- Build process hoàn tất

## Lưu ý
- Sử dụng `@android:color/white` thay vì `@color/white` để đảm bảo tương thích
- `attr/colorOnSurface` chỉ có trong Material Design theme, không có trong AppCompat theme
- Luôn kiểm tra resource tồn tại trước khi sử dụng