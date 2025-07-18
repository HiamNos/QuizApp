package com.example.quizme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    DatabaseHelper databaseHelper;
    
    // Views
    private CircleImageView profileImage;
    private EditText nameBox, emailBox, phoneBox;
    private LinearLayout changeAvatarBtn, changePasswordBtn, updateInfoBtn;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        databaseHelper = new DatabaseHelper(getContext());
        
        initViews(view);
        setupClickListeners();
        loadUserData();

        return view;
    }
    
    private void initViews(View view) {
        profileImage = view.findViewById(R.id.profileImage);
        nameBox = view.findViewById(R.id.nameBox);
        emailBox = view.findViewById(R.id.emailBox);
        phoneBox = view.findViewById(R.id.phoneBox);
        changeAvatarBtn = view.findViewById(R.id.changeAvatarBtn);
        changePasswordBtn = view.findViewById(R.id.changePasswordBtn);
        updateInfoBtn = view.findViewById(R.id.updateInfoBtn);
    }
    
    private void setupClickListeners() {
        changeAvatarBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChangeAvatarActivity.class);
            startActivity(intent);
        });
        
        changePasswordBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            startActivity(intent);
        });
        
        updateInfoBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UpdateInfoActivity.class);
            startActivity(intent);
        });
    }
    
    private void loadUserData() {
        // Lấy thông tin user từ SharedPreferences
        SharedPreferences prefs = getActivity().getSharedPreferences("QuizApp", getActivity().MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        String userRole = prefs.getString("user_role", "user");

        if (userId != -1) {
            User user = databaseHelper.getUserById(userId);
            if (user != null) {
                nameBox.setText(user.getName());
                emailBox.setText(user.getEmail());
                phoneBox.setText(user.getPhone() != null ? user.getPhone() : "");
                
                // Load avatar image
                loadAvatarImage(user.getAvatarImage());
            }
        }
        
        // Disable editing for display purposes - users should use specific activities to edit
        nameBox.setEnabled(false);
        emailBox.setEnabled(false);
        phoneBox.setEnabled(false);
    }
    
    private void loadAvatarImage(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Glide.with(this)
                        .load(imageFile)
                        .placeholder(R.drawable.avatar)
                        .error(R.drawable.avatar)
                        .into(profileImage);
            } else {
                profileImage.setImageResource(R.drawable.avatar);
            }
        } else {
            profileImage.setImageResource(R.drawable.avatar);
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Reload user data when returning to this fragment
        loadUserData();
    }
}