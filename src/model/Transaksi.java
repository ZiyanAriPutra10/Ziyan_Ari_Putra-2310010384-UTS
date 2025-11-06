package model;

import java.time.LocalDate;


/**
 * Kelas Transaksi merepresentasikan entri pemasukan atau pengeluaran.
 * Menerapkan prinsip Encapsulation (data fields private).
 */
public class Transaksi {
    // Attribute (Encapsulation: private)
    private LocalDate tanggal;
    private String deskripsi;
    private double jumlah;
    private String jenis; // "Pemasukan" atau "Pengeluaran"

    // Constructor 1
    public Transaksi(LocalDate tanggal, String deskripsi, double jumlah, String jenis) {
        this.tanggal = tanggal;
        this.deskripsi = deskripsi;
        this.jumlah = jumlah;
        this.jenis = jenis;
    }

    // Constructor default (Wajib untuk deserialisasi Gson)
    public Transaksi() {}

    // Getter and Setter Method (Abstraction)
    public LocalDate getTanggal() { return tanggal; }
    public void setTanggal(LocalDate tanggal) { this.tanggal = tanggal; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    public double getJumlah() { return jumlah; }
    public void setJumlah(double jumlah) { this.jumlah = jumlah; }

    public String getJenis() { return jenis; }
    public void setJenis(String jenis) { this.jenis = jenis; }
    
    /**
     * Polymorphism: Overriding toString() untuk tampilan di JList.
     * @return String format transaksi.
     */
    @Override
    public String toString() {
        String tanda = jenis.equals("Pengeluaran") ? "-" : "+";
        // Menggunakan format mata uang Indonesia untuk jumlah
        return String.format("[%s] %s | %s%,.2f (%s)", 
                             tanggal, deskripsi, tanda, jumlah, jenis);
    }
}