package com.example.nexus_scribes.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nexus_scribes.databinding.ItemContainerUserBinding;
import com.example.nexus_scribes.listeners.UserListener;
import com.example.nexus_scribes.models.ChatUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private final List<ChatUser> chatUsers;
    private final UserListener userListener;

    public UsersAdapter(List<ChatUser> chatUsers, UserListener userListener) {
        this.chatUsers = chatUsers;
        this.userListener = userListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new UserViewHolder(itemContainerUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.setUserData(chatUsers.get(position));
    }

    @Override
    public int getItemCount() {
        return chatUsers.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        ItemContainerUserBinding binding;

        UserViewHolder(ItemContainerUserBinding itemContainerUserBinding) {
            super(itemContainerUserBinding.getRoot());
            binding = itemContainerUserBinding;
        }

        void setUserData(ChatUser chatUser) {
            if (!chatUser.penName.equals("")) {
                binding.textName.setText(chatUser.penName);
            } else {
                binding.textName.setText(chatUser.fullName);
            }
            Picasso.get().load(chatUser.imageProfile)
                    .resize(50,50).into(binding.imageProfile);
            binding.getRoot().setOnClickListener(v -> userListener.onUserClicked(chatUser));

        }
    }
}
