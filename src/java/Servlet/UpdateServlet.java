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
 * UpdateServlet for handling update requests.
 */
@WebServlet(name = "UpdateServlet", urlPatterns = {"/UpdateServlet"})
public class UpdateServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String currentUsername = (String) session.getAttribute("uName");

        if (currentUsername == null) {
            response.sendRedirect("signin.jsp");
            return;
        }

        String newName = request.getParameter("name");
        String newEmail = request.getParameter("email");
        String newPassword = request.getParameter("password");
        String newCity = request.getParameter("city");
        String newAddress = request.getParameter("address");
        String newPostal_Code = request.getParameter("postal_code");

        UserDAO userDAO = new UserDAO();
        UserBean user = userDAO.getUserByUsername(currentUsername);

        if (user != null) {
            // Check for changes
            boolean hasChanges = false;

            // Check if the new email is already in use
            if (newEmail != null && !newEmail.equals(user.getEmail())) {
                if (userDAO.isEmailUsed(newEmail)) {
                    request.setAttribute("errorMessage", "Email sudah digunakan.");
                    request.getRequestDispatcher("update.jsp").forward(request, response);
                    return;
                }
                user.setEmail(newEmail);
                hasChanges = true;
            }
            if (newName != null && !newName.equals(user.getName())) {
                user.setName(newName);
                hasChanges = true;
            }
            if (newPassword != null && !newPassword.isEmpty() && !newPassword.equals(user.getPassword())) {
                user.setPassword(newPassword);
                hasChanges = true;
            }
            if (newCity != null && !newCity.equals(user.getCity())) {
                user.setCity(newCity);
                hasChanges = true;
            }
            if (newAddress != null && !newAddress.equals(user.getAddress())) {
                user.setAddress(newAddress);
                hasChanges = true;
            }
            if (newPostal_Code != null && !newPostal_Code.equals(user.getPostal_Code())) {
                user.setPostal_Code(newPostal_Code);
                hasChanges = true;
            }

            if (hasChanges) {
                boolean success = userDAO.updateUser(user);

                if (success) {
                    session.setAttribute("uName", user.getUsername());
                    response.sendRedirect("update.jsp?updateSuccess=true");
                } else {
                    response.sendRedirect("update.jsp?updateSuccess=false");
                }
            } else {
                response.sendRedirect("update.jsp?noChanges=true");
            }
        } else {
            response.sendRedirect("signin.jsp");
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
        return "UpdateServlet for handling update requests.";
    }
}