package Servlet;

import Controller.UserDAO;
import Model.SignUpBean;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * RegisterServlet for handling registration requests.
 */
@WebServlet(name = "SignUpServlet", urlPatterns = {"/SignUpServlet"})
public class SignUpServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SignUpBean user = new SignUpBean();
        user.setName(request.getParameter("name"));
        user.setUsername(request.getParameter("username"));
        user.setEmail(request.getParameter("email"));
        user.setPassword(request.getParameter("password"));

        // Logging input values
        System.out.println("Name: " + user.getName());
        System.out.println("Username: " + user.getUsername());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Password: " + user.getPassword());

        UserDAO userDAO = new UserDAO();
        String registrationStatus = userDAO.SignUpUser(user);

        // Logging registration status
        System.out.println("Registration Status: " + registrationStatus);

        if ("success".equals(registrationStatus)) {
            request.setAttribute("registrationStatus", "success");
        } else if ("emailUsed".equals(registrationStatus)) {
            request.setAttribute("registrationStatus", "emailUsed");
        } else {
            request.setAttribute("registrationStatus", "failure");
        }

        request.getRequestDispatcher("signup.jsp").forward(request, response);
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
        return "RegisterServlet for handling registration requests.";
    }
}