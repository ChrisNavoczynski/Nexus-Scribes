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
import com.example.nexus_scribes.firestoreData.UploadBook;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    Context context;
    ArrayList<UploadBook> bookArrayList;
    SelectBook selectbook;


    public BookAdapter(Context context, ArrayList<UploadBook> bookArrayList, BookAdapter.SelectBook selectBook) {
        this.context = context;
        this.bookArrayList = bookArrayList;
        this.selectbook = selectBook;
    }

    @NonNull
    @Override
    public BookAdapter.BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.book_card,
                parent, false);

        return new BookAdapter.BookViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull BookAdapter.BookViewHolder holder, int position) {

        UploadBook book = bookArrayList.get(position);

        holder.bookTitle.setText(book.bookTitle);
        if (!book.penName.equals("")) {
            holder.penName.setText(book.penName);
        } else {
            holder.fullName.setText(book.fullName);
        }
        Picasso.get().load(book.getBookImage())
                .resize(90,110).into(holder.bookImage);

    }

    @Override
    public int getItemCount() {
        return bookArrayList.size();
    }

    public interface SelectBook {
        void selectBook(UploadBook uploadBook);
    }

    public class BookViewHolder extends RecyclerView.ViewHolder {

        TextView fullName, penName, bookTitle;
        ImageView bookImage;

        @SuppressLint("CutPasteId")
        public BookViewHolder(@NonNull View itemView) {
            super(itemView);

            bookTitle = itemView.findViewById(R.id.book_title);
            fullName = itemView.findViewById(R.id.book_author_name);
            penName = itemView.findViewById(R.id.book_author_name);
            bookImage = itemView.findViewById(R.id.book_cover_pic);

            itemView.setOnClickListener(
                    view -> selectbook.selectBook(
                            bookArrayList.get(getAbsoluteAdapterPosition())));

        }
    }
}
