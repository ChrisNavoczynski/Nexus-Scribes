package com.example.nexus_scribes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.nexus_scribes.databinding.HomePageBinding;

public class HomePage extends AppCompatActivity {

    private HomePageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Home Page");
        binding = HomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    public void ClickMenu(View view) {
        openDrawer(binding.drawerLayout);
    }

    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void ClickLogo(View view) {
        closeDrawer(binding.drawerLayout);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void ClickHome(View view) {
        recreate();
    }

    public void ClickProfile(View view) {
        redirectActivity(this, UserProfile.class);
    }

    public void ClickBook(View view) {
        redirectActivity(this, BookGallery.class);
    }

    public void ClickAuthor(View view) {
        redirectActivity(this, AuthorGallery.class);
    }

    public void ClickChat(View view) {
        redirectActivity(this, ChatForum.class);
    }

    public void ClickSettings(View view) {
        redirectActivity(this, UserSettings.class);
    }

    public void ClickLogout(View view) {
        logout(this);
    }

    public static void logout(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to Logout?");
        builder.setPositiveButton("YES", (dialog, which) -> {
           activity.finishAffinity();
           System.exit(0);
        });
        builder.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    public static void redirectActivity(Activity activity, Class gotoClass) {
        Intent intent = new Intent(activity, gotoClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(binding.drawerLayout);
    }
}
