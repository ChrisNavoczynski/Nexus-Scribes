package com.example.nexus_scribes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.nexus_scribes.firestoreData.UploadBook;

import com.squareup.picasso.Picasso;

public class BookDetail extends AppCompatActivity {

    TextView tvTitle, tvAuthor, tvSynopsis;
    ImageView ivBookCover, ivProfileImage;
    String bookImage, imageProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_detail);

        tvTitle = findViewById(R.id.book_display_title);
        tvAuthor = findViewById(R.id.book_display_author);
        tvSynopsis = findViewById(R.id.book_display_about);
        ivBookCover = findViewById(R.id.book_display_image);
        ivProfileImage = findViewById(R.id.author_book_pic);

        Intent intent = getIntent();

        if(intent.getExtras() != null) {
            UploadBook uploadBook = (UploadBook) intent.getSerializableExtra("bookData");
            bookImage = uploadBook.getBookImage();
            imageProfile = uploadBook.getImageProfile();

            if (!uploadBook.getPenName().equals("")) {
                tvAuthor.setText(uploadBook.getPenName());
            } else {
                tvAuthor.setText(uploadBook.getFullName());
            }
            tvTitle.setText(uploadBook.getBookTitle());
            tvSynopsis.setText(uploadBook.getBookSynopsis());
            Picasso.get().setLoggingEnabled(true);
            Picasso.get().load(bookImage)
                    .resize(200,280)
                    .centerCrop().into(ivBookCover);
            Picasso.get().load(imageProfile)
                    .resize(90,90)
                    .centerCrop().into(ivProfileImage);


            CardView cardView = findViewById(R.id.book_author_call);
            cardView.setOnClickListener(view ->
                    startActivity(new Intent(BookDetail.this, AuthorGallery.class)));
        }
    }

}
