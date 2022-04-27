package com.example.nexus_scribes;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.nexus_scribes.databinding.BookGalleryBinding;

public class BookGallery extends AppCompatActivity {

    private BookGalleryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = BookGalleryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
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