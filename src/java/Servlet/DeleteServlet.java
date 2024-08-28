package Servlet;

import Controller.UserDAO;
import Model.UserBean;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * DeleteServlet for handling delete account requests.
 */
@WebServlet(name = "DeleteServlet", urlPatterns = {"/DeleteServlet"})
public class DeleteServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String username = (String) session.getAttribute("uName");

        if (username == null) {
            response.sendRedirect("signin.jsp");
            return;
        }

        UserDAO userDAO = new UserDAO();
        boolean success = userDAO.deleteUser(username);

        if (success) {
            session.invalidate(); // Invalidate session after deleting the account
            response.sendRedirect("signin.jsp");
        } else {
            response.sendRedirect("detail.jsp?error=true");
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
        return "DeleteServlet for handling delete account requests.";
    }
}
