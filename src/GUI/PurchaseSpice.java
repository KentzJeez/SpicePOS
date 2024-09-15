package GUI;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

public class PurchaseSpice extends javax.swing.JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/spice_business"; // Change as needed
    private static final String DB_USER = "root"; // Change as needed
    private static final String DB_PASSWORD = ""; // Change as needed
    private double buyingPrice = 0; // Store the buying price of the selected spice

    /**
     * Creates new form PurchaseSpice
     */
    public PurchaseSpice() {
        initComponents();
        populateSpiceDropdown(); // Populate the dropdown when the form is initialized
        
        // Add a listener to the spice dropdown to update the price label
        comboSpice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateSpicePrice(); // Update the price when a spice is selected
            }
        });
        
        // Add a listener to the quantity text field to update the total price
        txtQuantity.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                updateTotalPrice(); // Update total price when quantity changes
            }
        });

        btnPurchaseSpice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPurchaseSpiceActionPerformed(evt);
            }
        });
    }

    // Connect to the database
    private Connection connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    // Populate the spice dropdown
    private void populateSpiceDropdown() {
        try (Connection conn = connectToDatabase()) {
            if (conn != null) {
                String sql = "SELECT spice_name FROM spices";
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();

                DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
                while (rs.next()) {
                    model.addElement(rs.getString("spice_name"));
                }
                comboSpice.setModel(model);

                rs.close();
                stmt.close();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Handle the purchase action
    private void btnPurchaseSpiceActionPerformed(java.awt.event.ActionEvent evt) {       
        String selectedSpice = (String) comboSpice.getSelectedItem(); // Get selected spice
        String quantityStr = txtQuantity.getText(); // Get entered quantity

        // Validate that a spice is selected and quantity is entered
        if (selectedSpice == null || quantityStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a spice and enter quantity.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int quantity;
        try {
            // Ensure quantity is a positive integer
            if (!quantityStr.matches("\\d+")) {
                throw new NumberFormatException("Quantity must be a positive integer.");
            }
            quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                throw new NumberFormatException("Quantity must be positive.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid quantity. Please enter a positive integer.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Call method to update spice quantity in the database
        updateSpiceQuantity(selectedSpice, quantity);
    }

    // Update the spice quantity in the database by adding the purchased quantity
    private void updateSpiceQuantity(String spiceName, int quantity) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = connectToDatabase(); // Connect to database
            if (conn != null) {
                // SQL to update the spice quantity
                String sql = "UPDATE spices SET quantity = quantity + ? WHERE spice_name = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, quantity); // Add the purchased quantity to existing quantity
                stmt.setString(2, spiceName);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Spice purchased successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    this.dispose(); // Close the current window after success
                    Dashboard dashboard = new Dashboard(); // Assuming you have a Dashboard class
                    dashboard.setVisible(true); // Open the Dashboard window
                } else {
                    JOptionPane.showMessageDialog(this, "Spice not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // Print stack trace for debugging
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    // Fetch and update the spice price when a spice is selected
    private void updateSpicePrice() {
        String selectedSpice = (String) comboSpice.getSelectedItem(); // Get selected spice
        if (selectedSpice == null) return;

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = connectToDatabase();
            if (conn != null) {
                String sql = "SELECT buying_price FROM spices WHERE spice_name = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, selectedSpice);
                rs = stmt.executeQuery();
                
                if (rs.next()) {
                    buyingPrice = rs.getDouble("buying_price");
                    lblPrice.setText(String.valueOf(buyingPrice)); // Display the price in the label
                    updateTotalPrice(); // Recalculate the total price when the price is updated
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching price: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    // Update the total price label
    private void updateTotalPrice() {
        String quantityStr = txtQuantity.getText();
        if (!quantityStr.matches("\\d+") || buyingPrice == 0) {
            lblTotal.setText("0.00"); // If quantity is invalid or buying price is 0, display 0.00
            return;
        }

        int quantity = Integer.parseInt(quantityStr);
        double totalPrice = quantity * buyingPrice;
        lblTotal.setText(String.format("%.2f", totalPrice)); // Display the total price in lblTotal
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
        btnPurchaseSpice = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        lblPrice = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Purchase Spices");

        comboSpice.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel2.setText("Select Spice ");

        jLabel3.setText("Quantity");

        btnPurchaseSpice.setText("Purchase Spice");

        jLabel4.setText("Price");

        lblPrice.setText("                                ");

        jLabel6.setText("Total");

        lblTotal.setText("                              ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(168, 168, 168)
                        .addComponent(jLabel1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(133, 133, 133)
                        .addComponent(btnPurchaseSpice)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(71, 71, 71)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 88, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(comboSpice, 0, 96, Short.MAX_VALUE)
                        .addComponent(txtQuantity))
                    .addComponent(lblPrice)
                    .addComponent(lblTotal))
                .addGap(80, 80, 80))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel1)
                .addGap(59, 59, 59)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboSpice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(lblPrice))
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(txtQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(lblTotal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                .addComponent(btnPurchaseSpice)
                .addGap(25, 25, 25))
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
            java.util.logging.Logger.getLogger(PurchaseSpice.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PurchaseSpice.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PurchaseSpice.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PurchaseSpice.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PurchaseSpice().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnPurchaseSpice;
    private javax.swing.JComboBox<String> comboSpice;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblPrice;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JTextField txtQuantity;
    // End of variables declaration//GEN-END:variables
}
