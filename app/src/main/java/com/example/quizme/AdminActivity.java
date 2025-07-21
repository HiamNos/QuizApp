package com.example.quizme;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quizme.databinding.ActivityAdminBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private ActivityAdminBinding binding;
    private DatabaseHelper databaseHelper;
    private List<CategoryModel> categories;
    private List<Question> questions;
    private ArrayAdapter<String> categoryAdapter;
    private QuestionAdminAdapter questionAdapter;
    private int selectedCategoryId = -1;
    
    // Image selection
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private String selectedImagePath = "";
    private Uri selectedImageUri = null;
    private ImageView dialogImagePreview;
    private TextView dialogImagePath;
    
    // Activity result launcher for AddQuestionActivity
    private ActivityResultLauncher<Intent> addQuestionLauncher;
    private Button dialogRemoveImageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);
        categories = new ArrayList<>();
        questions = new ArrayList<>();

        setupImagePickerLauncher();
        setupAddQuestionLauncher();

        // Kiểm tra quyền admin
        SharedPreferences prefs = getSharedPreferences("QuizApp", MODE_PRIVATE);
        String userRole = prefs.getString("user_role", "user");
        if (!"admin".equals(userRole)) {
            Toast.makeText(this, "Bạn không có quyền truy cập trang này!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupUI();
        loadData();
    }

    private void setupImagePickerLauncher() {
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            selectedImagePath = selectedImageUri.toString();
                            updateImagePreview(selectedImageUri);
                        }
                    }
                }
            }
        );
    }
    
    private void setupAddQuestionLauncher() {
        addQuestionLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        int updatedCategoryId = data.getIntExtra("updated_category_id", -1);
                        String updatedCategoryName = data.getStringExtra("updated_category_name");
                        
                        if (updatedCategoryId != -1) {
                            // Cập nhật selectedCategoryId
                            selectedCategoryId = updatedCategoryId;
                            
                            // Tìm và cập nhật Spinner để hiển thị đúng danh mục
                            for (int i = 0; i < categories.size(); i++) {
                                if (categories.get(i).getCategoryId() == updatedCategoryId) {
                                    binding.categorySpinner.setSelection(i);
                                    break;
                                }
                            }
                            
                            // Cập nhật danh sách câu hỏi cho danh mục đã cập nhật
                            loadQuestionsByCategory(selectedCategoryId);
                            
                            // Hiển thị thông báo
                            Toast.makeText(AdminActivity.this, 
                                "✅ Đã thêm câu hỏi vào danh mục: " + updatedCategoryName, 
                                Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        );
    }

    private void updateImagePreview(Uri imageUri) {
        if (dialogImagePreview != null) {
            dialogImagePreview.setImageURI(imageUri);
            dialogImagePath.setText("✅ Đã chọn hình ảnh từ thư viện");
            dialogRemoveImageBtn.setVisibility(View.VISIBLE);
        }
    }

    private String saveImageToInternalStorage(Uri imageUri, String categoryName) {
        try {
            // Tạo thư mục images nếu chưa có
            File imagesDir = new File(getFilesDir(), "category_images");
            if (!imagesDir.exists()) {
                imagesDir.mkdirs();
            }

            // Tạo tên file duy nhất
            String fileName = "cat_" + System.currentTimeMillis() + ".jpg";
            File imageFile = new File(imagesDir, fileName);

            // Copy ảnh từ URI vào internal storage
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            FileOutputStream outputStream = new FileOutputStream(imageFile);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            return imageFile.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "❌ Lỗi khi lưu hình ảnh!", Toast.LENGTH_SHORT).show();
            return "";
        }
    }

    private void setupUI() {
        // Setup category spinner
        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.categorySpinner.setAdapter(categoryAdapter);

        // Setup question list with custom adapter
        questionAdapter = new QuestionAdminAdapter(this, questions, categories);
        binding.questionList.setAdapter(questionAdapter);

        // Category spinner listener
        binding.categorySpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < categories.size()) {
                    selectedCategoryId = categories.get(position).getCategoryId();
                    loadQuestionsByCategory(selectedCategoryId);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                selectedCategoryId = -1;
                loadQuestionsByCategory(-1);
            }
        });

        // Button listeners
        binding.addCategoryBtn.setOnClickListener(v -> showAddCategoryDialog());
        binding.addQuestionBtn.setOnClickListener(v -> showAddQuestionDialog());
        binding.editQuestionBtn.setOnClickListener(v -> editSelectedQuestion());
        binding.deleteQuestionBtn.setOnClickListener(v -> deleteSelectedQuestion());
        binding.backBtn.setOnClickListener(v -> finish());

        // List item click - toggle selection
        binding.questionList.setOnItemClickListener((parent, view, position, id) -> {
            questionAdapter.toggleSelection(position);
            updateActionButtonsVisibility();
        });

        // Initially hide action buttons
        binding.actionButtonsContainer.setVisibility(View.GONE);
    }

    private void updateActionButtonsVisibility() {
        int selectedPosition = questionAdapter.getSelectedPosition();
        if (selectedPosition != -1) {
            binding.actionButtonsContainer.setVisibility(View.VISIBLE);
        } else {
            binding.actionButtonsContainer.setVisibility(View.GONE);
        }
    }

    private void loadData() {
        // Lưu danh mục đã chọn trước đó
        int previousSelectedCategoryId = selectedCategoryId;
        
        // Load categories
        categories.clear();
        categories.addAll(databaseHelper.getAllCategories());
        
        List<String> categoryNames = new ArrayList<>();
        for (CategoryModel category : categories) {
            categoryNames.add(category.getCategoryName());
        }
        
        categoryAdapter.clear();
        categoryAdapter.addAll(categoryNames);
        categoryAdapter.notifyDataSetChanged();

        // Khôi phục danh mục đã chọn trước đó hoặc chọn danh mục đầu tiên
        if (!categories.isEmpty()) {
            boolean found = false;
            
            // Tìm danh mục đã chọn trước đó
            if (previousSelectedCategoryId != -1) {
                for (int i = 0; i < categories.size(); i++) {
                    if (categories.get(i).getCategoryId() == previousSelectedCategoryId) {
                        selectedCategoryId = previousSelectedCategoryId;
                        binding.categorySpinner.setSelection(i);
                        found = true;
                        break;
                    }
                }
            }
            
            // Nếu không tìm thấy, chọn danh mục đầu tiên
            if (!found) {
                selectedCategoryId = categories.get(0).getCategoryId();
                binding.categorySpinner.setSelection(0);
            }
            
            loadQuestionsByCategory(selectedCategoryId);
        } else {
            selectedCategoryId = -1;
            loadQuestionsByCategory(-1);
        }
    }

    private void loadQuestionsByCategory(int categoryId) {
        questions.clear();
        
        if (categoryId == -1) {
            // Load all questions if no category selected
            questions.addAll(databaseHelper.getAllQuestions());
        } else {
            // Load questions by category (limit to 100 to show all questions)
            questions.addAll(databaseHelper.getQuestionsByCategory(categoryId, 100));
        }
        
        // Update adapter with new data
        questionAdapter.updateData(questions);
        
        // Hide action buttons when data changes
        binding.actionButtonsContainer.setVisibility(View.GONE);
        
        // Update question list label
        if (categoryId != -1) {
            CategoryModel selectedCategory = getCategoryById(categoryId);
            if (selectedCategory != null) {
                binding.questionListLabel.setText("Danh sách câu hỏi - " + selectedCategory.getCategoryName() + " (" + questions.size() + " câu hỏi):");
            }
        } else {
            binding.questionListLabel.setText("Danh sách câu hỏi (" + questions.size() + " câu hỏi):");
        }
    }

    private CategoryModel getCategoryById(int categoryId) {
        for (CategoryModel category : categories) {
            if (category.getCategoryId() == categoryId) {
                return category;
            }
        }
        return null;
    }

    private void loadQuestions() {
        // This method is now replaced by loadQuestionsByCategory
        loadQuestionsByCategory(selectedCategoryId);
    }

    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_category, null);
        builder.setView(dialogView);

        // Lấy references
        EditText categoryNameInput = dialogView.findViewById(R.id.categoryNameInput);
        dialogImagePreview = dialogView.findViewById(R.id.categoryImagePreview);
        dialogImagePath = dialogView.findViewById(R.id.selectedImagePath);
        Button selectImageBtn = dialogView.findViewById(R.id.selectImageBtn);
        dialogRemoveImageBtn = dialogView.findViewById(R.id.removeImageBtn);
        Button cancelBtn = dialogView.findViewById(R.id.cancelBtn);
        Button addBtn = dialogView.findViewById(R.id.addBtn);

        // Reset selected image
        selectedImagePath = "";
        selectedImageUri = null;
        dialogImagePreview.setImageResource(R.drawable.trophy);
        dialogImagePath.setText("💡 Chưa chọn hình ảnh (sẽ dùng icon mặc định)");
        dialogRemoveImageBtn.setVisibility(View.GONE);

        // Setup button listeners
        selectImageBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        dialogRemoveImageBtn.setOnClickListener(v -> {
            selectedImagePath = "";
            selectedImageUri = null;
            dialogImagePreview.setImageResource(R.drawable.trophy);
            dialogImagePath.setText("💡 Chưa chọn hình ảnh (sẽ dùng icon mặc định)");
            dialogRemoveImageBtn.setVisibility(View.GONE);
        });

        AlertDialog dialog = builder.create();

        addBtn.setOnClickListener(v -> {
            String categoryName = categoryNameInput.getText().toString().trim();
            
            if (categoryName.isEmpty()) {
                Toast.makeText(this, "⚠️ Vui lòng nhập tên danh mục!", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Tạo category mới
            CategoryModel newCategory = new CategoryModel();
            newCategory.setCategoryName(categoryName);
            
            // Xử lý hình ảnh
            String finalImagePath = "quiz_icon"; // Default
            if (selectedImageUri != null) {
                finalImagePath = saveImageToInternalStorage(selectedImageUri, categoryName);
                if (finalImagePath.isEmpty()) {
                    finalImagePath = "quiz_icon"; // Fallback to default if save failed
                }
            }
            
            newCategory.setCategoryImage(finalImagePath);
            
            // Thêm vào database
            long result = databaseHelper.addCategory(newCategory);
            if (result != -1) {
                Toast.makeText(this, "✅ Đã thêm danh mục: " + categoryName, Toast.LENGTH_SHORT).show();
                loadData(); // Reload data
                dialog.dismiss();
            } else {
                Toast.makeText(this, "❌ Thêm danh mục thất bại!", Toast.LENGTH_SHORT).show();
            }
        });

        cancelBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showAddQuestionDialog() {
        if (categories.isEmpty()) {
            Toast.makeText(this, "⚠️ Vui lòng thêm danh mục trước!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, AddQuestionActivity.class);
        
        // Truyền thông tin danh mục đã chọn
        if (selectedCategoryId != -1) {
            intent.putExtra("selected_category_id", selectedCategoryId);
            
            // Tìm tên danh mục
            for (CategoryModel category : categories) {
                if (category.getCategoryId() == selectedCategoryId) {
                    intent.putExtra("selected_category_name", category.getCategoryName());
                    break;
                }
            }
        }
        
        addQuestionLauncher.launch(intent);
    }

    private void editSelectedQuestion() {
        int position = questionAdapter.getSelectedPosition();
        if (position == -1) {
            Toast.makeText(this, "⚠️ Vui lòng chọn câu hỏi để sửa!", Toast.LENGTH_SHORT).show();
            return;
        }

        Question question = questions.get(position);
        Intent intent = new Intent(this, EditQuestionActivity.class);
        intent.putExtra("question_id", question.getQuestionId());
        startActivity(intent);
    }

    private void deleteSelectedQuestion() {
        int position = questionAdapter.getSelectedPosition();
        if (position == -1) {
            Toast.makeText(this, "⚠️ Vui lòng chọn câu hỏi để xóa!", Toast.LENGTH_SHORT).show();
            return;
        }

        Question question = questions.get(position);
        String questionText = question.getQuestion();
        if (questionText.length() > 30) {
            questionText = questionText.substring(0, 27) + "...";
        }

        new AlertDialog.Builder(this)
                .setTitle("🗑️ Xác nhận xóa câu hỏi")
                .setMessage("Bạn có chắc chắn muốn xóa câu hỏi này?\n\n\"" + questionText + "\"\n\nHành động này không thể hoàn tác!")
                .setPositiveButton("✅ Xóa", (dialog, which) -> {
                    boolean result = databaseHelper.deleteQuestion(question.getQuestionId());
                    if (result) {
                        loadQuestionsByCategory(selectedCategoryId);
                        Toast.makeText(this, "✅ Đã xóa câu hỏi thành công!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "❌ Xóa câu hỏi thất bại!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("❌ Hủy", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
} 