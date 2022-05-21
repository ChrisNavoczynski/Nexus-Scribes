package com.example.nexus_scribes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.nexus_scribes.adapters.AuthorAdapter;
import com.example.nexus_scribes.databinding.AuthorGalleryBinding;
import com.example.nexus_scribes.utilities.Constants;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


import java.util.ArrayList;


public class AuthorGallery extends AppCompatActivity implements AuthorAdapter.SelectAuthor {

    private AuthorGalleryBinding binding;
    RecyclerView recyclerView;
    ArrayList<UploadUser> userArrayList;
    AuthorAdapter authorAdapter;
    FirebaseFirestore fb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AuthorGalleryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerView = findViewById(R.id.author_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fb = FirebaseFirestore.getInstance();
        userArrayList = new ArrayList<>();
        authorAdapter = new AuthorAdapter(AuthorGallery.this, userArrayList, this);
        recyclerView.setAdapter(authorAdapter);
        EventChangeListener();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void EventChangeListener() {
        fb.collection(Constants.KEY_COLLECTIONS_USERS).orderBy(Constants.KEY_FULL_NAME,
                Query.Direction.ASCENDING)
            .addSnapshotListener((value, error) -> {

                if (error != null) {
                    Log.e("Firestore Error", error.getMessage());
                    return;
                }

                assert value != null;
                for (DocumentChange dc : value.getDocumentChanges()) {

                    if (dc.getType() == DocumentChange.Type.ADDED){
                        userArrayList.add(dc.getDocument().toObject(UploadUser.class));
                    }
                    authorAdapter.notifyDataSetChanged();
                }

            });
    }

    @Override
    public void selectAuthor(UploadUser uploadUser) {
        startActivity(new Intent(AuthorGallery.this,
                AuthorDetail.class).putExtra("data", uploadUser));
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
        recreate();
    }

    public void ClickChat(View view) {
        HomePage.redirectActivity(this, ChatForum.class);
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