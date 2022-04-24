package com.example.introinvert.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.introinvert.databinding.ItemContainerAddUserBinding;
import com.example.introinvert.listeners.UserListener;
import com.example.introinvert.models.Users;

import java.util.ArrayList;
import java.util.List;

public class AddUserAdapter extends RecyclerView.Adapter<AddUserAdapter.AddUserViewHolder> {

    private final List<Users> usersList;
    private final List<Users> alreadyAddedUsers;
    private final UserListener userListener;

    public AddUserAdapter(List<Users> usersList, List<Users> alreadyAddedUsers, UserListener userListener) {
        this.usersList = usersList;
        this.alreadyAddedUsers = alreadyAddedUsers;
        System.out.println("already added users: " + alreadyAddedUsers.size());
        System.out.println("already added users: " + alreadyAddedUsers.get(0).name);
        this.userListener = userListener;
    }

    @NonNull
    @Override
    public AddUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AddUserViewHolder(ItemContainerAddUserBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AddUserViewHolder holder, int position) {
        List<String> alreadyAddedUserIds = new ArrayList<>();
        for (Users user : alreadyAddedUsers) {
            alreadyAddedUserIds.add(user.id);
        }
        holder.setData(usersList.get(position));
        if (alreadyAddedUserIds.contains(usersList.get(position).id)) {
            holder.setChecked(true);
        }
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    class AddUserViewHolder extends RecyclerView.ViewHolder {

        private ItemContainerAddUserBinding binding;

        public AddUserViewHolder(ItemContainerAddUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        //EFFECTS: set Data for user
        public void setData(Users user) {
            binding.textName.setText(user.name);
            binding.imageProfile.setImageBitmap(getUserImage(user.image));
            binding.textEmail.setText(user.email);
            binding.checkBox.setOnCheckedChangeListener((bv, bl) -> userListener.onUserClicked(user));
        }

        public void setChecked(boolean checked) {
            binding.checkBox.setChecked(checked);
        }

    }

    private Bitmap getUserImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0, bytes.length);
    }

}
