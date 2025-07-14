package com.example.quizme;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizme.databinding.ActivityEditQuestionBinding;

import java.util.ArrayList;
import java.util.List;

public class EditQuestionActivity extends AppCompatActivity {

    private ActivityEditQuestionBinding binding;
    private DatabaseHelper databaseHelper;
    private List<CategoryModel> categories;
    private Question currentQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditQuestionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);
        categories = new ArrayList<>();

        int questionId = getIntent().getIntExtra("question_id", -1);
        if (questionId == -1) {
            Toast.makeText(this, "Không tìm thấy câu hỏi!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupUI();
        loadCategories();
        loadQuestion(questionId);
    }

    private void setupUI() {
        binding.updateQuestionBtn.setOnClickListener(v -> updateQuestion());
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

    private void loadQuestion(int questionId) {
        currentQuestion = databaseHelper.getQuestionById(questionId);
        if (currentQuestion == null) {
            Toast.makeText(this, "Không tìm thấy câu hỏi!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.questionInput.setText(currentQuestion.getQuestion());
        binding.option1Input.setText(currentQuestion.getOption1());
        binding.option2Input.setText(currentQuestion.getOption2());
        binding.option3Input.setText(currentQuestion.getOption3());
        binding.option4Input.setText(currentQuestion.getOption4());

        // Set selected category
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getCategoryId() == currentQuestion.getCategoryId()) {
                binding.categorySpinner.setSelection(i);
                break;
            }
        }

        // Setup answer spinner with options
        setupAnswerSpinner();
        
        // Set selected answer
        String answer = currentQuestion.getAnswer();
        if (answer.equals(currentQuestion.getOption1())) {
            binding.answerSpinner.setSelection(0);
        } else if (answer.equals(currentQuestion.getOption2())) {
            binding.answerSpinner.setSelection(1);
        } else if (answer.equals(currentQuestion.getOption3())) {
            binding.answerSpinner.setSelection(2);
        } else if (answer.equals(currentQuestion.getOption4())) {
            binding.answerSpinner.setSelection(3);
        }
    }

    private void setupAnswerSpinner() {
        List<String> options = new ArrayList<>();
        options.add(currentQuestion.getOption1());
        options.add(currentQuestion.getOption2());
        options.add(currentQuestion.getOption3());
        options.add(currentQuestion.getOption4());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.answerSpinner.setAdapter(adapter);
    }

    private void updateQuestion() {
        String questionText = binding.questionInput.getText().toString().trim();
        String option1 = binding.option1Input.getText().toString().trim();
        String option2 = binding.option2Input.getText().toString().trim();
        String option3 = binding.option3Input.getText().toString().trim();
        String option4 = binding.option4Input.getText().toString().trim();

        if (questionText.isEmpty() || option1.isEmpty() || option2.isEmpty() || 
            option3.isEmpty() || option4.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedPosition = binding.categorySpinner.getSelectedItemPosition();
        if (selectedPosition < 0 || selectedPosition >= categories.size()) {
            Toast.makeText(this, "Vui lòng chọn danh mục!", Toast.LENGTH_SHORT).show();
            return;
        }

        int answerPosition = binding.answerSpinner.getSelectedItemPosition();
        if (answerPosition < 0 || answerPosition >= 4) {
            Toast.makeText(this, "Vui lòng chọn đáp án đúng!", Toast.LENGTH_SHORT).show();
            return;
        }

        CategoryModel selectedCategory = categories.get(selectedPosition);
        
        // Get selected answer
        String answer;
        switch (answerPosition) {
            case 0:
                answer = option1;
                break;
            case 1:
                answer = option2;
                break;
            case 2:
                answer = option3;
                break;
            case 3:
                answer = option4;
                break;
            default:
                answer = option1;
                break;
        }
        
        currentQuestion.setQuestion(questionText);
        currentQuestion.setOption1(option1);
        currentQuestion.setOption2(option2);
        currentQuestion.setOption3(option3);
        currentQuestion.setOption4(option4);
        currentQuestion.setAnswer(answer);
        currentQuestion.setCategoryId(selectedCategory.getCategoryId());

        boolean result = databaseHelper.updateQuestion(currentQuestion);
        if (result) {
            Toast.makeText(this, "Cập nhật câu hỏi thành công!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Cập nhật câu hỏi thất bại!", Toast.LENGTH_SHORT).show();
        }
    }
} 