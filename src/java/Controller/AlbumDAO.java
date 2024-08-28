package Controller;

import Model.AlbumBean;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import DB.DBConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AlbumDAO {

    private Connection connection;

    public AlbumDAO() {
        try {
            this.connection = new DBConnection().getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(AlbumDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public AlbumBean getAlbumById(int id) throws SQLException {
        String query = "SELECT * FROM tbl_album WHERE album_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractAlbumFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public List<AlbumBean> getAllAlbums() throws SQLException {
        List<AlbumBean> albums = new ArrayList<>();
        String query = "SELECT * FROM tbl_album";
        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                albums.add(extractAlbumFromResultSet(rs));
            }
        }
        return albums;
    }

    public List<AlbumBean> searchAlbums(String query) {
        return searchAlbums(query, null, null, null);
    }

    public List<AlbumBean> searchAlbums(String query, String minPrice, String maxPrice, String stock) {
        List<AlbumBean> albums = new ArrayList<>();
        try {
            StringBuilder sql = new StringBuilder("SELECT * FROM tbl_album WHERE album_name LIKE ?");
            if (minPrice != null && !minPrice.isEmpty()) {
                sql.append(" AND album_price >= ").append(minPrice);
            }
            if (maxPrice != null && !maxPrice.isEmpty()) {
                sql.append(" AND album_price <= ").append(maxPrice);
            }
            if (stock != null && stock.equals("available")) {
                sql.append(" AND stock > 0");
            }

            PreparedStatement stmt = connection.prepareStatement(sql.toString());
            stmt.setString(1, "%" + query + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                albums.add(extractAlbumFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return albums;
    }

    private AlbumBean extractAlbumFromResultSet(ResultSet rs) throws SQLException {
        AlbumBean album = new AlbumBean();
        album.setId(rs.getInt("album_id"));
        String albumName = rs.getString("album_name");
        System.out.println("Album Name: " + albumName); // Added logging
        album.setName(albumName);
        album.setPrice(rs.getDouble("album_price"));
        album.setUnit(rs.getString("unit"));
        album.setSubUnit(rs.getString("sub_unit"));
        album.setDescription(rs.getString("album_description"));
        album.setImage(rs.getBinaryStream("album_img"));
        album.setStock(rs.getInt("stock"));
        return album;
    }

    public List<AlbumBean> searchAlbumsStartingWith(String query) {
        List<AlbumBean> albums = new ArrayList<>();
        String sql = "SELECT * FROM tbl_album WHERE album_name LIKE ?";
        try (Connection connection = new DBConnection().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, query + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    albums.add(extractAlbumFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return albums;
    }
}