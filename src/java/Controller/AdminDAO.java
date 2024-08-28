package Controller;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;
import DB.DBConnection;
import Model.AlbumBean;

public class AdminDAO {

    public boolean addalbum(AlbumBean album) {
        String query = "INSERT INTO tbl_album (album_img, album_name, album_price, unit, sub_unit, album_description, stock) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            if (album.getImage() == null || album.getName() == null || album.getUnit() == null || album.getSubUnit() == null || album.getDescription() == null || album.getStock() < 0) {
                return false;
            }
            pstmt.setBinaryStream(1, album.getImage());
            pstmt.setString(2, album.getName());
            pstmt.setDouble(3, album.getPrice());
            pstmt.setString(4, album.getUnit());
            pstmt.setString(5, album.getSubUnit());
            pstmt.setString(6, album.getDescription());
            pstmt.setInt(7, album.getStock());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatealbum(AlbumBean album) {
        String query = "UPDATE tbl_album SET ";
        boolean hasImage = album.getImage() != null;
        if (!hasImage && (album.getName() == null || album.getUnit() == null || album.getSubUnit() == null || album.getDescription() == null || album.getStock() < 0)) {
            return false;
        }
        if (hasImage) {
            query += "album_img = ?, ";
        }
        query += "album_name = ?, album_price = ?, unit = ?, sub_unit = ?, album_description = ?, stock = ? WHERE album_id = ?";
        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            int paramIndex = 1;
            if (hasImage) {
                pstmt.setBinaryStream(paramIndex++, album.getImage());
            }
            pstmt.setString(paramIndex++, album.getName());
            pstmt.setDouble(paramIndex++, album.getPrice());
            pstmt.setString(paramIndex++, album.getUnit());
            pstmt.setString(paramIndex++, album.getSubUnit());
            pstmt.setString(paramIndex++, album.getDescription());
            pstmt.setInt(paramIndex++, album.getStock());
            pstmt.setInt(paramIndex, album.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletealbum(int albumId) {
        String query = "DELETE FROM tbl_album WHERE album_id = ?";
        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, albumId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public AlbumBean getalbumById(int albumId) {
        String query = "SELECT * FROM tbl_album WHERE album_id = ?";
        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, albumId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    AlbumBean album = new AlbumBean();
                    album.setId(rs.getInt("album_id"));

                    Blob blob = rs.getBlob("album_img");
                    if (blob != null) {
                        InputStream inputStream = blob.getBinaryStream();
                        album.setImage(inputStream);
                    }

                    album.setName(rs.getString("album_name"));
                    album.setPrice(rs.getDouble("album_price"));
                    album.setUnit(rs.getString("unit"));
                    album.setSubUnit(rs.getString("sub_unit"));
                    album.setDescription(rs.getString("album_description"));
                    album.setStock(rs.getInt("stock"));
                    return album;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<AlbumBean> getAllalbums() {
        List<AlbumBean> albums = new ArrayList<>();
        String query = "SELECT * FROM tbl_album";
        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                AlbumBean album = new AlbumBean();
                album.setId(rs.getInt("album_id"));

                Blob blob = rs.getBlob("album_img");
                if (blob != null) {
                    InputStream inputStream = blob.getBinaryStream();
                    album.setImage(inputStream);
                }

                album.setName(rs.getString("album_name"));
                album.setPrice(rs.getDouble("album_price"));
                album.setUnit(rs.getString("unit"));
                album.setSubUnit(rs.getString("sub_unit"));
                album.setDescription(rs.getString("album_description"));
                album.setStock(rs.getInt("stock"));
                albums.add(album);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return albums;
    }

    public InputStream getalbumImageStream(int albumId) {
        String query = "SELECT album_img FROM tbl_album WHERE album_id = ?";
        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, albumId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBinaryStream("album_img");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static String formatSubUnit(String subUnit) {
        switch (subUnit) {
            case "aksi":
                return "Serial Aksi";
            case "cerpen":
                return "Cerpen";
            case "duniaParalel":
                return "Dunia Paralel";
            
            default:
                return subUnit;
        }
    }
    
}