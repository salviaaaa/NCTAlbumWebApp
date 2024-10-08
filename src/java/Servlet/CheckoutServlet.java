package Servlet;

import Controller.CheckoutDAO;
import Controller.CartDAO;
import Model.CartBean;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "CheckoutServlet", urlPatterns = {"/CheckoutServlet"})
public class CheckoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("uName");
        if (username == null) {
            session.setAttribute("message", "Please log in to proceed with the checkout.");
            response.sendRedirect("signin.jsp");
            return;
        }

        String[] selectedItemsParam = request.getParameterValues("selectedItems");
        if (selectedItemsParam == null || selectedItemsParam.length == 0) {
            session.setAttribute("message", "No items selected for checkout.");
            response.sendRedirect("cart.jsp");
            return;
        }

        Map<String, Integer> selectedItemQuantities = new HashMap<>();
        double totalPrice = 0.0;
        CartDAO cartDAO = new CartDAO();
        for (String itemId : selectedItemsParam) {
            String[] idStrings = itemId.split(",");
            for (String idString : idStrings) {
                try {
                    int id = Integer.parseInt(idString.trim());
                    CartBean item = cartDAO.getCartItemById(id);
                    if (item != null) {
                        selectedItemQuantities.put(item.getAlbumName(), item.getQuantity());
                        totalPrice += item.getAlbumPrice() * item.getQuantity();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    session.setAttribute("message", "An error occurred while fetching cart items. Please try again.");
                    response.sendRedirect("error.jsp");
                    return;
                }
            }
        }
        
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String address = request.getParameter("address");
        String city = request.getParameter("city");
        String postalCode = request.getParameter("postalCode");
        String paymentMethod = request.getParameter("paymentMethod");

        CheckoutDAO checkoutDAO = new CheckoutDAO();
        
        // Set expiry time to 2 minutes from now
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 2);
        Timestamp expiryTime = new Timestamp(calendar.getTimeInMillis());
        boolean orderPlaced;
        try {
            orderPlaced = checkoutDAO.placeOrder(username, name, email, address, city, postalCode, totalPrice, paymentMethod, selectedItemQuantities, expiryTime);
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message", "An error occurred while processing your order. Please try again.");
            response.sendRedirect("error.jsp");
            return;
        }

        if (orderPlaced) {
            try {
                cartDAO.clearCart(selectedItemQuantities);
            } catch (SQLException e) {
                e.printStackTrace();
                session.setAttribute("message", "Order placed, but failed to clear the cart.");
                response.sendRedirect("checkout.jsp");
                return;
            }
            session.setAttribute("message", "Your order has been successfully placed!");
        } else {
            session.setAttribute("message", "There was an issue with your order. Please try again.");
        }
        response.sendRedirect("TranshistoryServlet");
    }
}
