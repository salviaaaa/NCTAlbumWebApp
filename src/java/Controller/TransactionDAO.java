package Controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import DB.DBConnection;
import java.io.InputStream;
import Model.TransactionBean;

public class TransactionDAO {

    public List<TransactionBean> getTransactionByUsername(String username) {
        String sql = "SELECT o.order_id, o.username, o.name, o.email, o.address, o.city, o.postal_code, o.payment_method, o.total_price, o.order_date, o.status, o.payment_code, o.expiry_time, o.stock_returned, " +
                     "GROUP_CONCAT(io.album_name SEPARATOR ', ') as album_names, GROUP_CONCAT(io.quantity SEPARATOR ', ') as quantities " +
                     "FROM tbl_order o " +
                     "JOIN item_order io ON o.order_id = io.order_id " +
                     "WHERE o.username = ? " +
                     "GROUP BY o.order_id";
        List<TransactionBean> transaksiList = new ArrayList<>();
        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                TransactionBean transaksi = new TransactionBean();
                transaksi.setOrderId(rs.getInt("order_id"));
                transaksi.setUsername(rs.getString("username"));
                transaksi.setName(rs.getString("name"));
                transaksi.setEmail(rs.getString("email"));
                transaksi.setAddress(rs.getString("address"));
                transaksi.setCity(rs.getString("city"));
                transaksi.setPostalCode(rs.getString("postal_code"));
                transaksi.setPaymentMethod(rs.getString("payment_method"));
                transaksi.setTotalPrice(rs.getDouble("total_price"));
                transaksi.setOrderDate(rs.getTimestamp("order_date"));
                transaksi.setStatus(rs.getString("status"));
                transaksi.setPaymentCode(rs.getString("payment_code"));
                transaksi.setExpiryTime(rs.getTimestamp("expiry_time")); // Set expiry time
                transaksi.setStockReturned(rs.getBoolean("stock_returned")); // Set stock_returned
                transaksi.setAlbumNames(rs.getString("album_names"));
                transaksi.setQuantities(rs.getString("quantities"));
                transaksiList.add(transaksi);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
        }
        return transaksiList;
    }

    public boolean updatePaymentStatus(int orderId, InputStream paymentProof) {
        String sql = "UPDATE tbl_order SET status = ?, proof_payment = ? WHERE order_id = ?";
        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "sudah_bayar");  // Set the status to "sudah_bayar"
            ps.setBlob(2, paymentProof);
            ps.setInt(3, orderId);
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

    public boolean updateStatus(int orderId, String status) {
        String sql = "UPDATE tbl_order SET status = ? WHERE order_id = ?";
        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0 && "gagal".equals(status)) {
                returnStock(orderId);
            }
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

    public void returnStock(int orderId) throws SQLException {
        String checkStockReturnedQuery = "SELECT stock_returned FROM tbl_order WHERE order_id = ?";
        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement checkPs = conn.prepareStatement(checkStockReturnedQuery)) {
            checkPs.setInt(1, orderId);
            ResultSet rs = checkPs.executeQuery();
            if (rs.next()) {
                boolean isStockReturned = rs.getBoolean("stock_returned");
                if (isStockReturned) {
                    // Stok sudah dikembalikan, tidak perlu mengembalikan lagi
                    return;
                }
            }
        }

        String sql = "SELECT album_name, quantity FROM item_order WHERE order_id = ?";
        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            String updateStockQuery = "UPDATE tbl_album SET stock = stock + ? WHERE album_name = ?";
            try (PreparedStatement updateStockPs = conn.prepareStatement(updateStockQuery)) {
                while (rs.next()) {
                    String albumName = rs.getString("album_name");
                    int quantity = rs.getInt("quantity");

                    updateStockPs.setInt(1, quantity);
                    updateStockPs.setString(2, albumName);
                    updateStockPs.executeUpdate();
                }
            }
        }

        // menandai stok sebagai sudah dikembalikan
        String markStockReturnedQuery = "UPDATE tbl_order SET stock_returned = TRUE WHERE order_id = ?";
        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement markPs = conn.prepareStatement(markStockReturnedQuery)) {
            markPs.setInt(1, orderId);
            markPs.executeUpdate();
        }
    }
}