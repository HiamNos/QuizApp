package com.example.quizme;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView emptyTextView, totalQuizzesText, avgScoreText, bestScoreText, accuracyText;
    private DatabaseHelper databaseHelper;
    private QuizResultsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        initViews(view);
        loadData();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        emptyTextView = view.findViewById(R.id.emptyTextView);
        totalQuizzesText = view.findViewById(R.id.totalQuizzesText);
        avgScoreText = view.findViewById(R.id.avgScoreText);
        bestScoreText = view.findViewById(R.id.bestScoreText);
        accuracyText = view.findViewById(R.id.accuracyText);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        databaseHelper = new DatabaseHelper(getContext());
        
        // DEBUG: Kiểm tra xem table quiz_results có tồn tại không
        try {
            databaseHelper.debugAllQuizResults();
        } catch (Exception e) {
            System.out.println("HistoryFragment: Error accessing quiz_results table, might need to recreate database");
            System.out.println("HistoryFragment: Error details: " + e.getMessage());
        }
    }

    private void loadData() {
        SharedPreferences prefs = getActivity().getSharedPreferences("QuizApp", getActivity().MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        
        System.out.println("HistoryFragment: Loading data for userId: " + userId);
        
        // Debug: in tất cả quiz results
        databaseHelper.debugAllQuizResults();
        
        if (userId == -1) {
            System.out.println("HistoryFragment: No userId found in preferences");
            emptyTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            return;
        }
        
        // Load quiz results
        List<QuizResult> results = databaseHelper.getQuizResultsByUser(userId);
        
        System.out.println("HistoryFragment: Found " + results.size() + " quiz results");
        
        if (results.isEmpty()) {
            System.out.println("HistoryFragment: No quiz results found");
            emptyTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            System.out.println("HistoryFragment: Displaying quiz results");
            emptyTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            
            adapter = new QuizResultsAdapter(getContext(), results);
            recyclerView.setAdapter(adapter);
        }

        // Load statistics
        QuizStats stats = databaseHelper.getQuizStats(userId);
        updateStatsUI(stats);
    }

    private void updateStatsUI(QuizStats stats) {
        totalQuizzesText.setText("Tổng số bài: " + stats.getTotalQuizzes());
        avgScoreText.setText("Điểm TB: " + String.format("%.1f", stats.getAverageScore()));
        bestScoreText.setText("Điểm cao nhất: " + stats.getBestScore());
        accuracyText.setText("Độ chính xác: " + String.format("%.1f%%", stats.getOverallAccuracy()));
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when fragment becomes visible
        if (databaseHelper != null) {
            loadData();
        }
    }
}
