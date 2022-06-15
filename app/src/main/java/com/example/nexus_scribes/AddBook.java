package com.example.nexus_scribes;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nexus_scribes.databinding.AddBookBinding;
import com.example.nexus_scribes.firestore.UploadBook;
import com.example.nexus_scribes.firestore.UploadUser;
import com.example.nexus_scribes.utilities.Constants;
import com.example.nexus_scribes.viewModels.UserProfileViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.Objects;

public class AddBook extends AppCompatActivity {

    private AddBookBinding binding;
    private StorageReference storageReference;
    private Uri bookImage;
    private StorageTask uploadTask;
    String fullName;
    String penName;
    String imageProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = AddBookBinding.inflate(getLayoutInflater());
        storageReference = FirebaseStorage.getInstance().getReference(Constants.KEY_COLLECTIONS_BOOKS);
        setContentView(binding.getRoot());

        setListeners();
    }

    private void addNewBook() {
            FirebaseAuth myAuth = FirebaseAuth.getInstance();
            FirebaseFirestore fb = FirebaseFirestore.getInstance();
            String userId = Objects.requireNonNull(myAuth.getCurrentUser()).getUid();
            UserProfileViewModel viewModel = new UserProfileViewModel();
            DocumentReference documentReference = fb
                    .collection(Constants.KEY_COLLECTIONS_USERS)
                    .document(userId)
                    .collection(Constants.KEY_COLLECTIONS_BOOKS)
                    .document();
            DocumentReference documentReference2 = fb
                    .collection(Constants.KEY_COLLECTION_ALL_BOOKS)
                    .document();
            String bookId = documentReference.getId();
            viewModel.getProfileInfo(
                (UploadUser profileInfo) -> {
                    fullName = profileInfo.getFullName();
                    penName = profileInfo.getPenName();
                    imageProfile = profileInfo.getImageProfile();
                });
            StorageReference fileReference = storageReference
                    .child(binding.etBookTitle.getText().toString()
                    + "BookPic" + "." + getFileExtension(bookImage));
            uploadTask = fileReference.putFile(bookImage)
                    .addOnSuccessListener(taskSnapshot ->
                            storageReference.child(binding.etBookTitle.getText().toString()
                                    + "BookPic" + "." + getFileExtension(bookImage))
                                    .getDownloadUrl().addOnCompleteListener(task -> {
                                        Uri downloadUri = task.getResult();
                                        UploadBook uploadBook =
                                                new UploadBook(
                                                        bookId.trim(),
                                                        fullName,
                                                        penName,
                                                        downloadUri.toString(),
                                                        binding.etBookTitle.getText().toString(),
                                                        binding.etBookSynopsis.getText().toString(),
                                                        imageProfile
                                                );
                                        documentReference.set(uploadBook);
                                        documentReference2.set(uploadBook);
                                    }));
            startActivity(new Intent(getApplicationContext(),
                    UserProfile.class));
            showToast("New publication added");
    }

    private void setListeners() {
        binding.bookImage.setOnClickListener(view ->
                pickImage.launch("image/*"));

        binding.bookPublishButton.setOnClickListener(view -> {
            if (isValidBook()) {
                addNewBook();
            }
        });
    }

    private Boolean isValidBook() {
        if (binding.etBookTitle.length() == 0) {
            binding.bookTitleError.setVisibility(View.VISIBLE);
            return false;
        }
        else if (binding.etBookSynopsis.length() == 0) {
            binding.bookSynopsisError.setVisibility(View.VISIBLE);
            return false;
        }
        else {
            binding.bookImageError.setVisibility(View.GONE);
            binding.bookTitleError.setVisibility(View.GONE);
            binding.bookSynopsisError.setVisibility(View.GONE);
            return true;
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mine = MimeTypeMap.getSingleton();
        return mine.getExtensionFromMimeType(cR.getType(uri));
    }

    ActivityResultLauncher<String> pickImage = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if (result != null) {
                        binding.textAddBookImage.setVisibility(View.GONE);
                        binding.bookImage.setImageURI(result);
                        bookImage = result;
                    }
                }
            });

    private void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        View view = toast.getView();
        view.setBackgroundResource(R.drawable.toast_background);
        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(Color.parseColor("#FFFFFF"));
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

}
