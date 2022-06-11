package com.example.nexus_scribes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nexus_scribes.adapters.BookAdapter;
import com.example.nexus_scribes.adapters.VideoAdapter;
import com.example.nexus_scribes.databinding.HomePageBinding;
import com.example.nexus_scribes.firestoreData.UploadBook;
import com.example.nexus_scribes.models.YouTubeVideos;
import com.example.nexus_scribes.utilities.Constants;
import com.example.nexus_scribes.viewModels.BookViewModel;
import com.example.nexus_scribes.viewModels.UserProfileViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class HomePage extends AppCompatActivity implements BookAdapter.SelectBook {

    private HomePageBinding binding;
    RecyclerView recyclerView;
    Vector<YouTubeVideos> youTubeVideos = new Vector<>();
    FirebaseFirestore fb;
    FirebaseAuth myAuth;
    String fullName, penName, bookImage, bookTitle;
    ImageView ivBookCover;
    UploadBook randomBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Home Page");
        binding = HomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        myAuth = FirebaseAuth.getInstance();
        fb = FirebaseFirestore.getInstance();
        BookViewModel viewModel = new BookViewModel();
        ivBookCover = findViewById(R.id.book_cover_pic_news);

        CollectionReference bookCollectionReference = fb.collection(Constants.KEY_COLLECTION_ALL_BOOKS);
        bookCollectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<UploadBook> bookList = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        UploadBook book = document.toObject(UploadBook.class);
                        bookList.add(book);
                    }
                    randomBook = bookList.get(new Random().nextInt(bookList.size()));
                    viewModel.getBookInfo(
                            (UploadBook rBook) -> {
                                fullName = randomBook.getFullName();
                                penName = randomBook.getPenName();
                                bookImage = randomBook.getBookImage();
                                bookTitle = randomBook.getBookTitle();

                                Picasso.get().setLoggingEnabled(true);
                                Picasso.get().load(bookImage)
                                        .resize(90,110)
                                        .centerCrop().into(ivBookCover);
                                if (!"".equals(penName)) {
                                    binding.bookAuthorNameNews.setText(penName);
                                } else {
                                    binding.bookAuthorNameNews.setText(fullName);
                                }
                                binding.bookTitleNews.setText(bookTitle);
                            }
                    );
                }
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.rvVideo);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        youTubeVideos.add(new YouTubeVideos("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/NUbfLmdbU3E\" frameborder=\"0\" allowfullscreen></iframe>"));
        youTubeVideos.add(new YouTubeVideos("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/xMPPUM4UwnY\" frameborder=\"0\" allowfullscreen></iframe>"));
        youTubeVideos.add(new YouTubeVideos("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/IWYInBImLug\" frameborder=\"0\" allowfullscreen></iframe>"));

        VideoAdapter videoAdapter = new VideoAdapter(youTubeVideos);
        recyclerView.setAdapter(videoAdapter);

        CardView cardView = findViewById(R.id.book_author_call_news);
        cardView.setOnClickListener(view ->
                startActivity(new Intent(HomePage.this, BookDetail.class).putExtra("bookData", randomBook)));
    }

    @Override
    public void selectBook(UploadBook uploadBook) {
        startActivity(new Intent(HomePage.this,
                BookDetail.class).putExtra("bookData", uploadBook));
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
