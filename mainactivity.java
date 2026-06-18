package com.example.melikeproje;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.imageview.ShapeableImageView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private TextView tvProfileName, tvCreateEvent;
    private ImageView ivLogout;
    private ShapeableImageView ivProfilePic;
    private DBHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anaekran);

        dbHelper = new DBHelper(this);
        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvCreateEvent = findViewById(R.id.tvCreateEvent);
        ivLogout = findViewById(R.id.ivLogout);
        ivProfilePic = findViewById(R.id.ivProfilePic);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Etkinlikler"); break;
                case 1: tab.setText("Geçmiş"); break;
                case 2: tab.setText("Sosyal"); break;
            }
        }).attach();

        if (ivProfilePic != null) {
            ivProfilePic.setOnClickListener(v -> {
                drawerLayout.openDrawer(GravityCompat.START);
                updateNavHeader();
            });
        }

        if (ivLogout != null) {
            ivLogout.setOnClickListener(v -> {
                sessionManager.logoutUser();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            });
        }

        if (tvCreateEvent != null) {
            tvCreateEvent.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, EtkinlikOlusturActivity.class));
            });
        }

        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
        updateNavHeader();
    }

    private void updateUI() {
        String username = sessionManager.getUsername();
        if (tvProfileName != null) {
            tvProfileName.setText(username);
        }

        if (tvCreateEvent != null) {
            if ("representative".equals(sessionManager.getRole())) {
                tvCreateEvent.setVisibility(View.VISIBLE);
            } else {
                tvCreateEvent.setVisibility(View.GONE);
            }
        }

        Cursor cursor = dbHelper.getUserData(username);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("profile_image");
            if (columnIndex != -1) {
                String imageUriStr = cursor.getString(columnIndex);
                if (ivProfilePic != null) {
                    if (imageUriStr != null && !imageUriStr.isEmpty()) {
                        try {
                            ivProfilePic.setImageURI(Uri.parse(imageUriStr));
                            ivProfilePic.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        } catch (Exception e) {
                            ivProfilePic.setImageResource(android.R.drawable.ic_menu_gallery);
                        }
                    } else {
                        ivProfilePic.setImageResource(android.R.drawable.ic_menu_gallery);
                    }
                }
            }
            cursor.close();
        }
    }

    private void updateNavHeader() {
        if (navigationView == null) return;
        
        View headerView = navigationView.getHeaderView(0);
        TextView tvNavUsername = headerView.findViewById(R.id.tvNavUsername);
        TextView tvNavRole = headerView.findViewById(R.id.tvNavRole);
        TextView tvNavBio = headerView.findViewById(R.id.tvNavBio);
        TextView tvNavSchoolInfo = headerView.findViewById(R.id.tvNavSchoolInfo);
        TextView tvNavClubInfo = headerView.findViewById(R.id.tvNavClubInfo);
        ShapeableImageView ivNavProfilePic = headerView.findViewById(R.id.ivNavProfilePic);
        Button btnEditProfile = headerView.findViewById(R.id.btnEditProfile);

        String username = sessionManager.getUsername();
        if (tvNavUsername != null) tvNavUsername.setText(username);
        
        String role = sessionManager.getRole();
        if (tvNavRole != null) {
            tvNavRole.setText("representative".equals(role) ? "Kulüp Sorumlusu" : "Öğrenci");
        }

        Cursor cursor = dbHelper.getUserData(username);
        if (cursor != null && cursor.moveToFirst()) {
            String bio = getStringFromCursor(cursor, "bio");
            String school = getStringFromCursor(cursor, "school");
            String department = getStringFromCursor(cursor, "department");
            String clubName = getStringFromCursor(cursor, "club_name");
            String imageUriStr = getStringFromCursor(cursor, "profile_image");

            if (tvNavBio != null) {
                tvNavBio.setText(bio != null && !bio.isEmpty() ? bio : "Biyografi henüz eklenmedi.");
            }
            
            String schoolInfo = "";
            if (school != null && !school.isEmpty()) schoolInfo += school;
            if (department != null && !department.isEmpty()) schoolInfo += (schoolInfo.isEmpty() ? "" : " / ") + department;
            if (tvNavSchoolInfo != null) {
                tvNavSchoolInfo.setText(schoolInfo.isEmpty() ? "Okul/Bölüm bilgisi yok" : schoolInfo);
            }

            if (tvNavClubInfo != null) {
                if ("representative".equals(role)) {
                    tvNavClubInfo.setVisibility(View.VISIBLE);
                    tvNavClubInfo.setText("Kulüp: " + (clubName != null && !clubName.isEmpty() ? clubName : "-"));
                } else {
                    tvNavClubInfo.setVisibility(View.GONE);
                }
            }

            if (ivNavProfilePic != null) {
                if (imageUriStr != null && !imageUriStr.isEmpty()) {
                    try {
                        Uri imgUri = Uri.parse(imageUriStr);
                        ivNavProfilePic.setImageURI(imgUri);
                        ivNavProfilePic.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        ivNavProfilePic.setOnClickListener(v -> showEnlargedImage(imgUri));
                    } catch (Exception e) {
                        ivNavProfilePic.setImageResource(android.R.drawable.ic_menu_gallery);
                        ivNavProfilePic.setOnClickListener(null);
                    }
                } else {
                    ivNavProfilePic.setImageResource(android.R.drawable.ic_menu_gallery);
                    ivNavProfilePic.setOnClickListener(null);
                }
            }
            cursor.close();
        }

        if (btnEditProfile != null) {
            btnEditProfile.setOnClickListener(v -> {
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(MainActivity.this, ProfileEditActivity.class));
            });
        }
    }

    private String getStringFromCursor(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        return index != -1 ? cursor.getString(index) : null;
    }

    private void showEnlargedImage(Uri imgUri) {
        android.app.Dialog dialog = new android.app.Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.view_full_image);
        ImageView ivFull = dialog.findViewById(R.id.ivFullImage);
        if (ivFull != null) {
            ivFull.setImageURI(imgUri);
            ivFull.setOnClickListener(v -> dialog.dismiss());
        }
        dialog.show();
    }
}
