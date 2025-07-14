package com.example.quizme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quizme.databinding.ActivityAdminBinding;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);
        categories = new ArrayList<>();
        questions = new ArrayList<>();

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

        // Load questions for first category if available
        if (!categories.isEmpty()) {
            selectedCategoryId = categories.get(0).getCategoryId();
            loadQuestionsByCategory(selectedCategoryId);
        } else {
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
        builder.setTitle("📂 Thêm danh mục mới");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_category, null);
        builder.setView(dialogView);

        // Lấy reference đến EditText
        android.widget.EditText categoryNameInput = dialogView.findViewById(R.id.categoryNameInput);
        android.widget.EditText categoryImageInput = dialogView.findViewById(R.id.categoryImageInput);

        builder.setPositiveButton("✅ Thêm", (dialog, which) -> {
            String categoryName = categoryNameInput.getText().toString().trim();
            String categoryImage = categoryImageInput.getText().toString().trim();
            
            if (categoryName.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên danh mục!", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Tạo category mới
            CategoryModel newCategory = new CategoryModel();
            newCategory.setCategoryName(categoryName);
            newCategory.setCategoryImage(categoryImage.isEmpty() ? "quiz_icon" : categoryImage);
            
            // Thêm vào database
            long result = databaseHelper.addCategory(newCategory);
            if (result != -1) {
                Toast.makeText(this, "✅ Đã thêm danh mục: " + categoryName, Toast.LENGTH_SHORT).show();
                loadData(); // Reload data
            } else {
                Toast.makeText(this, "❌ Thêm danh mục thất bại!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("❌ Hủy", (dialog, which) -> dialog.dismiss());
        
        AlertDialog dialog = builder.create();
        dialog.show();
        
        // Tùy chỉnh style cho dialog
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPurple));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(android.R.color.darker_gray));
    }

    private void showAddQuestionDialog() {
        if (categories.isEmpty()) {
            Toast.makeText(this, "Vui lòng thêm danh mục trước!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, AddQuestionActivity.class);
        startActivity(intent);
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