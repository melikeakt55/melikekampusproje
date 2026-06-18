package com.example.melikeproje;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText etRegUsername, etRegEmail, etRegPassword, etRegPasswordConfirm;
    private RadioGroup rgRole;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kayit);

        etRegUsername = findViewById(R.id.etRegUsername);
        etRegEmail = findViewById(R.id.etRegEmail);
        etRegPassword = findViewById(R.id.etRegPassword);
        etRegPasswordConfirm = findViewById(R.id.etRegPasswordConfirm);
        rgRole = findViewById(R.id.rgRole);
        Button btnRegister = findViewById(R.id.btnRegister);
        android.widget.TextView tvBackToLogin = findViewById(R.id.tvBackToLogin);
        
        dbHelper = new DBHelper(this);

        btnRegister.setOnClickListener(v -> {
            String user = etRegUsername.getText().toString().trim();
            String email = etRegEmail.getText().toString().trim();
            String pass = etRegPassword.getText().toString().trim();
            String repass = etRegPasswordConfirm.getText().toString().trim();

            String role = "student";
            int selectedId = rgRole.getCheckedRadioButtonId();
            if (selectedId == R.id.rbRepresentative) {
                role = "representative";
            }

            if (user.isEmpty() || email.isEmpty() || pass.isEmpty() || repass.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
            } else if (!pass.equals(repass)) {
                Toast.makeText(RegisterActivity.this, "Şifreler uyuşmuyor", Toast.LENGTH_SHORT).show();
            } else {
                if (dbHelper.checkUsername(user)) {
                    Toast.makeText(RegisterActivity.this, "Bu kullanıcı adı zaten alınmış", Toast.LENGTH_SHORT).show();
                } else if (dbHelper.checkEmail(email)) {
                    Toast.makeText(RegisterActivity.this, "Bu e-posta zaten kullanımda", Toast.LENGTH_SHORT).show();
                } else {
                    boolean insert = dbHelper.insertUser(user, pass, role, email);
                    if (insert) {
                        Toast.makeText(RegisterActivity.this, "Kayıt Başarılı! Şimdi giriş yapabilirsiniz.", Toast.LENGTH_LONG).show();
                        // Kayıt başarılı olunca giriş ekranına dönüyoruz. 
                        // finish() çağrısı LoginActivity'ye döndürür.
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Kayıt sırasında bir hata oluştu.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        tvBackToLogin.setOnClickListener(v -> finish());
    }
}
