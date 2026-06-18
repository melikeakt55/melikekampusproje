package com.example.melikeproje;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private RadioGroup rgLoginRole;
    private DBHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        sessionManager = new SessionManager(this);
        // Eğer kullanıcı zaten giriş yapmışsa direkt ana ekrana gönder
        if (sessionManager.isLoggedIn()) {
            goToMainActivity();
            return;
        }

        setContentView(R.layout.girisekran);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        rgLoginRole = findViewById(R.id.rgLoginRole);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvRegister = findViewById(R.id.tvRegister);
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);
        
        dbHelper = new DBHelper(this);

        btnLogin.setOnClickListener(v -> {
            String user = etUsername.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            String selectedRole = "student";
            if (rgLoginRole != null && rgLoginRole.getCheckedRadioButtonId() == R.id.rbLoginRepresentative) {
                selectedRole = "representative";
            }

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Lütfen kullanıcı adı ve şifrenizi girin", Toast.LENGTH_SHORT).show();
            } else {
                boolean isValidUser = dbHelper.checkUser(user, pass);
                if (isValidUser) {
                    String dbRole = dbHelper.getUserRole(user);
                    if (dbRole != null && dbRole.equalsIgnoreCase(selectedRole)) {
                        sessionManager.createLoginSession(user, dbRole);
                        Toast.makeText(LoginActivity.this, "Giriş Başarılı! Hoş geldin " + user, Toast.LENGTH_SHORT).show();
                        goToMainActivity();
                    } else {
                        String roleName = selectedRole.equals("student") ? "Öğrenci" : "Kulüp Sorumlusu";
                        Toast.makeText(LoginActivity.this, "Bu kullanıcı " + roleName + " rolüne sahip değil", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Hatalı Kullanıcı Adı veya Şifre", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
