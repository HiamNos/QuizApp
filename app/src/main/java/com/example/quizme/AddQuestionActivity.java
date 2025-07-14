package com.example.quizme;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddQuestionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);
        categories = new ArrayList<>();

        setupUI();
        loadCategories();
    }

    private void setupUI() {
        binding.addQuestionBtn.setOnClickListener(v -> addQuestion());
        binding.backBtn.setOnClickListener(v -> finish());
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
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (answer.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn đáp án đúng!", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedPosition = binding.categorySpinner.getSelectedItemPosition();
        if (selectedPosition < 0 || selectedPosition >= categories.size()) {
            Toast.makeText(this, "Vui lòng chọn danh mục!", Toast.LENGTH_SHORT).show();
            return;
        }

        CategoryModel selectedCategory = categories.get(selectedPosition);
        Question question = new Question(questionText, option1, option2, option3, option4, answer);

        long result = databaseHelper.addQuestion(question, selectedCategory.getCategoryId());
        if (result != -1) {
            Toast.makeText(this, "Thêm câu hỏi thành công!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Thêm câu hỏi thất bại!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Thiết lập Spinner đáp án đúng
        String[] answerOptions = {"Đáp án 1", "Đáp án 2", "Đáp án 3", "Đáp án 4"};
        ArrayAdapter<String> answerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, answerOptions);
        answerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.answerSpinner.setAdapter(answerAdapter);
    }
} 