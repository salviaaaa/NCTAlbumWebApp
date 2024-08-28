package Controller;

import DB.DBConnection;
import Model.CartBean;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CartDAO {

    public Connection getConnection() {
        Connection connection = null;
        try {
            connection = new DBConnection().getConnection();
            return connection;
        } catch (SQLException ex) {
            Logger.getLogger(AlbumDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return connection;
    }

    public void addToCart(CartBean cart) throws SQLException {
        String query = "INSERT INTO tbl_cart (album_img, album_name, album_price, total_buy, username) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setBlob(1, cart.getAlbumImage());
            ps.setString(2, cart.getAlbumName());
            ps.setDouble(3, cart.getAlbumPrice());
            ps.setInt(4, cart.getQuantity());
            ps.setString(5, cart.getUsername());
            ps.executeUpdate();
        }
    }

    public List<CartBean> getCartItems(String username) throws SQLException {
        List<CartBean> cartItems = new ArrayList<>();
        String query = "SELECT c.*, a.stock FROM tbl_cart c JOIN tbl_album a ON c.album_name = a.album_name WHERE c.username = ?";
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CartBean Album = new CartBean();
                    Album.setId(rs.getInt("cart_id"));
                    Album.setAlbumName(rs.getString("album_name"));
                    Album.setAlbumPrice(rs.getDouble("album_price"));
                    Album.setQuantity(rs.getInt("total_buy"));
                    Album.setAlbumImage(rs.getBlob("album_img").getBinaryStream());
                    Album.setStock(rs.getInt("stock")); // Set stock
                    cartItems.add(Album);
                }
            }
        }
        return cartItems;
    }

    public List<CartBean> getSelectedItems(List<Integer> itemIds) throws SQLException {
        List<CartBean> selectedItems = new ArrayList<>();
        if (itemIds == null || itemIds.isEmpty()) {
            return selectedItems;
        }

        StringBuilder query = new StringBuilder("SELECT * FROM tbl_cart WHERE cart_id IN (");
        for (int i = 0; i < itemIds.size(); i++) {
            query.append("?");
            if (i < itemIds.size() - 1) {
                query.append(",");
            }
        }
        query.append(")");

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(query.toString())) {
            for (int i = 0; i < itemIds.size(); i++) {
                ps.setInt(i + 1, itemIds.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CartBean item = new CartBean();
                    item.setId(rs.getInt("cart_id"));
                    item.setAlbumName(rs.getString("album_name"));
                    item.setAlbumPrice(rs.getDouble("harga"));
                    item.setQuantity(rs.getInt("total_buy"));
                    item.setAlbumImage(rs.getBlob("album_img").getBinaryStream());
                    selectedItems.add(item);
                }
            }
        }
        return selectedItems;
    }

    public CartBean getCartItemById(int id) throws SQLException {
        String query = "SELECT * FROM tbl_cart WHERE cart_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CartBean item = new CartBean();
                    item.setId(rs.getInt("cart_id"));
                    item.setAlbumName(rs.getString("album_name"));
                    item.setAlbumPrice(rs.getDouble("album_price"));
                    item.setQuantity(rs.getInt("total_buy"));
                    item.setAlbumImage(rs.getBlob("album_img").getBinaryStream());
                    return item;
                }
            }
        }
        return null;
    }

    public double getTotalPrice(String username) throws SQLException {
        String query = "SELECT SUM(album_price * total_buy) as total FROM tbl_cart WHERE username = ?";
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        }
        return 0;
    }

    public void updateCartItem(int cartId, int quantity) throws SQLException {
        String query = "UPDATE tbl_cart SET total_buy = ? WHERE cart_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, quantity);
            ps.setInt(2, cartId);
            ps.executeUpdate();
        }
    }

    public void deleteCartItem(int cartId) throws SQLException {
        String query = "DELETE FROM tbl_cart WHERE cart_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, cartId);
            ps.executeUpdate();
        }
    }

    public boolean checkIfItemExists(String albumName, String username) throws SQLException {
        String query = "SELECT cart_id, total_buy FROM tbl_cart WHERE album_name = ? AND username = ?";
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1,albumName );
            ps.setString(2, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void updateItemQuantity(String AlbumName, String username, int quantity) throws SQLException {
        String query = "UPDATE tbl_cart SET total_buy = total_buy + ? WHERE album_name = ? AND username = ?";
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, quantity);
            ps.setString(2, AlbumName);
            ps.setString(3, username);
            ps.executeUpdate();
        }
    }

    public void updateItemSelection(int cartId, boolean selected) throws SQLException {
        String query = "UPDATE tbl_cart SET selected = ? WHERE cart_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setBoolean(1, selected);
            ps.setInt(2, cartId);
            ps.executeUpdate();
        }
    }

    public void clearCart(Map<String, Integer> selectedItemQuantities) throws SQLException {
        String query = "DELETE FROM tbl_cart WHERE album_name = ?";
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            for (Map.Entry<String, Integer> entry : selectedItemQuantities.entrySet()) {
                ps.setString(1, entry.getKey());
                ps.executeUpdate();
            }
        }
    }
}