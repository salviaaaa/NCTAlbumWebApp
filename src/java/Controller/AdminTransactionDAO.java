package Controller;

import DB.DBConnection;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import Model.TransactionBean;

public class AdminTransactionDAO {

    public List<TransactionBean> getAllTransaction() {
        String sql = "SELECT o.order_id, o.username, o.name, o.email, o.city, o.address, o.postal_code, o.payment_method, o.total_price, o.order_date, " +
                     "GROUP_CONCAT(oi.album_name SEPARATOR ', ') as album_names, GROUP_CONCAT(oi.kuantitas SEPARATOR ', ') as quantities, " +
                     "o.status " +
                     "FROM orders o " +
                     "JOIN item_order oi ON o.order_id = oi.order_id " +
                     "GROUP BY o.order_id";
        List<TransactionBean> transactionList = new ArrayList<>();
        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                TransactionBean transaction = new TransactionBean();
                transaction.setOrderId(rs.getInt("order_id"));
                transaction.setUsername(rs.getString("username"));
                transaction.setName(rs.getString("name"));
                transaction.setEmail(rs.getString("email"));
                transaction.setCity(rs.getString("city"));
                transaction.setAddress(rs.getString("address"));
                transaction.setPostalCode(rs.getString("postal_code"));
                transaction.setPaymentMethod(rs.getString("payment_method"));
                transaction.setTotalPrice(rs.getDouble("total_price"));
                transaction.setOrderDate(rs.getTimestamp("order_date"));
                transaction.setAlbumNames(rs.getString("album_name"));
                transaction.setQuantities(rs.getString("quantities"));
                transaction.setStatus(rs.getString("status"));
                transactionList.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
        }
        return transactionList;
    }

    public boolean updateStatus(int orderId, String paymentStatus) {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, paymentStatus);
            ps.setInt(2, orderId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
            return false;
        }
    }

    public InputStream getPaymentProof(int orderId) {
        String sql = "SELECT proof_payment FROM tbl_order WHERE order_id = ?";
        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBlob("proof_payment").getBinaryStream();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
        }
        return null;
    }
}
