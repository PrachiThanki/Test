package com.example.chathub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chathub.model.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class searchUserRecyclerAdapter extends FirestoreRecyclerAdapter<User,searchUserRecyclerAdapter.UserModelViewHolder> {

    Context context;
    public searchUserRecyclerAdapter(@NonNull FirestoreRecyclerOptions<User> options,Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserModelViewHolder holder, int position, @NonNull User model) {
        holder.usernameText.setText(model.getUserId());
        holder.statusText.setText(model.getPhoneNumber());

        // Set profile initial (first letter of username) if no profile pic
        if (model.getUserId() != null && !model.getUserId().isEmpty()) {
            holder.profileInitials.setText(String.valueOf(model.getUserId().charAt(0)).toUpperCase());
        }
    }

    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_user_recycler_row,parent,false);
        return new UserModelViewHolder(view);
    }

    class UserModelViewHolder extends RecyclerView.ViewHolder {

        TextView usernameText;
        TextView statusText;
        TextView profileInitials;
        ImageView profilePic;
        ImageView profileBg;

        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name);
            statusText = itemView.findViewById(R.id.status_text);
            profilePic = itemView.findViewById(R.id.profile_image);
            profileInitials = itemView.findViewById(R.id.profile_initials);
            profileBg = itemView.findViewById(R.id.profile_image_bg);

            // Set item click listener
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    User user = getItem(position);
                    // Handle user selection here if needed
                }
            });
        }
    }
}