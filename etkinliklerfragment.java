package com.example.melikeproje;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class EtkinliklerFragment extends Fragment {

    private DBHelper dbHelper;
    private SessionManager sessionManager;
    private List<Etkinlik> etkinlikList;
    private EtkinlikAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmentetkinlikler, container, false);

        dbHelper = new DBHelper(getContext());
        sessionManager = new SessionManager(getContext());
        
        RecyclerView rvEtkinlikler = view.findViewById(R.id.rvEtkinlikler);
        rvEtkinlikler.setLayoutManager(new LinearLayoutManager(getContext()));

        loadData();

        adapter = new EtkinlikAdapter(etkinlikList, (etkinlik) -> {
            String currentUser = sessionManager.getUsername();
            boolean success = dbHelper.joinEvent(etkinlik.id, currentUser);
            if (success) {
                Toast.makeText(getContext(), etkinlik.ad + " etkinliğine katıldınız!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Hata oluştu!", Toast.LENGTH_SHORT).show();
            }
        });
        rvEtkinlikler.setAdapter(adapter);

        return view;
    }

    private void loadData() {
        etkinlikList = new ArrayList<>();
        Cursor cursor = dbHelper.getAllEvents();
        
        if (cursor != null) {
            if (cursor.getCount() == 0) {
                dbHelper.insertEvent("Hoş Geldin Etkinliği", "Yeni gelen öğrenciler için tanışma partisi.", "25.10.2024", "Kütüphane Bahçesi", 1);
                dbHelper.insertEvent("Kariyer Günleri", "Sektörün öncü firmalarıyla buluşma.", "05.11.2024", "Konferans Salonu", 1);
                cursor.close();
                cursor = dbHelper.getAllEvents();
            }

            int idIdx = cursor.getColumnIndex("id");
            int nameIdx = cursor.getColumnIndex("name");
            int dateIdx = cursor.getColumnIndex("date");
            int locIdx = cursor.getColumnIndex("location");
            int descIdx = cursor.getColumnIndex("description");

            while (cursor.moveToNext()) {
                etkinlikList.add(new Etkinlik(
                        cursor.getInt(idIdx),
                        cursor.getString(nameIdx),
                        cursor.getString(dateIdx),
                        cursor.getString(locIdx),
                        cursor.getString(descIdx)
                ));
            }
            cursor.close();
        }
    }

    static class Etkinlik {
        int id;
        String ad, tarih, yer, detay;
        public Etkinlik(int id, String ad, String tarih, String yer, String detay) {
            this.id = id;
            this.ad = ad;
            this.tarih = tarih;
            this.yer = yer;
            this.detay = detay;
        }
    }

    interface OnEtkinlikClickListener {
        void onJoinClick(Etkinlik etkinlik);
    }

    static class EtkinlikAdapter extends RecyclerView.Adapter<EtkinlikAdapter.EtkinlikViewHolder> {
        private final List<Etkinlik> list;
        private final OnEtkinlikClickListener listener;

        public EtkinlikAdapter(List<Etkinlik> list, OnEtkinlikClickListener listener) {
            this.list = list;
            this.listener = listener;
        }

        @NonNull
        @Override
        public EtkinlikViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.etkinlikkart, parent, false);
            return new EtkinlikViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull EtkinlikViewHolder holder, int position) {
            Etkinlik e = list.get(position);
            holder.tvEtkinlikAdi.setText(e.ad);
            holder.tvTarihSaat.setText(e.tarih);
            holder.tvYerMekan.setText(e.yer);
            holder.tvEtkinlikDetay.setText(e.detay);

            holder.btnKatilimTalebi.setOnClickListener(v -> listener.onJoinClick(e));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        static class EtkinlikViewHolder extends RecyclerView.ViewHolder {
            TextView tvEtkinlikAdi, tvTarihSaat, tvYerMekan, tvEtkinlikDetay;
            Button btnKatilimTalebi;

            public EtkinlikViewHolder(@NonNull View itemView) {
                super(itemView);
                tvEtkinlikAdi = itemView.findViewById(R.id.tvEtkinlikAdi);
                tvTarihSaat = itemView.findViewById(R.id.tvTarihSaat);
                tvYerMekan = itemView.findViewById(R.id.tvYerMekan);
                tvEtkinlikDetay = itemView.findViewById(R.id.tvEtkinlikDetay);
                btnKatilimTalebi = itemView.findViewById(R.id.btnKatilimTalebi);
            }
        }
    }
}
