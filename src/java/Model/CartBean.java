package Model;

import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.io.IOException;

public class CartBean {
    private int id;
    private int albumId;
    private String albumName;
    private double albumPrice;
    private InputStream albumImage;
    private int quantity;
    private String username;
    private int stock; 

    // Getters and Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getAlbumId() {
        return albumId;
    }
    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public String getAlbumName() {
        return albumName;
    }
    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public double getAlbumPrice() {
        return albumPrice;
    }
    public void setAlbumPrice(double albumPrice) {
        this.albumPrice = albumPrice;
    }

    public InputStream getAlbumImage() {
        return albumImage;
    }
    public void setAlbumImage(InputStream albumImage) {
        this.albumImage = albumImage;
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public int getStock() {
        return stock;
    }
    public void setStock(int stock) {
        this.stock = stock;
    }

    // Convert InputStream to Base64 for image display in HTML
    public String getImageBase64() {
        if (albumImage == null) {
            return "";
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = albumImage.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            byte[] imageBytes = outputStream.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
