package com.example.quizme;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizme.databinding.ActivityQuizBinding;

import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    ActivityQuizBinding binding;

    ArrayList<Question> questions;
    int index = 0;
    Question question;
    CountDownTimer timer;
    DatabaseHelper databaseHelper;
    int correctAnswers = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        questions = new ArrayList<>();
        databaseHelper = new DatabaseHelper(this);

        final int catId = getIntent().getIntExtra("catId", -1);
        System.out.println("QuizActivity: Received catId = " + catId);

        if (catId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy danh mục!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        try {
            // Lấy questions từ SQLite
            List<Question> questionList = databaseHelper.getQuestionsByCategory(catId, 5);
            System.out.println("QuizActivity: Loaded " + questionList.size() + " questions for category " + catId);
            
            questions.clear();
            questions.addAll(questionList);

            if (questions.isEmpty()) {
                Toast.makeText(this, "Không có câu hỏi nào trong danh mục này!", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            resetTimer();
            setNextQuestion();
        } catch (Exception e) {
            System.out.println("QuizActivity: Error loading questions: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi tải câu hỏi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    void resetTimer() {
        timer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.timer.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                // Hết thời gian, chuyển sang câu hỏi tiếp theo
                if (index < questions.size() - 1) {
                    index++;
                    setNextQuestion();
                } else {
                    // Kết thúc quiz
                    Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
                    intent.putExtra("correct", correctAnswers);
                    intent.putExtra("total", questions.size());
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    void showAnswer() {
        if (question.getAnswer().equals(binding.option1.getText().toString()))
            binding.option1.setBackground(getResources().getDrawable(R.drawable.option_right));
        else if (question.getAnswer().equals(binding.option2.getText().toString()))
            binding.option2.setBackground(getResources().getDrawable(R.drawable.option_right));
        else if (question.getAnswer().equals(binding.option3.getText().toString()))
            binding.option3.setBackground(getResources().getDrawable(R.drawable.option_right));
        else if (question.getAnswer().equals(binding.option4.getText().toString()))
            binding.option4.setBackground(getResources().getDrawable(R.drawable.option_right));
    }

    void setNextQuestion() {
        try {
            if (timer != null)
                timer.cancel();

            if (timer != null) {
                timer.start();
            }
            
            if (index < questions.size()) {
                binding.questionCounter.setText(String.format("%d/%d", (index + 1), questions.size()));
                question = questions.get(index);
                binding.question.setText(question.getQuestion());
                binding.option1.setText(question.getOption1());
                binding.option2.setText(question.getOption2());
                binding.option3.setText(question.getOption3());
                binding.option4.setText(question.getOption4());
            }
        } catch (Exception e) {
            System.out.println("QuizActivity: Error in setNextQuestion: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi hiển thị câu hỏi!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    void checkAnswer(TextView textView) {
        try {
            String selectedAnswer = textView.getText().toString();
            if (selectedAnswer.equals(question.getAnswer())) {
                correctAnswers++;
                textView.setBackground(getResources().getDrawable(R.drawable.option_right));
            } else {
                showAnswer();
                textView.setBackground(getResources().getDrawable(R.drawable.option_wrong));
            }
        } catch (Exception e) {
            System.out.println("QuizActivity: Error in checkAnswer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    void reset() {
        binding.option1.setBackground(getResources().getDrawable(R.drawable.option_unselected));
        binding.option2.setBackground(getResources().getDrawable(R.drawable.option_unselected));
        binding.option3.setBackground(getResources().getDrawable(R.drawable.option_unselected));
        binding.option4.setBackground(getResources().getDrawable(R.drawable.option_unselected));
    }

    public void onClick(View view) {
        int viewId = view.getId();
        
        if (viewId == R.id.option_1 || viewId == R.id.option_2 || 
            viewId == R.id.option_3 || viewId == R.id.option_4) {
            if (timer != null)
                timer.cancel();
            TextView selected = (TextView) view;
            checkAnswer(selected);
        } else if (viewId == R.id.nextBtn) {
            reset();
            if (index < questions.size()) {
                index++;
                setNextQuestion();
            }
            if (index == questions.size()) {
                Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
                intent.putExtra("correct", correctAnswers);
                intent.putExtra("total", questions.size());
                startActivity(intent);
            }
        } else if (viewId == R.id.quizBtn) {
            Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
            intent.putExtra("correct", correctAnswers);
            intent.putExtra("total", questions.size());
            startActivity(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}