package com.example.nexus_scribes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nexus_scribes.adapters.RecentConversationAdapter;
import com.example.nexus_scribes.databinding.CurrentUserBinding;
import com.example.nexus_scribes.firestore.UploadUser;
import com.example.nexus_scribes.listeners.ConversationListener;
import com.example.nexus_scribes.models.ChatMessage;
import com.example.nexus_scribes.models.ChatUser;
import com.example.nexus_scribes.utilities.Constants;
import com.example.nexus_scribes.utilities.PreferenceManager;
import com.example.nexus_scribes.viewModels.UserProfileViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CurrentUser extends AppCompatActivity implements ConversationListener {

    private CurrentUserBinding binding;
    ImageView image;
    String penName, fullName, imageProfile;
    PreferenceManager preferenceManager;
    private List<ChatMessage> conversations;
    private RecentConversationAdapter conversationAdapter;
    private FirebaseFirestore fb;

    @Override
    protected void onCreate(Bundle savedInstanceStance) {
        super.onCreate(savedInstanceStance);
        binding = CurrentUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        image = findViewById(R.id.imageProfile);
        preferenceManager = new PreferenceManager(getApplicationContext());
        init();
        loadUserDetails();
        getToken();
        setListeners();
        listenConversations();
    }

    private void init() {
        conversations = new ArrayList<>();
        conversationAdapter = new RecentConversationAdapter(conversations, this);
        binding.conversationsRecyclerView.setAdapter(conversationAdapter);
        fb = FirebaseFirestore.getInstance();
    }

    private void setListeners() {
        binding.imageSignOut.setOnClickListener(v -> signOut());
        binding.fabNewChat.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), ChatUserList.class)));
    }

    private void loadUserDetails() {
        UserProfileViewModel viewModel = new UserProfileViewModel();
        viewModel.getProfileInfo(
                (UploadUser profileInfo) -> {
                    fullName = profileInfo.getFullName();
                    penName = profileInfo.getPenName();
                    imageProfile = profileInfo.getImageProfile();

                    Picasso.get().setLoggingEnabled(true);
                    Picasso.get().load(imageProfile)
                            .resize(30,30)
                            .centerCrop().into(binding.imageProfile);
                    if (!"".equals(penName)) {
                        binding.textName.setText(penName);
                    } else {
                        binding.textName.setText(fullName);
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void listenConversations() {
        fb.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID,
                        preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
        fb.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_RECEIVER_ID,
                        preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if(error != null) {
            return;
        }
        if(value != null) {
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if(documentChange.getType() == DocumentChange.Type.ADDED) {
                    String senderId = documentChange.getDocument()
                            .getString(Constants.KEY_SENDER_ID);
                    String receiverId = documentChange.getDocument()
                            .getString(Constants.KEY_RECEIVER_ID);
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = senderId;
                    chatMessage.receiverId = receiverId;
                    if(preferenceManager.getString(Constants.KEY_USER_ID).equals(senderId)) {
                        chatMessage.conversationImage = documentChange
                                .getDocument().getString(Constants.KEY_RECEIVER_IMAGE);
                        chatMessage.conversationName = documentChange
                                    .getDocument().getString(Constants.KEY_RECEIVER_NAME);
                        chatMessage.conversationId = documentChange
                                .getDocument().getString(Constants.KEY_RECEIVER_ID);
                    } else {
                        chatMessage.conversationImage = documentChange
                                .getDocument().getString(Constants.KEY_SENDER_IMAGE);
                        chatMessage.conversationName = documentChange
                                    .getDocument().getString(Constants.KEY_SENDER_NAME);
                        chatMessage.conversationId = documentChange
                                .getDocument().getString(Constants.KEY_SENDER_ID);
                    }
                    chatMessage.message = documentChange
                            .getDocument().getString(Constants.KEY_LAST_MESSAGE);
                    chatMessage.dateObject = documentChange
                            .getDocument().getDate(Constants.KEY_TIMESTAMP);
                    conversations.add(chatMessage);
                } else if(documentChange.getType() == DocumentChange.Type.MODIFIED) {
                    for (int i = 0; i < conversations.size(); i++) {
                        String senderId = documentChange.getDocument()
                                .getString(Constants.KEY_SENDER_ID);
                        String receiverId = documentChange.getDocument()
                                .getString(Constants.KEY_RECEIVER_ID);
                        if(conversations.get(i).senderId.equals(senderId) &&
                            conversations.get(i).receiverId.equals(receiverId)) {
                            conversations.get(i).message = documentChange.getDocument()
                                    .getString(Constants.KEY_LAST_MESSAGE);
                            conversations.get(i).dateObject = documentChange.getDocument()
                                    .getDate(Constants.KEY_TIMESTAMP);
                            break;
                        }
                    }
                }
            }
            Collections.sort(conversations, (obj1, obj2) -> obj2.dateObject
                    .compareTo(obj1.dateObject));
            conversationAdapter.notifyDataSetChanged();
            binding.conversationsRecyclerView.smoothScrollToPosition(0);
            binding.conversationsRecyclerView.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        }
    };

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token) {
        FirebaseAuth myAuth = FirebaseAuth.getInstance();
        FirebaseFirestore fb = FirebaseFirestore.getInstance();
        String userId = Objects.requireNonNull(myAuth.getCurrentUser()).getUid();
        DocumentReference documentReference =
                fb.collection(Constants.KEY_COLLECTIONS_USERS).document(userId);
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnFailureListener(e -> showToast("Unable to update token"));
    }

    private void signOut() {
        showToast("Signing out of Chat...");
        FirebaseAuth myAuth = FirebaseAuth.getInstance();
        FirebaseFirestore fb = FirebaseFirestore.getInstance();
        String userId = Objects.requireNonNull(myAuth.getCurrentUser()).getUid();
        DocumentReference documentReference =
                fb.collection(Constants.KEY_COLLECTIONS_USERS).document(userId);
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    preferenceManager.clear();
                    startActivity(new Intent(getApplicationContext(), HomePage.class));
                    finish();
                })
                .addOnFailureListener(e -> showToast("Unable to Sign out of chat"));
    }

    @Override
    public void onConversationClicked(ChatUser chatUser) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, chatUser);
        startActivity(intent);
    }


    public void ClickMenu(View view) {
        HomePage.openDrawer(binding.drawerLayout);
    }

    public void ClickLogo(View view) {
        HomePage.closeDrawer(binding.drawerLayout);
    }

    public void ClickHome(View view) {
        HomePage.redirectActivity(this, HomePage.class);
    }

    public void ClickProfile(View view) {
        HomePage.redirectActivity(this, UserProfile.class);
    }

    public void ClickBook(View view) {
        HomePage.redirectActivity(this, BookGallery.class);
    }

    public void ClickAuthor(View view) {
        HomePage.redirectActivity(this, AuthorGallery.class);
    }

    public void ClickChat(View view) {
        recreate();
    }

    public void ClickSettings(View view) {
        HomePage.redirectActivity(this, UserSettings.class);
    }

    public void ClickLogout(View view) {
        HomePage.logout(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        HomePage.closeDrawer(binding.drawerLayout);
    }
}
