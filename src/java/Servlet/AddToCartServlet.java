package Servlet;

import DB.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "AddToCartServlet", urlPatterns = {"/AddToCartServlet"})
public class AddToCartServlet extends HttpServlet {

    @Override
protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    response.setContentType("application/json");
    PrintWriter out = response.getWriter();
    
    // Retrieve album details from request parameters
    String albumId = request.getParameter("albumId");
    String albumName = request.getParameter("albumName");
    String albumPrice = request.getParameter("albumPrice");

    // Debugging: print received parameters
    System.out.println("albumId: " + albumId);
    System.out.println("albumName: " + albumName);
    System.out.println("albumPrice: " + albumPrice);

    // Check if parameters are null or empty
    if (albumId == null || albumName == null || albumPrice == null) {
        out.write("{\"status\":\"error\", \"message\":\"Missing parameters.\"}");
        return;
    }

    // Retrieve user session and user ID
    HttpSession session = request.getSession();
    String userId = (String) session.getAttribute("userId");

    if (userId == null) {
        // User is not logged in
        out.write("{\"status\":\"error\", \"message\":\"User is not logged in.\"}");
        return;
    }

    // Database connection
    Connection conn = null;
    PreparedStatement pstmt = null;

    try {
        conn = new DBConnection().setConnection();
        String sql = "INSERT INTO tbl_cart (userId, albumId, albumName, albumPrice) VALUES (?, ?, ?, ?)";
        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, userId);
        pstmt.setString(2, albumId);
        pstmt.setString(3, albumName);
        pstmt.setString(4, albumPrice);

        int rowsAffected = pstmt.executeUpdate();

        if (rowsAffected > 0) {
            out.write("{\"status\":\"success\", \"message\":\"Album added to cart.\"}");
        } else {
            out.write("{\"status\":\"error\", \"message\":\"Failed to add album to cart.\"}");
        }

    } catch (SQLException e) {
        e.printStackTrace();
        out.write("{\"status\":\"error\", \"message\":\"Database error: " + e.getMessage() + "\"}");
    } finally {
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
}
    

