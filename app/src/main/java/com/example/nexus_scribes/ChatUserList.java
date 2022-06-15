package com.example.nexus_scribes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.nexus_scribes.adapters.UsersAdapter;
import com.example.nexus_scribes.databinding.ChatUserListBinding;

import com.example.nexus_scribes.listeners.UserListener;
import com.example.nexus_scribes.models.ChatUser;
import com.example.nexus_scribes.utilities.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatUserList extends AppCompatActivity implements UserListener {

    private ChatUserListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ChatUserListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        getUsers();
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }

    private void getUsers() {
        loading(true);
        FirebaseAuth myAuth = FirebaseAuth.getInstance();
        FirebaseFirestore fb = FirebaseFirestore.getInstance();
        fb.collection(Constants.KEY_COLLECTIONS_USERS).orderBy(Constants.KEY_FULL_NAME,
                Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                   loading(false);
                   String currentUserId = Objects.requireNonNull(myAuth.getCurrentUser()).getUid();
                   if (task.isSuccessful() && task.getResult() != null) {
                       List<ChatUser> chatUsers = new ArrayList<>();
                       for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                           if (currentUserId.equals(queryDocumentSnapshot.getId())) {
                               continue;
                           }
                           ChatUser chatUser = new ChatUser();
                           chatUser.fullName = queryDocumentSnapshot.getString(Constants.KEY_FULL_NAME);
                           chatUser.penName = queryDocumentSnapshot.getString(Constants.KEY_PEN_NAME);
                           chatUser.imageProfile = queryDocumentSnapshot.getString(Constants.KEY_USER_IMAGE);
                           chatUser.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                           chatUser.id = queryDocumentSnapshot.getId();
                           if (chatUser.penName.equals("")) {
                               chatUser.name = chatUser.fullName;
                           } else {
                               chatUser.name = chatUser.penName;
                           }
                           chatUsers.add(chatUser);
                       }
                       if(chatUsers.size() > 0) {
                           UsersAdapter usersAdapter = new UsersAdapter(chatUsers, this);
                           binding.usersChatRecyclerView.setAdapter(usersAdapter);
                           binding.usersChatRecyclerView.setVisibility(View.VISIBLE);
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


    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onUserClicked(ChatUser chatUser) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, chatUser);
        startActivity(intent);
        finish();
    }
}