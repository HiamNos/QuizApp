package com.example.quizme;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChangeAvatarActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 101;
    private static final int CAMERA_PERMISSION_CODE = 102;

    private CircleImageView currentAvatar;
    private CircleImageView avatarOption1, avatarOption2, avatarOption3, avatarOption4, avatarOption5, avatarOption6;
    private Button selectFromGalleryBtn, takePhotoBtn, saveAvatarBtn;
    private ImageView backButton;
    private TextView toolbarTitle;
    private Uri selectedImageUri;
    private String savedImagePath;
    private String selectedDrawableAvatar; // Để lưu drawable resource name
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_avatar);

        // Khởi tạo database và SharedPreferences trước
        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("QuizApp", MODE_PRIVATE);
        
        initViews();
        setupClickListeners();
        loadCurrentAvatar();
    }

    private void initViews() {
        currentAvatar = findViewById(R.id.currentAvatar);
        selectFromGalleryBtn = findViewById(R.id.selectFromGalleryBtn);
        takePhotoBtn = findViewById(R.id.takePhotoBtn);
        saveAvatarBtn = findViewById(R.id.saveAvatarBtn);
        backButton = findViewById(R.id.backButton);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        
        // Avatar options
        avatarOption1 = findViewById(R.id.avatarOption1);
        avatarOption2 = findViewById(R.id.avatarOption2);
        avatarOption3 = findViewById(R.id.avatarOption3);
        avatarOption4 = findViewById(R.id.avatarOption4);
        avatarOption5 = findViewById(R.id.avatarOption5);
        avatarOption6 = findViewById(R.id.avatarOption6);
        
        // Set toolbar title
        toolbarTitle.setText("Đổi ảnh đại diện");
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        selectFromGalleryBtn.setOnClickListener(v -> openGallery());

        takePhotoBtn.setOnClickListener(v -> {
            if (checkCameraPermission()) {
                openCamera();
            } else {
                requestCameraPermission();
            }
        });

        saveAvatarBtn.setOnClickListener(v -> saveAvatar());
        
        // Avatar options click listeners
        avatarOption1.setOnClickListener(v -> selectDrawableAvatar("avatar_male_1"));
        avatarOption2.setOnClickListener(v -> selectDrawableAvatar("avatar_female_1"));
        avatarOption3.setOnClickListener(v -> selectDrawableAvatar("avatar_male_2"));
        avatarOption4.setOnClickListener(v -> selectDrawableAvatar("avatar_female_2"));
        avatarOption5.setOnClickListener(v -> selectDrawableAvatar("avatar_male_3"));
        avatarOption6.setOnClickListener(v -> selectDrawableAvatar("avatar"));
    }
    
    private void selectDrawableAvatar(String drawableName) {
        // Clear previous selections
        selectedImageUri = null;
        savedImagePath = null;
        selectedDrawableAvatar = drawableName;
        
        // Load selected avatar to current avatar view
        try {
            int resourceId = getResources().getIdentifier(drawableName, "drawable", getPackageName());
            if (resourceId != 0) {
                Glide.with(this)
                        .load(resourceId)
                        .into(currentAvatar);
                        
                // Show save button
                saveAvatarBtn.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Avatar đã được chọn", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi chọn avatar", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadCurrentAvatar() {
        try {
            int userId = sharedPreferences.getInt("user_id", -1);
            
            if (userId != -1) {
                User user = databaseHelper.getUserById(userId);
                if (user != null && user.getAvatarImage() != null && !user.getAvatarImage().isEmpty()) {
                    String imagePath = user.getAvatarImage();
                    
                    // Kiểm tra nếu là drawable resource name (không chứa "/")
                    if (!imagePath.contains("/") && !imagePath.contains("\\")) {
                        // Load từ drawable resource
                        try {
                            int resourceId = getResources().getIdentifier(
                                imagePath, "drawable", getPackageName());
                            if (resourceId != 0) {
                                Glide.with(this)
                                        .load(resourceId)
                                        .placeholder(R.drawable.avatar)
                                        .error(R.drawable.avatar)
                                        .into(currentAvatar);
                            } else {
                                // Resource không tồn tại, dùng default
                                Glide.with(this)
                                        .load(R.drawable.avatar)
                                        .into(currentAvatar);
                            }
                        } catch (Exception e) {
                            Glide.with(this)
                                    .load(R.drawable.avatar)
                                    .into(currentAvatar);
                        }
                    } else {
                        // Load từ file path (existing logic)
                        File imageFile = new File(imagePath);
                        if (imageFile.exists()) {
                            Glide.with(this)
                                    .load(imageFile)
                                    .placeholder(R.drawable.avatar)
                                    .error(R.drawable.avatar)
                                    .into(currentAvatar);
                        } else {
                            // File không tồn tại, load ảnh mặc định
                            Glide.with(this)
                                    .load(R.drawable.avatar)
                                    .into(currentAvatar);
                        }
                    }
                } else {
                    // Chưa có avatar, load ảnh mặc định
                    Glide.with(this)
                            .load(R.drawable.avatar)
                            .into(currentAvatar);
                }
            } else {
                // Chưa đăng nhập, load ảnh mặc định
                Glide.with(this)
                        .load(R.drawable.avatar)
                        .into(currentAvatar);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Nếu có lỗi, load ảnh mặc định
            Glide.with(this)
                    .load(R.drawable.avatar)
                    .into(currentAvatar);
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    private void openCamera() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        } catch (Exception e) {
            Toast.makeText(this, "Không thể mở camera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Cần quyền truy cập camera để chụp ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == GALLERY_REQUEST_CODE) {
                selectedImageUri = data.getData();
                // Sử dụng Glide để hiển thị ảnh từ gallery
                Glide.with(this)
                        .load(selectedImageUri)
                        .placeholder(R.drawable.avatar)
                        .error(R.drawable.avatar)
                        .into(currentAvatar);
                saveAvatarBtn.setVisibility(View.VISIBLE);
                
            } else if (requestCode == CAMERA_REQUEST_CODE) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                
                // Lưu bitmap thành file ngay lập tức
                savedImagePath = saveImageToInternalStorage(imageBitmap);
                
                if (savedImagePath != null) {
                    // Sử dụng Glide để hiển thị ảnh từ file
                    Glide.with(this)
                            .load(new File(savedImagePath))
                            .placeholder(R.drawable.avatar)
                            .error(R.drawable.avatar)
                            .into(currentAvatar);
                    saveAvatarBtn.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(this, "Không thể lưu ảnh", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private String saveImageToInternalStorage(Bitmap bitmap) {
        try {
            File directory = new File(getFilesDir(), "avatars");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            String fileName = "avatar_" + System.currentTimeMillis() + ".jpg";
            File file = new File(directory, fileName);
            
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.close();
            
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveAvatar() {
        int userId = sharedPreferences.getInt("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        String imagePath = null;
        
        // Ưu tiên drawable avatar nếu đã chọn
        if (selectedDrawableAvatar != null) {
            imagePath = selectedDrawableAvatar;
        } else if (selectedImageUri != null) {
            // Copy image từ gallery vào internal storage
            imagePath = copyImageToInternalStorage(selectedImageUri);
        } else if (savedImagePath != null) {
            // Dùng ảnh đã chụp
            imagePath = savedImagePath;
        }
        
        if (imagePath != null) {
            boolean success = databaseHelper.updateUserAvatar(userId, imagePath);
            if (success) {
                Toast.makeText(this, "Ảnh đại diện đã được cập nhật!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Có lỗi xảy ra khi cập nhật ảnh đại diện", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Không thể lưu ảnh đại diện", Toast.LENGTH_SHORT).show();
        }
    }

    private String copyImageToInternalStorage(Uri imageUri) {
        try {
            File directory = new File(getFilesDir(), "avatars");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            String fileName = "avatar_" + System.currentTimeMillis() + ".jpg";
            File file = new File(directory, fileName);
            
            // Copy file từ URI vào internal storage
            java.io.InputStream inputStream = getContentResolver().openInputStream(imageUri);
            java.io.FileOutputStream outputStream = new java.io.FileOutputStream(file);
            
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            
            inputStream.close();
            outputStream.close();
            
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
