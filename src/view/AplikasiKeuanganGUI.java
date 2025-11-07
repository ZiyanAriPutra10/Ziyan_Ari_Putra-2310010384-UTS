package view;

import service.KeuanganManager;
import model.Transaksi;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Aplikasi Keuangan Pribadi - GUI dengan JTable untuk menampilkan data
 */
public class AplikasiKeuanganGUI extends javax.swing.JFrame {

    // PROPERTI APLIKASI
    private KeuanganManager manager = new KeuanganManager();
    private DefaultTableModel tableModel;
    
    /**
     * Creates new form AplikasiKeuanganGUI
     */
    public AplikasiKeuanganGUI() {
        initComponents();
        setupComponents();
        loadData();
    }
    
    private void setupComponents() {
        // Setup ComboBox
        cmbJenis.removeAllItems();
        cmbJenis.addItem("Pemasukan");
        cmbJenis.addItem("Pengeluaran");
        
        // Set tanggal default
        txtTanggal.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        
        // Setup JTable dengan model yang benar
        setupTable();
        
        // Styling improvements
        styleComponents();
    }
    
    private void setupTable() {
        // Define column names
        String[] columnNames = {"No", "Tanggal", "Deskripsi", "Jenis", "Jumlah (Rp)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        jTableTransaksi.setModel(tableModel);
        
        // Set column widths
        jTableTransaksi.getColumnModel().getColumn(0).setPreferredWidth(50);  // No
        jTableTransaksi.getColumnModel().getColumn(1).setPreferredWidth(100); // Tanggal
        jTableTransaksi.getColumnModel().getColumn(2).setPreferredWidth(200); // Deskripsi
        jTableTransaksi.getColumnModel().getColumn(3).setPreferredWidth(100); // Jenis
        jTableTransaksi.getColumnModel().getColumn(4).setPreferredWidth(150); // Jumlah
        
        // Set table properties
        jTableTransaksi.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTableTransaksi.setRowHeight(25);
        jTableTransaksi.setShowGrid(true);
        jTableTransaksi.setGridColor(Color.LIGHT_GRAY);
        jTableTransaksi.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Custom renderer for amount column
        jTableTransaksi.getColumnModel().getColumn(4).setCellRenderer(new AmountRenderer());
        
        // Custom renderer for type column
        jTableTransaksi.getColumnModel().getColumn(3).setCellRenderer(new TypeRenderer());
    }
    
    // Custom cell renderer for amount column
    private class AmountRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            // Format number with currency
            if (value instanceof Double) {
                setText(String.format("%,.2f", (Double) value));
            }
            
            // Color based on transaction type
            String jenis = (String) table.getValueAt(row, 3);
            if (jenis.equals("Pemasukan")) {
                setForeground(new Color(0, 128, 0)); // Green for income
                setFont(getFont().deriveFont(Font.BOLD));
            } else {
                setForeground(new Color(200, 0, 0)); // Red for expense
                setFont(getFont().deriveFont(Font.BOLD));
            }
            
            return c;
        }
    }
    
    // Custom cell renderer for type column
    private class TypeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (value instanceof String) {
                String jenis = (String) value;
                if (jenis.equals("Pemasukan")) {
                    setBackground(new Color(220, 255, 220));
                    setForeground(new Color(0, 100, 0));
                } else {
                    setBackground(new Color(255, 220, 220));
                    setForeground(new Color(139, 0, 0));
                }
            }
            
            return c;
        }
    }
    
    private void styleComponents() {
        // Style untuk header
        jLabel1.setFont(new Font("Segoe UI", Font.BOLD, 20));
        jLabel1.setForeground(new Color(0, 70, 140));
        
        // Style untuk buttons
        styleButton(btnTambah, new Color(76, 175, 80));
        styleButton(btnUbah, new Color(33, 150, 243));
        styleButton(btnHapus, new Color(244, 67, 54));
        styleButton(btnClear, new Color(158, 158, 158));
        styleButton(btnImpor, new Color(255, 152, 0));
        styleButton(btnEkspor, new Color(156, 39, 176));
        
        // Style untuk saldo label
        lblSaldo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSaldo.setForeground(new Color(0, 100, 0));
        
        // Style untuk panel
        jPanel1.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        jPanel1.setBackground(new Color(245, 245, 245));
    }
    
    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
    }
    
    private void loadData() {
        updateTampilan();
    }
    
    /**
     * Memperbarui tampilan JTable dan Label Saldo.
     */
    private void updateTampilan() {
        tableModel.setRowCount(0); // Kosongkan table terlebih dahulu
        
        List<Transaksi> semuaTransaksi = manager.getAllTransaksi();
        int nomor = 1;
        for (Transaksi t : semuaTransaksi) {
            tableModel.addRow(new Object[]{
                nomor++,
                t.getTanggalFormatted(),
                t.getDeskripsi(),
                t.getJenis(),
                t.getJumlah()
            });
        }
        
        // Update label saldo dengan format yang lebih baik
        double saldo = manager.hitungSaldo();
        lblSaldo.setText("SALDO SAAT INI: Rp " + String.format("%,.2f", saldo));
        
        // Update warna saldo
        if (saldo >= 0) {
            lblSaldo.setForeground(new Color(0, 128, 0));
        } else {
            lblSaldo.setForeground(new Color(255, 0, 0));
        }
    }
    
    /**
     * Mengosongkan input fields setelah operasi.
     */
    private void clearInputFields() {
        txtTanggal.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))); 
        txtDeskripsi.setText("");
        txtJumlah.setText("");
        cmbJenis.setSelectedIndex(0);
        jTableTransaksi.clearSelection();
    }
    
    private boolean validasiInput() {
        // Validasi tanggal
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate.parse(txtTanggal.getText().trim(), formatter);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, 
                "Format tanggal tidak valid! Gunakan format dd-MM-yyyy", 
                "Error Validasi", JOptionPane.ERROR_MESSAGE);
            txtTanggal.requestFocus();
            return false;
        }
        
        // Validasi deskripsi
        if (txtDeskripsi.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Deskripsi tidak boleh kosong!", 
                "Error Validasi", JOptionPane.ERROR_MESSAGE);
            txtDeskripsi.requestFocus();
            return false;
        }
        
        // Validasi jumlah
        try {
            double jumlah = Double.parseDouble(txtJumlah.getText().trim().replace(",", ""));
            if (jumlah <= 0) {
                JOptionPane.showMessageDialog(this, 
                    "Jumlah harus lebih besar dari 0!", 
                    "Error Validasi", JOptionPane.ERROR_MESSAGE);
                txtJumlah.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Jumlah harus berupa angka yang valid!", 
                "Error Validasi", JOptionPane.ERROR_MESSAGE);
            txtJumlah.requestFocus();
            return false;
        }
        
        return true;
    }



    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabelTanggal = new javax.swing.JLabel();
        txtTanggal = new javax.swing.JTextField();
        jLabelDeskripsi = new javax.swing.JLabel();
        txtDeskripsi = new javax.swing.JTextField();
        jLabelJumlah = new javax.swing.JLabel();
        jLabelJenis = new javax.swing.JLabel();
        txtJumlah = new javax.swing.JTextField();
        cmbJenis = new javax.swing.JComboBox<>();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableTransaksi = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        lblSaldo = new javax.swing.JLabel();
        btnEkspor = new javax.swing.JButton();
        btnImpor = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        btnHapus = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        btnUbah = new javax.swing.JButton();
        btnTambah = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("APLIKASI KEUANGAN PRIBADI");

        jLabelTanggal.setText("Tanggal (yyyy-MM-dd)");

        jLabelDeskripsi.setText("Deskripsi/Keterangan");

        jLabelJumlah.setText("Jumlah (Rp)");

        jLabelJenis.setText("Jenis Transaksi");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(58, 58, 58)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelDeskripsi)
                    .addComponent(jLabelJumlah)
                    .addComponent(jLabelJenis)
                    .addComponent(jLabelTanggal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 149, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDeskripsi, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbJenis, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(80, 80, 80))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelTanggal)
                    .addComponent(txtTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelDeskripsi)
                    .addComponent(txtDeskripsi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelJumlah))
                .addGap(21, 21, 21)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelJenis)
                    .addComponent(cmbJenis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTableTransaksi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTableTransaksi);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(35, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblSaldo.setText("Saldo Saat Ini: Rp 0.00");

        btnEkspor.setText("Ekspor JSON");
        btnEkspor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEksporActionPerformed(evt);
            }
        });

        btnImpor.setText(" Impor JSON");
        btnImpor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImporActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnImpor)
                .addGap(40, 40, 40)
                .addComponent(lblSaldo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnEkspor)
                .addGap(17, 17, 17))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnImpor)
                    .addComponent(lblSaldo)
                    .addComponent(btnEkspor))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        btnHapus.setText("Hapus");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });

        btnClear.setText("Clear");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        btnUbah.setText("Ubah");
        btnUbah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUbahActionPerformed(evt);
            }
        });

        btnTambah.setText("Tambah");
        btnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addComponent(btnTambah, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnUbah, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addComponent(btnHapus, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(67, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnTambah)
                    .addComponent(btnUbah)
                    .addComponent(btnHapus)
                    .addComponent(btnClear))
                .addContainerGap(8, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(44, 44, 44))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(122, 122, 122)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(36, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 22, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        // TODO add your handling code here:
        int selectedIndex = jTableTransaksi.getSelectedRow();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Pilih entri yang ingin dihapus.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String deskripsi = (String) tableModel.getValueAt(selectedIndex, 2);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Yakin hapus transaksi: " + deskripsi + "?", 
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (manager.hapusTransaksi(selectedIndex)) {
                updateTampilan();
                clearInputFields();
                JOptionPane.showMessageDialog(this, "Transaksi berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnUbahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUbahActionPerformed
        // TODO add your handling code here:
         int selectedIndex = jTableTransaksi.getSelectedRow();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Pilih entri yang ingin diubah.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!validasiInput()) return;
        
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate tanggal = LocalDate.parse(txtTanggal.getText().trim(), formatter);
            String deskripsi = txtDeskripsi.getText().trim();
            double jumlah = Double.parseDouble(txtJumlah.getText().trim().replace(",", ""));
            String jenis = (String) cmbJenis.getSelectedItem();

            Transaksi transaksiBaru = new Transaksi(tanggal, deskripsi, jumlah, jenis);
            
            if (manager.ubahTransaksi(selectedIndex, transaksiBaru)) {
                updateTampilan();
                clearInputFields();
                JOptionPane.showMessageDialog(this, "Transaksi berhasil diubah!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Data input tidak valid. Pastikan format benar.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnUbahActionPerformed

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
        // TODO add your handling code here:
        if (!validasiInput()) return;
        
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate tanggal = LocalDate.parse(txtTanggal.getText().trim(), formatter);
            String deskripsi = txtDeskripsi.getText().trim();
            double jumlah = Double.parseDouble(txtJumlah.getText().trim().replace(",", ""));
            String jenis = (String) cmbJenis.getSelectedItem();

            Transaksi transaksiBaru = new Transaksi(tanggal, deskripsi, jumlah, jenis);
            manager.tambahTransaksi(transaksiBaru);
            updateTampilan();
            clearInputFields();
            JOptionPane.showMessageDialog(this, "Transaksi berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Jumlah harus berupa angka yang valid.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Format Tanggal tidak valid (Gunakan dd-MM-yyyy).", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnTambahActionPerformed

    private void btnImporActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImporActionPerformed
        // TODO add your handling code here:
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Pilih File JSON untuk Diimpor");
        fileChooser.setFileFilter(new FileNameExtensionFilter("JSON Files", "json"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            if (manager.importDataFromJson(path)) {
                updateTampilan();
                JOptionPane.showMessageDialog(this, "Data berhasil diimpor!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Impor Gagal! Pastikan file JSON valid.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnImporActionPerformed

    private void btnEksporActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEksporActionPerformed
        // TODO add your handling code here:
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan Transaksi sebagai JSON");
        fileChooser.setFileFilter(new FileNameExtensionFilter("JSON Files", "json"));
        fileChooser.setSelectedFile(new File("KeuanganPribadi_Export.json"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            if (!path.toLowerCase().endsWith(".json")) {
                path += ".json";
            }
            
            if (manager.exportDataToJson(path)) {
                JOptionPane.showMessageDialog(this, "Data berhasil diekspor ke: " + path, "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Ekspor Gagal!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnEksporActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        // TODO add your handling code here:
        clearInputFields();
    }//GEN-LAST:event_btnClearActionPerformed

    private void jTableTransaksiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableTransaksiMouseClicked
    int selectedIndex = jTableTransaksi.getSelectedRow();
    if (selectedIndex >= 0) {
        // Get data from selected row
        String tanggal = (String) tableModel.getValueAt(selectedIndex, 1);
        String deskripsi = (String) tableModel.getValueAt(selectedIndex, 2);
        String jenis = (String) tableModel.getValueAt(selectedIndex, 3);
        Double jumlah = (Double) tableModel.getValueAt(selectedIndex, 4);
        
        // Fill the input fields
        txtTanggal.setText(tanggal);
        txtDeskripsi.setText(deskripsi);
        txtJumlah.setText(String.format("%.2f", jumlah));
        cmbJenis.setSelectedItem(jenis);
    }
}//GEN-LAST:event_jTableTransaksiMouseClicked
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AplikasiKeuanganGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AplikasiKeuanganGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AplikasiKeuanganGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AplikasiKeuanganGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AplikasiKeuanganGUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnEkspor;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnImpor;
    private javax.swing.JButton btnTambah;
    private javax.swing.JButton btnUbah;
    private javax.swing.JComboBox<String> cmbJenis;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelDeskripsi;
    private javax.swing.JLabel jLabelJenis;
    private javax.swing.JLabel jLabelJumlah;
    private javax.swing.JLabel jLabelTanggal;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableTransaksi;
    private javax.swing.JLabel lblSaldo;
    private javax.swing.JTextField txtDeskripsi;
    private javax.swing.JTextField txtJumlah;
    private javax.swing.JTextField txtTanggal;
    // End of variables declaration//GEN-END:variables

}
