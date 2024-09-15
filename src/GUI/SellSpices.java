package GUI;

import java.sql.*;
import javax.swing.*;
import java.text.DecimalFormat;

public class SellSpices extends javax.swing.JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/spice_business"; // Change as needed
    private static final String DB_USER = "root"; // Change as needed
    private static final String DB_PASSWORD = ""; // Change as needed

    private DecimalFormat df = new DecimalFormat("#.##"); // Format for price and total

    public SellSpices() {
        initComponents();
        populateSpiceDropdown(); // Populate the dropdown when the form is initialized

        comboSpice.addActionListener(evt -> spiceSelectedActionPerformed(evt)); // Handle spice selection

        txtQuantity.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                updateTotalPrice(); // Update total price as the user types the quantity
            }
        });
        
        btnSellSpice.addActionListener(evt -> btnSellSpiceActionPerformed(evt)); // Handle sell button click
    }

    // Populate the comboSpice dropdown with available spices from the database
    private void populateSpiceDropdown() {
        try (Connection conn = connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement("SELECT spice_name FROM spices");
             ResultSet rs = stmt.executeQuery()) {

            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            while (rs.next()) {
                model.addElement(rs.getString("spice_name"));
            }
            comboSpice.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching spices: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Handle spice selection and display selling price
    private void spiceSelectedActionPerformed(java.awt.event.ActionEvent evt) {
        String selectedSpice = (String) comboSpice.getSelectedItem();
        if (selectedSpice != null) {
            try (Connection conn = connectToDatabase()) {
                String sql = "SELECT selling_price FROM spices WHERE spice_name = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, selectedSpice);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    double sellingPrice = rs.getDouble("selling_price");
                    lblPrice.setText(df.format(sellingPrice)); // Display selling price
                }
                rs.close();
                stmt.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Update total price when quantity is entered or changed
    private void updateTotalPrice() {
        String quantityStr = txtQuantity.getText();
        String priceStr = lblPrice.getText();

        if (!quantityStr.isEmpty() && priceStr != null && !priceStr.isEmpty()) {
            try {
                int quantity = Integer.parseInt(quantityStr);
                double sellingPrice = Double.parseDouble(priceStr);
                double totalPrice = quantity * sellingPrice;
                lblTotal.setText(df.format(totalPrice)); // Display total price in lblTotal
            } catch (NumberFormatException e) {
                lblTotal.setText("0"); // Reset total price if input is invalid
            }
        } else {
            lblTotal.setText("0"); // Reset total if no quantity or price
        }
    }

    // Selling spice logic
    private void btnSellSpiceActionPerformed(java.awt.event.ActionEvent evt) {  
        String selectedSpice = (String) comboSpice.getSelectedItem();
        String quantityStr = txtQuantity.getText();
        
        if (selectedSpice == null || quantityStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a spice and enter quantity.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                throw new NumberFormatException("Quantity must be positive.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid quantity. Please enter a positive integer.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        updateSpiceQuantity(selectedSpice, quantity); // Update spice quantity in DB
    }

    // Update the spice quantity in the database
    private void updateSpiceQuantity(String spiceName, int quantity) {
        try (Connection conn = connectToDatabase()) {
            String checkSql = "SELECT quantity, selling_price FROM spices WHERE spice_name = ?";
            PreparedStatement stmt = conn.prepareStatement(checkSql);
            stmt.setString(1, spiceName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int availableQuantity = rs.getInt("quantity");
                double sellingPrice = rs.getDouble("selling_price");

                if (availableQuantity < quantity) {
                    JOptionPane.showMessageDialog(this, "Not enough quantity available.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double totalPrice = quantity * sellingPrice;
                JOptionPane.showMessageDialog(this, "Total Price: " + df.format(totalPrice), "Total Price", JOptionPane.INFORMATION_MESSAGE);

                String updateSql = "UPDATE spices SET quantity = quantity - ? WHERE spice_name = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setInt(1, quantity);
                updateStmt.setString(2, spiceName);

                int rowsAffected = updateStmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Spice sold successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    this.dispose(); // Close the current window
                    Dashboard dashboard = new Dashboard(); // Assuming you have a Dashboard class
                    dashboard.setVisible(true); // Open the Dashboard window
                } else {
                    JOptionPane.showMessageDialog(this, "Spice not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Spice not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Connect to the database
    private Connection connectToDatabase() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
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
        comboSpice = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtQuantity = new javax.swing.JTextField();
        btnSellSpice = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        lblPrice = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Sell Spices");

        comboSpice.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel2.setText("Select Spice ");

        jLabel3.setText("Quantity");

        btnSellSpice.setText("Sell Spice");

        jLabel4.setText("Price");

        lblPrice.setText("   ");

        jLabel5.setText("Total");

        lblTotal.setText("  ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(71, 71, 71)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3))
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 88, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(comboSpice, 0, 96, Short.MAX_VALUE)
                    .addComponent(txtQuantity)
                    .addComponent(lblPrice, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(80, 80, 80))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(168, 168, 168)
                        .addComponent(jLabel1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(151, 151, 151)
                        .addComponent(btnSellSpice)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel1)
                .addGap(35, 35, 35)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(comboSpice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(lblPrice))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(lblTotal))
                .addGap(45, 45, 45)
                .addComponent(btnSellSpice)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
            java.util.logging.Logger.getLogger(SellSpices.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SellSpices.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SellSpices.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SellSpices.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SellSpices().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSellSpice;
    private javax.swing.JComboBox<String> comboSpice;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblPrice;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JTextField txtQuantity;
    // End of variables declaration//GEN-END:variables
}
