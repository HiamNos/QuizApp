package com.example.quizme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import de.hdodenhof.circleimageview.CircleImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment {

    private RecyclerView friendsRecyclerView;
    private RecyclerView searchRecyclerView;
    private RecyclerView requestsRecyclerView;
    private CircleImageView currentUserAvatar;
    private TextView currentUserName;
    private TextView currentUserEmail;
    private EditText searchEditText;
    private TextView friendsTab, searchTab, requestsTab;
    
    private DatabaseHelper databaseHelper;
    private FriendsListAdapter friendsAdapter;
    private SearchUserAdapter searchAdapter;
    private FriendRequestAdapter requestsAdapter;
    private List<Friend> friends;
    private List<User> searchResults;
    private int currentUserId;
    private int currentTab = 0; // 0: friends, 1: search, 2: requests

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = new DatabaseHelper(getContext());
        friends = new ArrayList<>();
        searchResults = new ArrayList<>();

        // Get current user ID
        SharedPreferences prefs = getActivity().getSharedPreferences("QuizApp", getActivity().MODE_PRIVATE);
        currentUserId = prefs.getInt("user_id", -1);

        if (currentUserId == -1) {
            Toast.makeText(getContext(), "Lỗi: Không thể xác định user", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        // Initialize views
        friendsRecyclerView = view.findViewById(R.id.friendsRecyclerView);
        searchRecyclerView = view.findViewById(R.id.searchRecyclerView);
        requestsRecyclerView = view.findViewById(R.id.requestsRecyclerView);
        currentUserAvatar = view.findViewById(R.id.currentUserAvatar);
        currentUserName = view.findViewById(R.id.currentUserName);
        currentUserEmail = view.findViewById(R.id.currentUserEmail);
        searchEditText = view.findViewById(R.id.searchEditText);
        
        // Tab buttons
        friendsTab = view.findViewById(R.id.friendsTab);
        searchTab = view.findViewById(R.id.searchTab);
        requestsTab = view.findViewById(R.id.requestsTab);

        setupUI();
        setupRecyclerViews();
        loadCurrentUserInfo();
        loadFriends();
        updateTabState();

        return view;
    }

    private void setupUI() {
        // Tab click listeners
        friendsTab.setOnClickListener(v -> {
            currentTab = 0;
            updateTabState();
            showFriendsTab();
        });

        searchTab.setOnClickListener(v -> {
            currentTab = 1;
            updateTabState();
            showSearchTab();
        });

        requestsTab.setOnClickListener(v -> {
            currentTab = 2;
            updateTabState();
            showRequestsTab();
        });

        // Search functionality
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (currentTab == 1) { // Only search when on search tab
                    searchUsers(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupRecyclerViews() {
        // Friends RecyclerView
        friendsAdapter = new FriendsListAdapter(friends, new FriendsListAdapter.OnFriendActionListener() {
            @Override
            public void onChatClick(Friend friend) {
                openChatWithFriend(friend);
            }

            @Override
            public void onViewProfileClick(Friend friend) {
                showUserProfile(friend);
            }
        });
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        friendsRecyclerView.setAdapter(friendsAdapter);

        // Search RecyclerView
        searchAdapter = new SearchUserAdapter(searchResults, new SearchUserAdapter.OnUserClickListener() {
            @Override
            public void onAddFriendClick(User user, int position) {
                sendFriendRequest(user);
            }
        }, databaseHelper, currentUserId);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchRecyclerView.setAdapter(searchAdapter);

        // Requests RecyclerView
        requestsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void updateTabState() {
        // Reset all tabs
        friendsTab.setBackgroundResource(R.color.white);
        friendsTab.setTextColor(getResources().getColor(R.color.colorPrimary));
        searchTab.setBackgroundResource(R.color.white);
        searchTab.setTextColor(getResources().getColor(R.color.colorPrimary));
        requestsTab.setBackgroundResource(R.color.white);
        requestsTab.setTextColor(getResources().getColor(R.color.colorPrimary));

        // Highlight active tab
        switch (currentTab) {
            case 0:
                friendsTab.setBackgroundResource(R.drawable.tab_selected_background);
                friendsTab.setTextColor(getResources().getColor(R.color.white));
                break;
            case 1:
                searchTab.setBackgroundResource(R.drawable.tab_selected_background);
                searchTab.setTextColor(getResources().getColor(R.color.white));
                break;
            case 2:
                requestsTab.setBackgroundResource(R.drawable.tab_selected_background);
                requestsTab.setTextColor(getResources().getColor(R.color.white));
                break;
        }
    }

    private void showFriendsTab() {
        friendsRecyclerView.setVisibility(View.VISIBLE);
        searchRecyclerView.setVisibility(View.GONE);
        requestsRecyclerView.setVisibility(View.GONE);
        loadFriends();
    }

    private void showSearchTab() {
        friendsRecyclerView.setVisibility(View.GONE);
        searchRecyclerView.setVisibility(View.VISIBLE);
        requestsRecyclerView.setVisibility(View.GONE);
        // Clear search if switching to search tab
        if (!searchEditText.getText().toString().isEmpty()) {
            searchUsers(searchEditText.getText().toString());
        }
    }

    private void showRequestsTab() {
        friendsRecyclerView.setVisibility(View.GONE);
        searchRecyclerView.setVisibility(View.GONE);
        requestsRecyclerView.setVisibility(View.VISIBLE);
        
        // Load friend requests directly in this fragment
        loadFriendRequests();
    }

    private void loadCurrentUserInfo() {
        new Thread(() -> {
            try {
                User currentUser = databaseHelper.getUserById(currentUserId);
                if (currentUser != null) {
                    getActivity().runOnUiThread(() -> {
                        currentUserName.setText(currentUser.getName());
                        currentUserEmail.setText(currentUser.getEmail());
                        loadCurrentUserAvatar(currentUser);
                    });
                }
            } catch (Exception e) {
                getActivity().runOnUiThread(() -> {
                    currentUserAvatar.setImageResource(R.drawable.avatar);
                });
            }
        }).start();
    }

    private void loadCurrentUserAvatar(User currentUser) {
        String imagePath = currentUser.getAvatarImage();
        
        if (imagePath != null && !imagePath.isEmpty()) {
            // Check if it's a drawable resource name (doesn't contain "/")
            if (!imagePath.contains("/") && !imagePath.contains("\\")) {
                // Load from drawable resource
                try {
                    int resourceId = getResources().getIdentifier(
                        imagePath, "drawable", getActivity().getPackageName());
                    if (resourceId != 0) {
                        Glide.with(this)
                                .load(resourceId)
                                .placeholder(R.drawable.avatar)
                                .error(R.drawable.avatar)
                                .into(currentUserAvatar);
                    } else {
                        // Resource doesn't exist, use default
                        currentUserAvatar.setImageResource(R.drawable.avatar);
                    }
                } catch (Exception e) {
                    currentUserAvatar.setImageResource(R.drawable.avatar);
                }
            } else {
                // Load from file path
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    Glide.with(this)
                            .load(imageFile)
                            .placeholder(R.drawable.avatar)
                            .error(R.drawable.avatar)
                            .into(currentUserAvatar);
                } else {
                    currentUserAvatar.setImageResource(R.drawable.avatar);
                }
            }
        } else {
            // Use default avatar if no image path
            currentUserAvatar.setImageResource(R.drawable.avatar);
        }
    }

    private void loadFriends() {
        new Thread(() -> {
            try {
                List<Friend> friendsList = databaseHelper.getFriends(currentUserId);
                
                getActivity().runOnUiThread(() -> {
                    friends.clear();
                    friends.addAll(friendsList);
                    friendsAdapter.notifyDataSetChanged();
                });
            } catch (Exception e) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Lỗi tải danh sách bạn bè: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void searchUsers(String query) {
        if (query.trim().isEmpty()) {
            searchResults.clear();
            searchAdapter.notifyDataSetChanged();
            return;
        }

        new Thread(() -> {
            try {
                List<User> results = databaseHelper.searchUsers(query.trim(), currentUserId);
                
                getActivity().runOnUiThread(() -> {
                    searchResults.clear();
                    searchResults.addAll(results);
                    searchAdapter.notifyDataSetChanged();
                });
            } catch (Exception e) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Lỗi tìm kiếm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void sendFriendRequest(User user) {
        new Thread(() -> {
            try {
                long result = databaseHelper.sendFriendRequest(currentUserId, user.getUserId());
                
                getActivity().runOnUiThread(() -> {
                    if (result != -1) {
                        Toast.makeText(getContext(), "Đã gửi lời mời kết bạn đến " + user.getName(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Không thể gửi lời mời kết bạn. Có thể đã tồn tại lời mời trước đó.", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Lỗi gửi lời mời: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (currentTab == 0) {
            loadFriends();
        } else if (currentTab == 2) {
            loadFriendRequests();
        }
    }

    private void loadFriendRequests() {
        new Thread(() -> {
            try {
                List<FriendRequest> requests = databaseHelper.getPendingFriendRequests(currentUserId);
                
                getActivity().runOnUiThread(() -> {
                    if (requestsAdapter == null) {
                        setupRequestsAdapter(requests);
                    } else {
                        requestsAdapter.updateRequests(requests);
                    }
                });
            } catch (Exception e) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Lỗi tải yêu cầu kết bạn: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void setupRequestsAdapter(List<FriendRequest> requests) {
        requestsAdapter = new FriendRequestAdapter(requests, new FriendRequestAdapter.OnRequestActionListener() {
            @Override
            public void onAcceptRequest(FriendRequest request, int position) {
                acceptFriendRequest(request, position);
            }

            @Override
            public void onDeclineRequest(FriendRequest request, int position) {
                declineFriendRequest(request, position);
            }
        });
        requestsRecyclerView.setAdapter(requestsAdapter);
    }

    private void acceptFriendRequest(FriendRequest request, int position) {
        new Thread(() -> {
            try {
                boolean success = databaseHelper.acceptFriendRequest(request.getRequestId());
                
                getActivity().runOnUiThread(() -> {
                    if (success) {
                        Toast.makeText(getContext(), "✅ Đã chấp nhận lời mời từ " + request.getSenderName(), Toast.LENGTH_SHORT).show();
                        requestsAdapter.removeRequest(position);
                        // Refresh friends list if we're on friends tab
                        if (currentTab == 0) {
                            loadFriends();
                        }
                    } else {
                        Toast.makeText(getContext(), "❌ Không thể chấp nhận lời mời", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void declineFriendRequest(FriendRequest request, int position) {
        new Thread(() -> {
            try {
                boolean success = databaseHelper.declineFriendRequest(request.getRequestId());
                
                getActivity().runOnUiThread(() -> {
                    if (success) {
                        Toast.makeText(getContext(), "❌ Đã từ chối lời mời từ " + request.getSenderName(), Toast.LENGTH_SHORT).show();
                        requestsAdapter.removeRequest(position);
                    } else {
                        Toast.makeText(getContext(), "❌ Không thể từ chối lời mời", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void openChatWithFriend(Friend friend) {
        System.out.println("FriendsFragment: Opening chat with friend: " + friend.getFriendName());
        
        Intent intent = new Intent(getContext(), ChatActivity.class);
        
        // Determine friend's actual user ID
        int friendUserId = determineFriendUserId(friend);
        
        System.out.println("FriendsFragment: Current user ID: " + currentUserId);
        System.out.println("FriendsFragment: Friend user ID: " + friendUserId);
        System.out.println("FriendsFragment: Friend name: " + friend.getFriendName());
        System.out.println("FriendsFragment: Friend avatar: " + friend.getFriendAvatar());
        
        intent.putExtra("friend_user_id", friendUserId);
        intent.putExtra("friend_name", friend.getFriendName());
        intent.putExtra("friend_avatar", friend.getFriendAvatar());
        
        try {
            startActivity(intent);
            System.out.println("FriendsFragment: ChatActivity started successfully");
        } catch (Exception e) {
            System.out.println("FriendsFragment: Error starting ChatActivity: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(getContext(), "Lỗi: Không thể mở chat", Toast.LENGTH_SHORT).show();
        }
    }
    
    private int determineFriendUserId(Friend friend) {
        // Determine which user ID is the actual friend (not the current user)
        if (friend.getUser1Id() == currentUserId) {
            return friend.getUser2Id(); // Current user is user1, so friend is user2
        } else {
            return friend.getUser1Id(); // Current user is user2, so friend is user1
        }
    }

    private void showUserProfile(Friend friend) {
        UserProfileModal.showUserProfile(getContext(), friend, currentUserId, new UserProfileModal.OnProfileActionListener() {
            @Override
            public void onSendMessage(Friend friend) {
                // Handle send message action
                Toast.makeText(getContext(), "Gửi tin nhắn tới " + friend.getFriendName(), Toast.LENGTH_SHORT).show();
                // TODO: Navigate to chat with this friend
            }
        });
    }
}
