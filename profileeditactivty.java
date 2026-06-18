package com.example.melikeproje;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class ProfileEditActivity extends AppCompatActivity {

    private EditText etEditSchool, etEditDepartment, etEditClubName, etEditBio, etNewPassword;
    private Button btnSaveProfile, btnUpdatePassword, btnChangePic, btnRemovePic;
    private ShapeableImageView ivEditProfilePic;
    private LinearLayout layoutClubEdit;
    private DBHelper dbHelper;
    private SessionManager sessionManager;
    private String username;
    private String currentImageUri = null;

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    String savedUri = saveImageToInternalStorage(uri);
                    if (savedUri != null) {
                        currentImageUri = savedUri;
                        ivEditProfilePic.setImageURI(Uri.parse(currentImageUri));
                    } else {
                        Toast.makeText(this, "Resim kaydedilemedi", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profil_duzenle);

        dbHelper = new DBHelper(this);
        sessionManager = new SessionManager(this);
        username = sessionManager.getUsername();

        ivEditProfilePic = findViewById(R.id.ivEditProfilePic);
        btnChangePic = findViewById(R.id.btnChangePic);
        btnRemovePic = findViewById(R.id.btnRemovePic);
        etEditSchool = findViewById(R.id.etEditSchool);
        etEditDepartment = findViewById(R.id.etEditDepartment);
        etEditClubName = findViewById(R.id.etEditClubName);
        etEditBio = findViewById(R.id.etEditBio);
        etNewPassword = findViewById(R.id.etNewPassword);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnUpdatePassword = findViewById(R.id.btnUpdatePassword);
        layoutClubEdit = findViewById(R.id.layoutClubEdit);

        if ("representative".equals(sessionManager.getRole())) {
            layoutClubEdit.setVisibility(View.VISIBLE);
        }

        loadUserData();

        btnChangePic.setOnClickListener(v -> mGetContent.launch("image/*"));

        btnRemovePic.setOnClickListener(v -> {
            currentImageUri = null;
            ivEditProfilePic.setImageResource(android.R.drawable.ic_menu_gallery);
        });

        btnSaveProfile.setOnClickListener(v -> {
            String bio = etEditBio.getText().toString();
            String school = etEditSchool.getText().toString();
            String dept = etEditDepartment.getText().toString();
            String club = etEditClubName.getText().toString();

            boolean updated = dbHelper.updateProfile(username, bio, school, dept, club, currentImageUri);
            if (updated) {
                Toast.makeText(this, "Profil güncellendi", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Güncelleme başarısız", Toast.LENGTH_SHORT).show();
            }
        });

        btnUpdatePassword.setOnClickListener(v -> {
            String newPass = etNewPassword.getText().toString();
            if (newPass.isEmpty()) {
                Toast.makeText(this, "Lütfen yeni bir şifre girin", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean updated = dbHelper.updatePassword(username, newPass);
            if (updated) {
                Toast.makeText(this, "Şifre güncellendi", Toast.LENGTH_SHORT).show();
                etNewPassword.setText("");
            } else {
                Toast.makeText(this, "Şifre güncelleme başarısız", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String saveImageToInternalStorage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            File file = new File(getFilesDir(), "profile_" + username + "_" + System.currentTimeMillis() + ".jpg");
            OutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
            return Uri.fromFile(file).toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadUserData() {
        Cursor cursor = dbHelper.getUserData(username);
        if (cursor != null && cursor.moveToFirst()) {
            etEditBio.setText(cursor.getString(cursor.getColumnIndexOrThrow("bio")));
            etEditSchool.setText(cursor.getString(cursor.getColumnIndexOrThrow("school")));
            etEditDepartment.setText(cursor.getString(cursor.getColumnIndexOrThrow("department")));
            etEditClubName.setText(cursor.getString(cursor.getColumnIndexOrThrow("club_name")));
            
            currentImageUri = cursor.getString(cursor.getColumnIndexOrThrow("profile_image"));
            if (currentImageUri != null && !currentImageUri.isEmpty()) {
                try {
                    ivEditProfilePic.setImageURI(Uri.parse(currentImageUri));
                    ivEditProfilePic.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } catch (Exception e) {
                    ivEditProfilePic.setImageResource(android.R.drawable.ic_menu_gallery);
                }
            }
            cursor.close();
        }
    }
}
