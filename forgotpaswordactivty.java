package com.example.melikeproje;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;
import java.util.Random;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etForgotEmail, etVerificationCode, etNewPassword, etConfirmNewPassword;
    private Button btnResetPassword;
    private DBHelper dbHelper;
    private SessionManager sessionManager;
    
    private String generatedCode;
    private int currentStep = 1; // 1: Email, 2: Code, 3: Password

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sifre_unuttum);

        etForgotEmail = findViewById(R.id.etForgotEmail);
        etVerificationCode = findViewById(R.id.etVerificationCode);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        TextView tvBackToLoginFromForgot = findViewById(R.id.tvBackToLoginFromForgot);
        
        dbHelper = new DBHelper(this);
        sessionManager = new SessionManager(this);

        btnResetPassword.setText("E-postayı Doğrula");

        btnResetPassword.setOnClickListener(v -> {
            if (currentStep == 1) {
                handleEmailVerification();
            } else if (currentStep == 2) {
                handleCodeVerification();
            } else if (currentStep == 3) {
                handlePasswordReset();
            }
        });

        tvBackToLoginFromForgot.setOnClickListener(v -> finish());
    }

    private void handleEmailVerification() {
        String email = etForgotEmail.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(this, "Lütfen e-posta adresinizi girin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.checkEmail(email)) {
            generatedCode = String.format(Locale.getDefault(), "%06d", new Random().nextInt(1000000));
            
            new AlertDialog.Builder(this)
                    .setTitle("Doğrulama Kodu")
                    .setMessage("Kodunuz: " + generatedCode)
                    .setPositiveButton("Tamam", null)
                    .show();
            
            etForgotEmail.setEnabled(false);
            etVerificationCode.setVisibility(View.VISIBLE);
            btnResetPassword.setText("Kodu Doğrula");
            currentStep = 2;
        } else {
            Toast.makeText(this, "Bu e-posta adresi kayıtlı değil", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleCodeVerification() {
        String inputCode = etVerificationCode.getText().toString().trim();
        if (inputCode.equals(generatedCode)) {
            etVerificationCode.setEnabled(false);
            etNewPassword.setVisibility(View.VISIBLE);
            etConfirmNewPassword.setVisibility(View.VISIBLE);
            btnResetPassword.setText("Şifreyi Güncelle ve Giriş Yap");
            currentStep = 3;
            Toast.makeText(this, "Kod doğrulandı! Yeni şifrenizi belirleyin.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Hatalı doğrulama kodu!", Toast.LENGTH_SHORT).show();
        }
    }

    private void handlePasswordReset() {
        String email = etForgotEmail.getText().toString().trim();
        String pass = etNewPassword.getText().toString().trim();
        String confirm = etConfirmNewPassword.getText().toString().trim();

        if (pass.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(this, "Lütfen şifre alanlarını doldurun", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pass.equals(confirm)) {
            Toast.makeText(this, "Şifreler uyuşmuyor", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.updatePasswordByEmail(email, pass)) {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT username, role FROM users WHERE email = ?", new String[]{email});
            if (cursor.moveToFirst()) {
                String username = cursor.getString(0);
                String role = cursor.getString(1);
                sessionManager.createLoginSession(username, role);
                
                Toast.makeText(this, "Şifre güncellendi. Giriş yapıldı!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
            cursor.close();
        } else {
            Toast.makeText(this, "Bir hata oluştu", Toast.LENGTH_SHORT).show();
        }
    }
}
