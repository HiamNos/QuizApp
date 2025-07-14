package com.example.quizme;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.quizme.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {
    DatabaseHelper databaseHelper;
    FragmentProfileBinding binding;

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
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        databaseHelper = new DatabaseHelper(getContext());

        // Lấy thông tin user từ SharedPreferences
        SharedPreferences prefs = getActivity().getSharedPreferences("QuizApp", getActivity().MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        String userRole = prefs.getString("user_role", "user");

        if ("admin".equals(userRole)) {
            // Disable các trường chỉnh sửa
            binding.nameBox.setEnabled(false);
            binding.emailBox.setEnabled(false);
            binding.passBox.setEnabled(false);
            binding.updateBtn.setVisibility(View.GONE); // Ẩn nút cập nhật
        }

        if (userId != -1) {
            User user = databaseHelper.getUserById(userId);
            if (user != null) {
                binding.nameBox.setHint(user.getName());
                binding.emailBox.setHint(user.getEmail());
                binding.passBox.setHint("Nhập mật khẩu mới");
            }
        }

        binding.updateBtn.setOnClickListener(view -> {
            String name = binding.nameBox.getText().toString().trim();
            String email = binding.emailBox.getText().toString().trim();
            String password = binding.passBox.getText().toString().trim();

            if (name.isEmpty() && email.isEmpty() && password.isEmpty()) {
                Toast.makeText(getActivity(), "Vui lòng nhập thông tin cần cập nhật!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (userId != -1) {
                User user = databaseHelper.getUserById(userId);
                if (user != null) {
                    if (!name.isEmpty()) user.setName(name);
                    if (!email.isEmpty()) user.setEmail(email);
                    if (!password.isEmpty()) user.setPassword(password);

                    databaseHelper.updateUser(user);

                    // Cập nhật SharedPreferences
                    SharedPreferences.Editor editor = prefs.edit();
                    if (!name.isEmpty()) editor.putString("user_name", name);
                    if (!email.isEmpty()) editor.putString("user_email", email);
                    editor.apply();

                    Toast.makeText(getActivity(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    
                    // Xóa text trong các input
                    binding.nameBox.setText("");
                    binding.emailBox.setText("");
                    binding.passBox.setText("");
                }
            }
        });

        return binding.getRoot();
    }
}