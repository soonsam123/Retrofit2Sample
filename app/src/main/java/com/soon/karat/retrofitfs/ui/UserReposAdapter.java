package com.soon.karat.retrofitfs.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.soon.karat.retrofitfs.R;
import com.soon.karat.retrofitfs.models.GitHubRepo;
import com.soon.karat.retrofitfs.utils.GlideApp;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserReposAdapter extends RecyclerView.Adapter<UserReposAdapter.ItemHolder> {

    private List<GitHubRepo> repositories;

    public UserReposAdapter(List<GitHubRepo> repositories) {
        this.repositories = repositories;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_repos, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {

        GitHubRepo singleRepo = repositories.get(position);

        GlideApp.with(holder.profileImage.getContext())
                .load(singleRepo.owner.getAvatar_url())
                .centerCrop()
                .into(holder.profileImage);

        holder.repoName.setText(singleRepo.getName());
        holder.repoLanguage.setText(singleRepo.getLanguage());
    }

    @Override
    public int getItemCount() {
        return repositories.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        private CircleImageView profileImage;
        private TextView repoName;
        private TextView repoLanguage;

        public ItemHolder(View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.image_profile);
            repoName = itemView.findViewById(R.id.text_repo_name);
            repoLanguage = itemView.findViewById(R.id.text_repo_language);
        }
    }

}
