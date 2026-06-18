package com.example.melikeproje;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SosyalFragment extends Fragment {

    private DBHelper dbHelper;
    private SessionManager sessionManager;
    private List<SosyalPost> postList;
    private SosyalAdapter adapter;
    private EditText etPostContent;
    private Button btnSharePost;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmentsosyal, container, false);

        dbHelper = new DBHelper(getContext());
        sessionManager = new SessionManager(getContext());

        etPostContent = view.findViewById(R.id.etPostContent);
        btnSharePost = view.findViewById(R.id.btnSharePost);
        RecyclerView rvSosyal = view.findViewById(R.id.rvSosyal);
        
        rvSosyal.setLayoutManager(new LinearLayoutManager(getContext()));

        loadPosts();

        adapter = new SosyalAdapter(postList);
        rvSosyal.setAdapter(adapter);

        btnSharePost.setOnClickListener(v -> {
            String content = etPostContent.getText().toString().trim();
            if (!content.isEmpty()) {
                String username = sessionManager.getUsername();
                if (username == null) username = "Anonim";
                
                boolean inserted = dbHelper.insertPost(username, content);
                if (inserted) {
                    etPostContent.setText("");
                    loadPosts();
                    adapter.updateList(postList);
                    Toast.makeText(getContext(), "Paylaşıldı!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void loadPosts() {
        postList = new ArrayList<>();
        Cursor cursor = dbHelper.getAllPostsWithProfile();
        
        if (cursor != null) {
            if (cursor.getCount() == 0) {
                dbHelper.insertPost("Sistem", "Kampüs Etkinlikleri uygulamasına hoş geldiniz!");
                cursor.close();
                cursor = dbHelper.getAllPostsWithProfile();
            }

            int usernameIdx = cursor.getColumnIndex("username");
            int contentIdx = cursor.getColumnIndex("content");
            int profileIdx = cursor.getColumnIndex("profile_image");

            while (cursor.moveToNext()) {
                postList.add(new SosyalPost(
                        cursor.getString(usernameIdx),
                        cursor.getString(contentIdx),
                        cursor.getString(profileIdx),
                        false 
                ));
            }
            cursor.close();
        }
    }

    static class SosyalPost {
        String username;
        String content;
        String profileImagePath;
        boolean hasImage;

        public SosyalPost(String username, String content, String profileImagePath, boolean hasImage) {
            this.username = username;
            this.content = content;
            this.profileImagePath = profileImagePath;
            this.hasImage = hasImage;
        }
    }

    static class SosyalAdapter extends RecyclerView.Adapter<SosyalAdapter.SosyalViewHolder> {
        private List<SosyalPost> list;

        public SosyalAdapter(List<SosyalPost> list) {
            this.list = list;
        }

        public void updateList(List<SosyalPost> newList) {
            this.list = newList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public SosyalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sosyal_post, parent, false);
            return new SosyalViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SosyalViewHolder holder, int position) {
            SosyalPost post = list.get(position);
            holder.tvKullaniciAdi.setText("@" + post.username);
            holder.tvPostContent.setText(post.content);
            holder.ivPostImage.setVisibility(post.hasImage ? View.VISIBLE : View.GONE);

            if (post.profileImagePath != null && !post.profileImagePath.isEmpty()) {
                try {
                    holder.ivPostUserProfile.setImageURI(Uri.parse(post.profileImagePath));
                } catch (Exception e) {
                    holder.ivPostUserProfile.setImageResource(android.R.drawable.ic_menu_gallery);
                }
            } else {
                holder.ivPostUserProfile.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        static class SosyalViewHolder extends RecyclerView.ViewHolder {
            TextView tvKullaniciAdi, tvPostContent;
            ImageView ivPostImage, ivPostUserProfile;

            public SosyalViewHolder(@NonNull View itemView) {
                super(itemView);
                tvKullaniciAdi = itemView.findViewById(R.id.tvKullaniciAdi);
                tvPostContent = itemView.findViewById(R.id.tvPostContent);
                ivPostImage = itemView.findViewById(R.id.ivPostImage);
                ivPostUserProfile = itemView.findViewById(R.id.ivPostUserProfile);
            }
        }
    }
}
