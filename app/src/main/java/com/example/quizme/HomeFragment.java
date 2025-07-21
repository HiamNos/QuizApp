package com.example.quizme;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.quizme.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    DatabaseHelper databaseHelper;
    private ArrayList<CategoryModel> categories;
    private CategoryAdapter adapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        databaseHelper = new DatabaseHelper(getContext());

        categories = new ArrayList<>();
        adapter = new CategoryAdapter(getContext(), categories);

        reloadCategories();

        binding.categoryList.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.categoryList.setAdapter(adapter);

        binding.spinwheel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SpinnerActivity.class));
            }
        });

        // Friend buttons click listeners
        binding.btnSearchFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SearchFriendsActivity.class));
            }
        });

        binding.btnFriendRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), FriendRequestsActivity.class));
            }
        });

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadCategories();
    }

    private void reloadCategories() {
        try {
            List<CategoryModel> categoryList = databaseHelper.getAllCategories();
            categories.clear();
            categories.addAll(categoryList);
            if (adapter != null) adapter.notifyDataSetChanged();
        } catch (Exception e) {
            System.out.println("Error loading categories: " + e.getMessage());
            e.printStackTrace();
        }
    }
}