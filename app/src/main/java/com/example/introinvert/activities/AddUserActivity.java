package com.example.introinvert.activities;


import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.introinvert.adapters.AddUserAdapter;

import com.example.introinvert.databinding.ActivityAddUserBinding;
import com.example.introinvert.listeners.UserListener;
import com.example.introinvert.models.Users;
import com.example.introinvert.utilities.Constants;
import com.example.introinvert.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddUserActivity extends BaseActivity implements UserListener {

    private ActivityAddUserBinding binding;
    private PreferenceManager preferenceManager;
    private List<Users> alreadyInChat;
    private List<Users> newUsersInChat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        this.preferenceManager = new PreferenceManager(getApplicationContext());
        getUsersAlreadyInChat();
        setListeners();
        getUsers();
    }

    private void getUsers() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Users> users = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (currentUserId.equals(queryDocumentSnapshot.getId())) {
                                continue;
                            }
                            Users user = new Users();
                            user.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                            user.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                            user.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                            user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                            user.id = queryDocumentSnapshot.getId();
                            users.add(user);
                        }

                        if (users.size() > 0) {
                            AddUserAdapter addUserAdapter = new AddUserAdapter(users, this.alreadyInChat, this);
                            binding.addUsersRecyclerView.setAdapter(addUserAdapter);
                            binding.addUsersRecyclerView.setVisibility(View.VISIBLE);
                        } else {
                            showErrorMessage();
                        }
                    } else {
                        showErrorMessage();
                    }
                });
    }

    private void showErrorMessage() {
        binding.textErrorMessage.setText(String.format("%s", "No user available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.saveChanges.setOnClickListener(v -> updateChatRoom());
    }

    //EFFECTS: Update the Conversation on firebase with the new checked users
    private void updateChatRoom() {
        if (newUsersInChat != null && newUsersInChat.size() > 0) {
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            Map<String, Object> chatRoom = new HashMap<>();
            chatRoom.put(Constants.KEY_LAST_MESSAGE, "Be First To Chat");
            chatRoom.put(Constants.KEY_TIMESTAMP, new Date());

            //Tandem List
            List<String> idList = new ArrayList<>();
            List<String> nameList = new ArrayList<>();
            List<String> imageList = new ArrayList<>();
            for (Users user : newUsersInChat) {
                idList.add(user.id);
                nameList.add(user.name);
                imageList.add(user.image);
            }
            chatRoom.put(Constants.KEY_ID_LIST, idList);
            chatRoom.put(Constants.KEY_NAME_LIST, nameList);
            chatRoom.put(Constants.KEY_IMAGE_LIST, imageList);
            database.collection(Constants.KEY_COLLECTION_CHATROOMS).add(chatRoom)
                    .addOnSuccessListener(documentReference -> {
                        showToast("New ChatRoom Created");
                    })
                    .addOnFailureListener(e -> showToast(e.getMessage()));
        } else if (newUsersInChat.size() == 0) {
            showToast("No user selected");
        } else if (newUsersInChat == alreadyInChat) {
            showToast("No changes made");
        }

    }

    //EFFECTS: Show Toast from String Message
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    //TODO: Get Users already in Chat
    private void getUsersAlreadyInChat() {
        Users user  = (Users) getIntent().getSerializableExtra(Constants.KEY_USER);
        this.alreadyInChat = new ArrayList<>();
        this.alreadyInChat.add(user);
        this.newUsersInChat = new ArrayList<>(this.alreadyInChat);
        showToast(String.valueOf(alreadyInChat.size()));
        showToast(String.valueOf(newUsersInChat.size()));
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onUserClicked(Users user) {
        if (this.newUsersInChat.contains(user)) {
            this.newUsersInChat.remove(user);
            showToast("User Removed");
        } else {
            this.newUsersInChat.add(user);
            showToast("User Added");
        }
    }

}
