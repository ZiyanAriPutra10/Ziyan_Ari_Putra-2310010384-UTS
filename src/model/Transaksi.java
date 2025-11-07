package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
    
    private static final DateTimeFormatter formatter = 
        DateTimeFormatter.ofPattern("dd-MM-yyyy");

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
    
    public String getTanggalFormatted() {
        return tanggal.format(formatter);
    }
    
    /**
     * Polymorphism: Overriding toString() untuk tampilan di JList.
     * @return String format transaksi.
     */
    @Override
    public String toString() {
        String tanda = jenis.equals("Pengeluaran") ? "➖" : "➕";
        String warna = jenis.equals("Pengeluaran") ? "MERAH" : "HIJAU";
        return String.format("%s %s | Rp%,.2f | %s", 
                           tanda, deskripsi, jumlah, getTanggalFormatted());
    }
    
    /**
     * Method untuk keperluan display di GUI
     */
    public String toDisplayString() {
        return String.format("<html><b>%s</b> - Rp%,.2f<br><small>%s | %s</small></html>", 
                           deskripsi, jumlah, getTanggalFormatted(), jenis);
    }
}