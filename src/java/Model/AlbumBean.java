package Model;

import java.io.InputStream;
import java.util.Base64;
import java.io.ByteArrayOutputStream;

public class AlbumBean {
    private int id;
    private InputStream image;
    private String name;
    private double price;
    private String unit;
    private String description;
    private int stock;
    private String subUnit;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public InputStream getImage() { return image; }
    public void setImage(InputStream image) { this.image = image; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getSubUnit() {
        return subUnit;
    }

    public void setSubUnit(String subUnit) {
        this.subUnit = subUnit;
    }
    
    

    // Convert InputStream to Base64 for image display in HTML
    public String getImageBase64() {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = image.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            byte[] imageBytes = outputStream.toByteArray();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            outputStream.close();
            return base64Image;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
