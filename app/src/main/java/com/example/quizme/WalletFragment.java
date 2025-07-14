package com.example.quizme;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.quizme.databinding.FragmentWalletBinding;

public class WalletFragment extends Fragment {

    public WalletFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    FragmentWalletBinding binding;
    DatabaseHelper databaseHelper;
    User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentWalletBinding.inflate(inflater, container, false);
        databaseHelper = new DatabaseHelper(getContext());

        SharedPreferences prefs = getActivity().getSharedPreferences("QuizApp", getActivity().MODE_PRIVATE);
        String userRole = prefs.getString("user_role", "user");
        if ("admin".equals(userRole)) {
            Toast.makeText(getContext(), "Admin không có quyền truy cập ví!", Toast.LENGTH_SHORT).show();
            getActivity().getSupportFragmentManager().popBackStack();
            return binding.getRoot();
        }

        // Lấy thông tin user từ SharedPreferences
        int userId = prefs.getInt("user_id", -1);

        if (userId != -1) {
            user = databaseHelper.getUserById(userId);
            if (user != null) {
                binding.currentCoins.setText(String.valueOf(user.getCoins()));
            }
        }

        binding.sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null && user.getCoins() > 50000) {
                    String payPal = binding.emailBox.getText().toString().trim();
                    
                    if (payPal.isEmpty()) {
                        Toast.makeText(getContext(), "Vui lòng nhập địa chỉ PayPal!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    WithdrawRequest request = new WithdrawRequest(userId, payPal, user.getName());
                    long requestId = databaseHelper.createWithdrawRequest(request);
                    
                    if (requestId != -1) {
                        Toast.makeText(getContext(), "Yêu cầu rút tiền đã được gửi thành công.", Toast.LENGTH_SHORT).show();
                        binding.emailBox.setText("");
                    } else {
                        Toast.makeText(getContext(), "Có lỗi xảy ra khi gửi yêu cầu.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Bạn cần có ít nhất 50,000 xu để rút tiền.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return binding.getRoot();
    }
}