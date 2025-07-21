package com.example.quizme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizme.databinding.ActivityAddQuestionBinding;

import java.util.ArrayList;
import java.util.List;

public class AddQuestionActivity extends AppCompatActivity {

    private ActivityAddQuestionBinding binding;
    private DatabaseHelper databaseHelper;
    private List<CategoryModel> categories;
    private int preselectedCategoryId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddQuestionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);
        categories = new ArrayList<>();

        // Nhận thông tin danh mục đã chọn từ AdminActivity
        preselectedCategoryId = getIntent().getIntExtra("selected_category_id", -1);

        setupUI();
        loadCategories();
    }

    private void setupUI() {
        binding.addQuestionBtn.setOnClickListener(v -> addQuestion());
        binding.backBtn.setOnClickListener(v -> finish());
        binding.cancelBtn.setOnClickListener(v -> finish());
        
        // Thiết lập Spinner đáp án đúng
        setupAnswerSpinner();
    }

    private void loadCategories() {
        categories.clear();
        categories.addAll(databaseHelper.getAllCategories());

        List<String> categoryNames = new ArrayList<>();
        for (CategoryModel category : categories) {
            categoryNames.add(category.getCategoryName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, categoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.categorySpinner.setAdapter(adapter);
        
        // Tự động chọn danh mục đã được chọn từ AdminActivity
        if (preselectedCategoryId != -1) {
            for (int i = 0; i < categories.size(); i++) {
                if (categories.get(i).getCategoryId() == preselectedCategoryId) {
                    binding.categorySpinner.setSelection(i);
                    break;
                }
            }
        }
    }

    private void setupAnswerSpinner() {
        String[] answerOptions = {"A - Lựa chọn 1", "B - Lựa chọn 2", "C - Lựa chọn 3", "D - Lựa chọn 4"};
        ArrayAdapter<String> answerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, answerOptions);
        answerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.answerSpinner.setAdapter(answerAdapter);
    }

    private void addQuestion() {
        String questionText = binding.questionInput.getText().toString().trim();
        String option1 = binding.option1Input.getText().toString().trim();
        String option2 = binding.option2Input.getText().toString().trim();
        String option3 = binding.option3Input.getText().toString().trim();
        String option4 = binding.option4Input.getText().toString().trim();

        // Lấy đáp án đúng từ Spinner
        int answerIndex = binding.answerSpinner.getSelectedItemPosition(); // 0-3
        String answer = "";
        switch (answerIndex) {
            case 0: answer = option1; break;
            case 1: answer = option2; break;
            case 2: answer = option3; break;
            case 3: answer = option4; break;
        }

        if (questionText.isEmpty() || option1.isEmpty() || option2.isEmpty() || 
            option3.isEmpty() || option4.isEmpty()) {
            Toast.makeText(this, "⚠️ Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (binding.answerSpinner.getSelectedItemPosition() == -1) {
            Toast.makeText(this, "⚠️ Vui lòng chọn đáp án đúng!", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedPosition = binding.categorySpinner.getSelectedItemPosition();
        if (selectedPosition < 0 || selectedPosition >= categories.size()) {
            Toast.makeText(this, "⚠️ Vui lòng chọn danh mục!", Toast.LENGTH_SHORT).show();
            return;
        }

        CategoryModel selectedCategory = categories.get(selectedPosition);
        Question question = new Question(questionText, option1, option2, option3, option4, answer);

        long result = databaseHelper.addQuestion(question, selectedCategory.getCategoryId());
        if (result != -1) {
            Toast.makeText(this, "✅ Thêm câu hỏi thành công!", Toast.LENGTH_SHORT).show();
            
            // Trả về kết quả cho AdminActivity để cập nhật danh sách
            Intent resultIntent = new Intent();
            resultIntent.putExtra("updated_category_id", selectedCategory.getCategoryId());
            resultIntent.putExtra("updated_category_name", selectedCategory.getCategoryName());
            setResult(RESULT_OK, resultIntent);
            
            clearInputs();
            finish();
        } else {
            Toast.makeText(this, "❌ Thêm câu hỏi thất bại!", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearInputs() {
        binding.questionInput.setText("");
        binding.option1Input.setText("");
        binding.option2Input.setText("");
        binding.option3Input.setText("");
        binding.option4Input.setText("");
        binding.categorySpinner.setSelection(0);
        binding.answerSpinner.setSelection(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCategories(); // Reload categories when returning to this activity
    }
} 