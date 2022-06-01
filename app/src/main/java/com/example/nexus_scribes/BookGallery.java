package com.example.nexus_scribes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.nexus_scribes.adapters.BookAdapter;
import com.example.nexus_scribes.databinding.BookGalleryBinding;
import com.example.nexus_scribes.firestoreData.UploadBook;
import com.example.nexus_scribes.utilities.Constants;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class BookGallery extends AppCompatActivity implements BookAdapter.SelectBook {

    private BookGalleryBinding binding;
    RecyclerView recyclerView;
    ArrayList<UploadBook> bookArrayList;
    BookAdapter bookAdapter;
    FirebaseFirestore fb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = BookGalleryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerView = findViewById(R.id.book_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fb = FirebaseFirestore.getInstance();
        bookArrayList = new ArrayList<>();
        bookAdapter = new BookAdapter(BookGallery.this, bookArrayList, this);
        recyclerView.setAdapter(bookAdapter);
        EventChangeListener();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void EventChangeListener() {
        fb.collection(Constants.KEY_COLLECTIONS_BOOKS).orderBy(Constants.KEY_BOOK_TITLE,
                Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {

                    if (error != null) {
                        Log.e("Firestore Error", error.getMessage());
                        return;
                    }

                    assert value != null;
                    for (DocumentChange dc : value.getDocumentChanges()) {

                        if (dc.getType() == DocumentChange.Type.ADDED){
                            bookArrayList.add(dc.getDocument().toObject(UploadBook.class));
                        }
                        bookAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void selectBook(UploadBook uploadBook) {
        startActivity(new Intent(BookGallery.this,
                BookDetail.class).putExtra("bookData", uploadBook));
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
        recreate();
    }

    public void ClickAuthor(View view) {
        HomePage.redirectActivity(this, AuthorGallery.class);
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