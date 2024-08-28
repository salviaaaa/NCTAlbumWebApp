package Controller;

import DB.DBConnection;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;

public class CheckoutDAO {

    public Connection getConnection() {
        Connection connection = null;
        try {
            connection = new DBConnection().getConnection();
            return connection;
        } catch (SQLException ex) {
            Logger.getLogger(CheckoutDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return connection;
    }

    private String generateRandomPayment_Code() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder payment_code = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            payment_code.append(characters.charAt(random.nextInt(characters.length())));
        }
        return payment_code.toString();
    }

    public boolean placeOrder(String username, String name, String email, String city, String address, String postalCode, double total_price, String payment_method, Map<String, Integer> selectedItemQuantities, Timestamp expiryTime) throws SQLException {
        try (Connection connection = getConnection()) {
            if (connection == null) {
                return false;
            }

            connection.setAutoCommit(false); // Start transaction

            // Insert order details
            String insertOrderQuery = "INSERT INTO tbl_order (username, name, email, city, address, postal_code, total_price, payment_method, payment_code, expiry_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            String payment_code = generateRandomPayment_Code();
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertOrderQuery, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, name);
                preparedStatement.setString(3, email);
                preparedStatement.setString(4, city);
                preparedStatement.setString(5, address);
                preparedStatement.setString(6, postalCode);
                preparedStatement.setDouble(7, total_price);
                preparedStatement.setString(8, payment_method);
                preparedStatement.setString(9, payment_code);
                preparedStatement.setTimestamp(10, expiryTime);
                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows == 0) {
                    connection.rollback();
                    return false;
                }

                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int orderId = generatedKeys.getInt(1);

                        // Insert order items and reduce stock
                        String insertOrderItemsQuery = "INSERT INTO item_order (order_id, album_name, quantity) VALUES (?, ?, ?)";
                        String updateStockQuery = "UPDATE tbl_album SET stock = stock - ? WHERE album_name = ?";
                        try (PreparedStatement orderItemsStatement = connection.prepareStatement(insertOrderItemsQuery);
                             PreparedStatement updateStockStatement = connection.prepareStatement(updateStockQuery)) {
                            for (Map.Entry<String, Integer> entry : selectedItemQuantities.entrySet()) {
                                String albumName = entry.getKey();
                                int quantity = entry.getValue();

                                // Insert order item
                                orderItemsStatement.setInt(1, orderId);
                                orderItemsStatement.setString(2, albumName);
                                orderItemsStatement.setInt(3, quantity);
                                orderItemsStatement.addBatch();

                                // Reduce stock
                                updateStockStatement.setInt(1, quantity);
                                updateStockStatement.setString(2, albumName);
                                updateStockStatement.addBatch();
                            }

                            int[] batchResult = orderItemsStatement.executeBatch();
                            int[] stockUpdateResult = updateStockStatement.executeBatch();
                            for (int result : batchResult) {
                                if (result == Statement.EXECUTE_FAILED) {
                                    connection.rollback();
                                    return false;
                                }
                            }
                            for (int result : stockUpdateResult) {
                                if (result == Statement.EXECUTE_FAILED) {
                                    connection.rollback();
                                    return false; // Stock update failed
                                }
                            }
                        }
                    } else {
                        connection.rollback();
                        return false;
                    }
                }
            }
            connection.commit(); // Commit transaction
        } catch (SQLException e) {
            Logger.getLogger(CheckoutDAO.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
        return true;
    }
}