package com.example.quizme;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class AdminHomeFragment extends Fragment {

    private TextView totalUsersText, totalQuestionsText, totalCategoriesText;
    private Button manageQuestionsBtn, manageUsersBtn, manageCategoriesBtn;
    private DatabaseHelper databaseHelper;

    public AdminHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = new DatabaseHelper(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        // Initialize views
        totalUsersText = view.findViewById(R.id.totalUsersText);
        totalQuestionsText = view.findViewById(R.id.totalQuestionsText);
        totalCategoriesText = view.findViewById(R.id.totalCategoriesText);
        
        manageQuestionsBtn = view.findViewById(R.id.manageQuestionsBtn);
        manageUsersBtn = view.findViewById(R.id.manageUsersBtn);
        manageCategoriesBtn = view.findViewById(R.id.manageCategoriesBtn);

        setupUI();
        loadStats();

        return view;
    }

    private void setupUI() {
        manageQuestionsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AdminActivity.class);
            startActivity(intent);
        });

        manageUsersBtn.setOnClickListener(v -> {
            // TODO: Implement user management
            // Intent intent = new Intent(getActivity(), ManageUsersActivity.class);
            // startActivity(intent);
        });

        manageCategoriesBtn.setOnClickListener(v -> {
            // TODO: Implement category management
            // Intent intent = new Intent(getActivity(), ManageCategoriesActivity.class);
            // startActivity(intent);
        });
    }

    private void loadStats() {
        new Thread(() -> {
            try {
                int totalUsers = databaseHelper.getTotalUsersCount();
                int totalQuestions = databaseHelper.getTotalQuestionsCount();
                int totalCategories = databaseHelper.getTotalCategoriesCount();

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        totalUsersText.setText(String.valueOf(totalUsers));
                        totalQuestionsText.setText(String.valueOf(totalQuestions));
                        totalCategoriesText.setText(String.valueOf(totalCategories));
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStats(); // Refresh stats when returning to this fragment
    }
}