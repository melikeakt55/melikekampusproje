package com.example.melikeproje;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EtkinlikOlusturActivity extends AppCompatActivity {

    EditText etEtkinlikAdi, etEtkinlikAciklama, etTarihSaat, etYerKonum;
    Button btnEtkinlikKaydet;
    DBHelper dbHelper;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.etkinlikolustur);

        etEtkinlikAdi = findViewById(R.id.etEtkinlikAdi);
        etEtkinlikAciklama = findViewById(R.id.etEtkinlikAciklama);
        etTarihSaat = findViewById(R.id.etTarihSaat);
        etYerKonum = findViewById(R.id.etYerKonum);
        btnEtkinlikKaydet = findViewById(R.id.btnEtkinlikKaydet);
        
        dbHelper = new DBHelper(this);
        sessionManager = new SessionManager(this);

        btnEtkinlikKaydet.setOnClickListener(v -> {
            String name = etEtkinlikAdi.getText().toString().trim();
            String desc = etEtkinlikAciklama.getText().toString().trim();
            String date = etTarihSaat.getText().toString().trim();
            String loc = etYerKonum.getText().toString().trim();

            if (name.isEmpty() || desc.isEmpty() || date.isEmpty() || loc.isEmpty()) {
                Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
            } else {
                String username = sessionManager.getUsername();
                int ownerId = dbHelper.getUserId(username);
                
                boolean insert = dbHelper.insertEvent(name, desc, date, loc, ownerId);
                if (insert) {
                    Toast.makeText(this, "Etkinlik başarıyla paylaşıldı", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Hata oluştu!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
