package com.example.melikeproje;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GecmisEtkinliklerFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // fragmentgecmisetk.xml layout dosyasını kullanıyoruz
        View view = inflater.inflate(R.layout.fragmentgecmisetk, container, false);

        RecyclerView rvGecmis = view.findViewById(R.id.rvGecmisEtkinlikler);
        if (rvGecmis != null) {
            rvGecmis.setLayoutManager(new LinearLayoutManager(getContext()));

            // Örnek Veriler
            List<Etkinlik> gecmisList = new ArrayList<>();
            gecmisList.add(new Etkinlik("Bahar Şenliği '24", "15.05.2024", "Merkez Kampüs", "Konserler ve turnuvalar başarıyla tamamlandı."));
            gecmisList.add(new Etkinlik("Teknoloji Zirvesi", "10.02.2024", "Teknopark", "Girişimcilik üzerine verimli bir etkinlikti."));

            GecmisEtkinliklerAdapter adapter = new GecmisEtkinliklerAdapter(gecmisList);
            rvGecmis.setAdapter(adapter);
        }

        return view;
    }

    // Model Sınıfı
    static class Etkinlik {
        String ad, tarih, yer, detay;

        public Etkinlik(String ad, String tarih, String yer, String detay) {
            this.ad = ad;
            this.tarih = tarih;
            this.yer = yer;
            this.detay = detay;
        }
    }

    // Adapter Sınıfı
    static class GecmisEtkinliklerAdapter extends RecyclerView.Adapter<GecmisEtkinliklerAdapter.GecmisViewHolder> {

        private final List<Etkinlik> list;

        public GecmisEtkinliklerAdapter(List<Etkinlik> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public GecmisViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // gecmisetkkart.xml layout dosyasını kullanıyoruz
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gecmisetkkart, parent, false);
            return new GecmisViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull GecmisViewHolder holder, int position) {
            Etkinlik e = list.get(position);
            holder.tvGecmisAd.setText("Etkinlik Adı: " + e.ad);
            holder.tvGecmisTarih.setText("Tarih: " + e.tarih);
            holder.tvGecmisYer.setText("Yer: " + e.yer);
            holder.tvGecmisDetay.setText("Açıklama: " + e.detay);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        // ViewHolder Sınıfı
        static class GecmisViewHolder extends RecyclerView.ViewHolder {
            TextView tvGecmisAd, tvGecmisTarih, tvGecmisYer, tvGecmisDetay;

            public GecmisViewHolder(@NonNull View itemView) {
                super(itemView);
                tvGecmisAd = itemView.findViewById(R.id.tvGecmisAd);
                tvGecmisTarih = itemView.findViewById(R.id.tvGecmisTarih);
                tvGecmisYer = itemView.findViewById(R.id.tvGecmisYer);
                tvGecmisDetay = itemView.findViewById(R.id.tvGecmisDetay);
            }
        }
    }
}
