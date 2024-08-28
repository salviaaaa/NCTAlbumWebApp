package Servlet;

import Controller.CartDAO;
import Model.CartBean;
import Controller.AlbumDAO;
import Model.AlbumBean;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

/**
 * AddToCartServlet for handling adding albums to the cart.
 */
@WebServlet(name = "AddToCartServlet", urlPatterns = {"/AddToCartServlet"})
public class AddToCartItemServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("uName");
        if (username == null) {
            response.sendRedirect("signin.jsp");
            return;
        }

        String albumIdParam = request.getParameter("albumId");
        String albumName = request.getParameter("albumName");
        String albumPriceParam = request.getParameter("albumPrice");

        // Debugging: print received parameters
        System.out.println("albumId: " + albumIdParam);
        System.out.println("albumName: " + albumName);
        System.out.println("albumPrice: " + albumPriceParam);

        if (albumIdParam == null || albumIdParam.isEmpty() ||
            albumName == null || albumName.isEmpty() ||
            albumPriceParam == null || albumPriceParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
            return;
        }

        int albumId = Integer.parseInt(albumIdParam);
        double albumPrice = Double.parseDouble(albumPriceParam);

        AlbumDAO albumDAO = new AlbumDAO();
        InputStream albumImage = null;
        try {
            AlbumBean album = albumDAO.getAlbumById(albumId);
            if (album != null) {
                albumImage = album.getImage();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while fetching the album image.");
            return;
        }

        if (albumImage == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "album image not found");
            return;
        }

        CartBean cartItem = new CartBean();
        cartItem.setAlbumId(albumId);
        cartItem.setAlbumName(albumName);
        cartItem.setAlbumPrice(albumPrice);
        cartItem.setAlbumImage(albumImage);
        cartItem.setQuantity(1); // Default quantity to 1
        cartItem.setUsername(username);

        CartDAO cartDAO = new CartDAO();

        try {
            if (cartDAO.checkIfItemExists(albumName, username)) {
                cartDAO.updateItemQuantity(albumName, username, 1);
            } else {
                cartDAO.addToCart(cartItem);
            }
            response.sendRedirect("cart.jsp");
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while adding the album to the cart.");
        }
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
        return "AddToCartServlet for handling adding albums to the cart.";
    }
}