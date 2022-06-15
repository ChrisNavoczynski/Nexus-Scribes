package com.example.nexus_scribes.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nexus_scribes.databinding.ItemContainerRecentConversationBinding;
import com.example.nexus_scribes.listeners.ConversationListener;
import com.example.nexus_scribes.models.ChatMessage;
import com.example.nexus_scribes.models.ChatUser;
import com.example.nexus_scribes.utilities.Constants;
import com.example.nexus_scribes.utilities.PreferenceManager;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecentConversationAdapter extends
        RecyclerView.Adapter<RecentConversationAdapter.ConversationViewHolder> {

    private final List<ChatMessage> chatMessages;
    private final ConversationListener conversationListener;
    PreferenceManager preferenceManager;

    public RecentConversationAdapter(List<ChatMessage> chatMessages,
                                     ConversationListener conversationListener) {
        this.chatMessages = chatMessages;
        this.conversationListener = conversationListener;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversationViewHolder(
                ItemContainerRecentConversationBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {

        holder.setData(chatMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    class ConversationViewHolder extends RecyclerView.ViewHolder {

        ItemContainerRecentConversationBinding binding;
        ConversationViewHolder(ItemContainerRecentConversationBinding
                                       itemContainerRecentConversationBinding) {
            super(itemContainerRecentConversationBinding.getRoot());
            binding = itemContainerRecentConversationBinding;
        }

        void setData(ChatMessage chatMessage) {

            binding.textName.setText(chatMessage.conversationName);
            binding.textRecentMessage.setText(chatMessage.message);
            Picasso.get().load(chatMessage.conversationImage)
                    .resize(60,60).into(binding.imageProfile);

            binding.getRoot().setOnClickListener(v ->{
                ChatUser chatUser = new ChatUser();
                chatUser.id = chatMessage.conversationId;
                chatUser.imageProfile = chatMessage.conversationImage;
                chatUser.name = chatMessage.conversationName;
/*                if(preferenceManager.getString(Constants.KEY_PEN_NAME) == null) {
                    chatUser.fullName = (chatMessage.conversationName);
                } else {
                    chatUser.penName = (chatMessage.conversationName);
                }*/
                conversationListener.onConversationClicked(chatUser);
            });
        }
    }
}
