// SearchServlet.java
package Servlet;

import Controller.AlbumDAO;
import Model.AlbumBean;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "SearchServlet", urlPatterns = {"/SearchServlet"})
public class SearchServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String query = request.getParameter("query");
        String action = request.getParameter("action");

        AlbumDAO albumDAO = new AlbumDAO();
        List<AlbumBean> searchResults = null;

        if ("suggest".equals(action)) {
            searchResults = albumDAO.searchAlbumsStartingWith(query);
        } else {
            searchResults = albumDAO.searchAlbums(query); // Use the existing search method for full search
        }

        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            if ("suggest".equals(action)) {
                for (AlbumBean album : searchResults) {
                    out.println("<div class='suggestion-item' onclick='selectSuggestion(\"" + album.getName() + "\")'>" + album.getName() + "</div>");
                }
            } else {
                request.setAttribute("searchResults", searchResults);
                request.getRequestDispatcher("searchResult.jsp").forward(request, response);
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
        return "Search Servlet";
    }
}