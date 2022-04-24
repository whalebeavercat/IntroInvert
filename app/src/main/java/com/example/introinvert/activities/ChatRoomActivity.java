package com.example.introinvert.activities;

import android.os.Bundle;

import com.example.introinvert.databinding.ActivityChatBinding;
import com.example.introinvert.models.Users;

import java.util.List;

public class ChatRoomActivity extends ChatActivity{

    private List<Users> usersList;
    private String chatroomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
