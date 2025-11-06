package service;

import model.Transaksi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Kelas KeuanganManager mengelola logika aplikasi dan daftar transaksi.
 * Menerapkan prinsip Abstraction dengan menyembunyikan detail List.
 */
public class KeuanganManager {
    private List<Transaksi> daftarTransaksi; 

    public KeuanganManager() {
        this.daftarTransaksi = new ArrayList<>();
    }

    // --- METODE CRUD (Create, Read, Update, Delete) ---
    
    // Create: Menambah entri
    public void tambahTransaksi(Transaksi t) {
        if (t != null) {
            this.daftarTransaksi.add(t);
        }
    }

    // Read: Mendapatkan semua entri
    public List<Transaksi> getAllTransaksi() {
        return new ArrayList<>(this.daftarTransaksi); // Mengembalikan salinan list
    }
    
    // Update: Mengubah entri
    public boolean ubahTransaksi(Transaksi transaksiLama, Transaksi transaksiBaru) {
        // Mencari index transaksi lama
        int index = daftarTransaksi.indexOf(transaksiLama);
        if (index != -1) {
            daftarTransaksi.set(index, transaksiBaru); // Mengganti objek
            return true;
        }
        return false;
    }

    // Delete: Menghapus entri
    public boolean hapusTransaksi(Transaksi t) {
        return daftarTransaksi.remove(t);
    }
    
    // --- METODE LAINNYA ---

    /**
     * Menghitung total saldo saat ini.
     * @return Saldo bersih (Pemasukan - Pengeluaran).
     */
    public double hitungSaldo() {
        return daftarTransaksi.stream()
               .mapToDouble(t -> t.getJenis().equals("Pemasukan") ? t.getJumlah() : -t.getJumlah())
               .sum();
    }
    
    // --- TANTANGAN (EKSPOR/IMPOR DATA JSON) ---

    // Ekspor Data ke JSON
    public boolean exportDataToJson(String pathFile) {
        try {
            // GsonBuilder dengan custom adapter untuk LocalDate
            Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
            
            try (FileWriter writer = new FileWriter(pathFile)) {
                writer.write(gson.toJson(this.daftarTransaksi));
                return true;
            }
        } catch (IOException e) {
            System.err.println("Error saat menulis file JSON: " + e.getMessage());
            return false;
        }
    }
    
    // Impor Data dari JSON
    public boolean importDataFromJson(String pathFile) {
        try {
            // Gson dengan custom adapter untuk LocalDate
            Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
            
            // TypeToken digunakan untuk menentukan tipe generik List<Transaksi> saat deserialisasi
            Type typeOfList = new TypeToken<List<Transaksi>>(){}.getType();
            
            try (FileReader reader = new FileReader(pathFile)) {
                List<Transaksi> importedList = gson.fromJson(reader, typeOfList);
                if (importedList != null) {
                    this.daftarTransaksi.clear(); 
                    this.daftarTransaksi.addAll(importedList);
                    return true;
                }
                return false;
            }
        } catch (IOException e) {
            System.err.println("Error saat membaca file JSON: " + e.getMessage());
            return false;
        }
    }
}