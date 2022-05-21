package com.example.nexus_scribes.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.nexus_scribes.R;
import com.example.nexus_scribes.UploadUser;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;


public class AuthorAdapter extends RecyclerView.Adapter<AuthorAdapter.AuthorViewHolder> {

    Context context;
    ArrayList<UploadUser> userArrayList;
    SelectAuthor selectAuthor;

    public AuthorAdapter(Context context, ArrayList<UploadUser> userArrayList, SelectAuthor selectAuthor) {
        this.context = context;
        this.userArrayList = userArrayList;
        this.selectAuthor = selectAuthor;
    }

    @NonNull
    @Override
    public AuthorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.author_card,
                parent, false);

        return new AuthorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AuthorAdapter.AuthorViewHolder holder, int position) {

        UploadUser author = userArrayList.get(position);

        if (!author.penName.equals("")) {
            holder.penName.setText(author.penName);
        } else {
            holder.fullName.setText(author.fullName);
        }
/*        String briefBio = author.userBio.substring(0, 10);
        holder.userBio.setText(String.format("%s...", briefBio));*/
        Picasso.get().load(author.getImageProfile())
                .resize(90,90).into(holder.imageProfile);

    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public interface SelectAuthor {
        void selectAuthor(UploadUser uploadUser);
    }

    public class AuthorViewHolder extends RecyclerView.ViewHolder {

        TextView fullName, penName, userBio;
        ImageView imageProfile;

        @SuppressLint("CutPasteId")
        public AuthorViewHolder(@NonNull View itemView) {
            super(itemView);

            fullName = itemView.findViewById(R.id.author_name);
            penName = itemView.findViewById(R.id.author_name);
/*            userBio = itemView.findViewById(R.id.author_bio);*/
            imageProfile = itemView.findViewById(R.id.author_pic);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectAuthor.selectAuthor(userArrayList.get(getAbsoluteAdapterPosition()));
                }
            });

        }
    }

}
