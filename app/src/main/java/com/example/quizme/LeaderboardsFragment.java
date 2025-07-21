package com.example.quizme;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.quizme.databinding.FragmentLeaderboardsBinding;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardsFragment extends Fragment {

    public LeaderboardsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    FragmentLeaderboardsBinding binding;
    private ArrayList<User> allUsers;
    private ArrayList<User> filteredUsers;
    private LeaderboardsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLeaderboardsBinding.inflate(inflater, container, false);

        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());

        allUsers = new ArrayList<>();
        filteredUsers = new ArrayList<>();
        adapter = new LeaderboardsAdapter(getContext(), filteredUsers, allUsers);

        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Lấy danh sách users từ SQLite (loại bỏ admin)
        List<User> userList = databaseHelper.getAllUsersForLeaderboard();
        for (User user : userList) {
            if (!"admin".equals(user.getRole())) {
                allUsers.add(user);
            }
        }
        
        filteredUsers.clear();
        filteredUsers.addAll(allUsers);
        adapter.notifyDataSetChanged();

        setupSearchFunctionality();

        return binding.getRoot();
    }

    private void setupSearchFunctionality() {
        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
                
                // Show/hide clear button
                if (s.length() > 0) {
                    binding.clearSearchBtn.setVisibility(View.VISIBLE);
                } else {
                    binding.clearSearchBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.clearSearchBtn.setOnClickListener(v -> {
            binding.searchEditText.setText("");
            binding.clearSearchBtn.setVisibility(View.GONE);
        });
    }

    private void filterUsers(String query) {
        filteredUsers.clear();
        
        if (query.isEmpty()) {
            filteredUsers.addAll(allUsers);
        } else {
            String lowercaseQuery = query.toLowerCase().trim();
            for (User user : allUsers) {
                String userName = user.getName() != null ? user.getName().toLowerCase() : "";
                String userEmail = user.getEmail() != null ? user.getEmail().toLowerCase() : "";
                
                if (userName.contains(lowercaseQuery) || userEmail.contains(lowercaseQuery)) {
                    filteredUsers.add(user);
                }
            }
        }
        
        adapter.notifyDataSetChanged();
    }
}