package com.example.nexus_scribes;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.nexus_scribes.databinding.UserProfileBinding;
import com.example.nexus_scribes.firestore.UploadUser;
import com.example.nexus_scribes.utilities.Constants;

import com.example.nexus_scribes.viewModels.UserProfileViewModel;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;


public class UserProfile extends AppCompatActivity {

    private UserProfileBinding binding;
    FirebaseAuth myAuth;
    FirebaseFirestore fb;
    String userId;
    public ImageView image;
    StorageReference storeRef;

    private String imageProfile;
    String fullName;
    String penName;
    String userBio;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = UserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        UserProfileViewModel viewModel = new UserProfileViewModel();

        myAuth = FirebaseAuth.getInstance();
        fb = FirebaseFirestore.getInstance();
        storeRef = FirebaseStorage.getInstance()
                .getReference(String.valueOf(Constants.KEY_PROFILE_PIC));
        userId = Objects.requireNonNull(myAuth.getCurrentUser()).getUid();
        image = findViewById(R.id.user_profile);

        viewModel.getProfileInfo(
                (UploadUser profileInfo) -> {
                    fullName = profileInfo.getFullName();
                    penName = profileInfo.getPenName();
                    userBio = profileInfo.getUserBio();
                    imageProfile = profileInfo.getImageProfile();

                    Picasso.get().setLoggingEnabled(true);
                    Picasso.get().load(imageProfile)
                            .resize(300,300)
                            .centerCrop().into(binding.userProfile);
                    if (!"".equals(penName)) {
                        binding.userDisplayName.setText(penName);
                    } else {
                        binding.userDisplayName.setText(fullName);
                    }
                    binding.userDisplayBio.setText(userBio);
                }
        );
        setListeners();
    }

    private void setListeners() {
        binding.newBookBtn.setOnClickListener(view ->
                startActivity(new Intent(UserProfile.this,
                        AddBook.class)));
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
        recreate();
    }

    public void ClickBook(View view) {
        HomePage.redirectActivity(this, BookGallery.class);
    }

    public void ClickAuthor(View view) {
        HomePage.redirectActivity(this, AuthorGallery.class);
    }

    public void ClickChat(View view) {
        HomePage.redirectActivity(this, CurrentUser.class);
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