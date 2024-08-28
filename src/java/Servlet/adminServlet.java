package Servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import Controller.AdminDAO;
import Model.AlbumBean;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@WebServlet(name = "adminServlet", urlPatterns = {"/adminServlet"})
@MultipartConfig
public class adminServlet extends HttpServlet {

    private AdminDAO AdminDAO;

    @Override
    public void init() throws ServletException {
        AdminDAO = new AdminDAO();
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if (action == null) {
            action = "list";
        }

        try {
            switch (action) {
                case "add":
                    addalbum(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "update":
                    updatealbum(request, response);
                    break;
                case "delete":
                    deletealbum(request, response);
                    break;
                case "list":
                default:
                    listalbums(request, response);
                    break;
            }
        } catch (IOException | ServletException ex) {
            throw new ServletException(ex);
        }
    }

    private void addalbum(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Part albumImage = request.getPart("albumImage");
        String album_name = request.getParameter("album_name");
        String album_priceStr = request.getParameter("album_price");
        String unit = request.getParameter("unit");
        String subUnit = request.getParameter("subUnit");
        String album_description = request.getParameter("album_description");
        String stockStr = request.getParameter("stock");

        double album_price = 0;
        int stock = 0;
        try {
            album_price = Double.parseDouble(album_priceStr);
            stock = Integer.parseInt(stockStr);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid input for price or stock.");
            listalbums(request, response);
            return;
        }

        InputStream albumImageStream = (albumImage != null && albumImage.getSize() > 0) ? albumImage.getInputStream() : null;

        AlbumBean album = new AlbumBean();
        album.setName(album_name);
        album.setPrice(album_price);
        album.setUnit(unit);
        album.setSubUnit(subUnit);
        album.setDescription(album_description);
        album.setStock(stock);
        if (albumImageStream != null) {
            album.setImage(albumImageStream);
        }

        boolean success = AdminDAO.addalbum(album);
        if (success) {
            request.setAttribute("message", "Album added successfully");
        } else {
            request.setAttribute("errorMessage", "Failed to add album");
        }
        listalbums(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int albumId = Integer.parseInt(request.getParameter("albumId"));
        AlbumBean existingAlbum = AdminDAO.getalbumById(albumId);
        request.setAttribute("album", existingAlbum);
        request.getRequestDispatcher("editAlbumForm.jsp").forward(request, response);
    }

    private void updatealbum(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        int id = Integer.parseInt(request.getParameter("albumId"));
        String album_name = request.getParameter("album_name");
        String album_priceStr = request.getParameter("album_price");
        String unit = request.getParameter("unit");
        String subUnit = request.getParameter("subUnit");
        String album_description = request.getParameter("album_description");
        String stockStr = request.getParameter("stock");

        double album_price = Double.parseDouble(album_priceStr);
        int stock = Integer.parseInt(stockStr);

        Part albumImage = request.getPart("albumImage");
        InputStream albumImageStream = (albumImage != null && albumImage.getSize() > 0) ? albumImage.getInputStream() : null;

        AlbumBean album = new AlbumBean();
        album.setId(id);
        if (albumImageStream != null) {
            album.setImage(albumImageStream);
        }
        album.setName(album_name);
        album.setPrice(album_price);
        album.setUnit(unit);
        album.setSubUnit(subUnit);
        album.setDescription(album_description);
        album.setStock(stock);

        boolean success = AdminDAO.updatealbum(album);
        if (success) {
            request.setAttribute("message", "Album updated successfully");
        } else {
            request.setAttribute("errorMessage", "Failed to update album");
        }
        listalbums(request, response);
    }

    private void deletealbum(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        int albumId = Integer.parseInt(request.getParameter("albumId"));

        boolean success = AdminDAO.deletealbum(albumId);
        if (success) {
            request.setAttribute("message", "Album deleted successfully");
        } else {
            request.setAttribute("errorMessage", "Failed to delete album");
        }
        listalbums(request, response);
    }

    private void listalbums(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<AlbumBean> albums = AdminDAO.getAllalbums();
        request.setAttribute("albums", albums);
        request.getRequestDispatcher("admin.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "adminServlet to handle album management";
    }
}