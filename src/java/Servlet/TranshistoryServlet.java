// TranshistoryServlet.java
package Servlet;

import Controller.TransactionDAO;
import Model.TransactionBean;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "TranshistoryServlet", urlPatterns = {"/TranshistoryServlet"})
public class TranshistoryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("uName");
        if (username == null) {
            response.sendRedirect("signin.jsp");
            return;
        }

        TransactionDAO TransactionDAO = new TransactionDAO();
        List<TransactionBean> transactionList = TransactionDAO.getTransactionByUsername(username);

        request.setAttribute("transactionList", transactionList);
        request.getRequestDispatcher("order.jsp").forward(request, response);
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
        return "TranshistoryServlet for handling transaction history requests.";
    }
}