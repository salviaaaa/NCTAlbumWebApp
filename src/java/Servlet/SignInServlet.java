package Servlet;

import DB.DBConnection;
import Model.SignInBean;
import java.sql.*;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * LoginServlet for handling login requests.
 */
@WebServlet(name = "SignInServlet", urlPatterns = {"/SignInServlet"})
public class SignInServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
         // Create a new SignInBean object and set its properties from the request parameters
        SignInBean login = new SignInBean();
        login.setUsername(request.getParameter("username"));
        login.setPassword(request.getParameter("password"));

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = new DBConnection().setConnection();

            // Check if user is an admin
            String adminSql = "SELECT * FROM tbl_admin WHERE username=? AND password=?";
            ps = conn.prepareStatement(adminSql);
            ps.setString(1, login.getUsername());
            ps.setString(2, login.getPassword());

            rs = ps.executeQuery();
            if (rs.next()) {
                HttpSession session = request.getSession();
                session.setAttribute("user", login.getUsername());
                session.setAttribute("login", true);
                session.setAttribute("uName", login.getUsername());
       
                // Set signinSuccess attribute to true
                request.setAttribute("signinSuccess", true);

                // Forward to the admin page
                RequestDispatcher rd = request.getRequestDispatcher("admin.jsp");
                rd.forward(request, response);
            } 
            else {
                // Check if user is a regular user
                String userSql = "SELECT * FROM tbl_user WHERE username=? AND password=?";
                ps = conn.prepareStatement(userSql);
                ps.setString(1, login.getUsername());
                ps.setString(2, login.getPassword());

                rs = ps.executeQuery();
                if (rs.next()) {
                    HttpSession session = request.getSession();
                    session.setAttribute("user", login.getUsername());
                    session.setAttribute("login", true);
                    session.setAttribute("uName", login.getUsername());
                    
                    // Set signinSuccess attribute to true
                    request.setAttribute("signinSuccess", true);

                    // Forward to the user page
                    RequestDispatcher rd = request.getRequestDispatcher("signin.jsp");
                    rd.forward(request, response);
                } else {
                    // Set signinSuccess attribute to false
                    request.setAttribute("signinSuccess", false);

                    // Forward to the login page to show the error modal
                    RequestDispatcher rd = request.getRequestDispatcher("signin.jsp");
                    rd.forward(request, response);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("eror.jsp");
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
        return "SignInServlet for handling login requests.";
    }
    
}