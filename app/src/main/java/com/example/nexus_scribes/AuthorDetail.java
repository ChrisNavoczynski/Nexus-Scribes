package com.example.nexus_scribes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;

public class AuthorDetail extends AppCompatActivity {

    TextView tvName, tvBio;
    ImageView ivProfile;
    String imageProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.author_detail);

        tvName = findViewById(R.id.author_display_name);
        tvBio = findViewById(R.id.author_display_bio);
        ivProfile = findViewById(R.id.author_profile);

        Intent intent = getIntent();

        if(intent.getExtras() != null) {
            UploadUser uploadUser = (UploadUser) intent.getSerializableExtra("data");
            imageProfile = uploadUser.getImageProfile();

            if (!uploadUser.getPenName().equals("")) {
                tvName.setText(uploadUser.getPenName());
            } else {
                tvName.setText(uploadUser.getFullName());
            }
            tvBio.setText(uploadUser.getUserBio());
            Picasso.get().setLoggingEnabled(true);
            Picasso.get().load(imageProfile)
                    .resize(300,300)
                    .centerCrop().into(ivProfile);

        }

    }

}
