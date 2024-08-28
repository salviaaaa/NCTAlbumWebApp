<%@ page import="Controller.AlbumDAO, Model.AlbumBean, java.sql.Connection, java.util.List" %>
<%@ page import="javax.servlet.http.HttpSession" %>
<%@ page import="DB.DBConnection" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Search Results</title>
    <link rel="stylesheet" href="css/style.css">
    <link rel="shortcut icon" href="img/logonct.png" type="image/x-icon">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:ital,wght@0,100..900;1,100..900&display=swap" rel="stylesheet">
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <style>
        .search-results {
            padding: 20px;
        }
        .search-results h2 {
            margin-bottom: 20px;
        }
        .results-count {
            font-size: 18px;
            margin-bottom: 20px;
        }
        .results-grid {
            display: flex;
            flex-wrap: wrap;
            gap: 20px;
        }
        .result-item {
            border: 1px solid #ccc;
            border-radius: 8px;
            padding: 10px;
            width: calc(33.333% - 20px);
            box-sizing: border-box;
            text-align: center;
        }
        .result-item img {
            max-width: 30%;
            height: 200px;
            border-radius: 8px;
        }
        .result-item h3 {
            margin: 10px 0;
            font-size: 18px;
        }
        .result-item p {
            margin: 5px 0;
            font-size: 16px;
        }
        .price {
            font-weight: bold;
            color: black;
        }
        #modal-img{
            width: 180px;
            }
            
       .out-of-stock {
            background-color: gray;
            pointer-events: none;
            cursor: not-allowed;
            position: relative;
            opacity: 0.6;
        }
        
        .out-of-stock::after {
            content: "SOLD OUT";
            color: red;
            font-size: 20px;
            font-weight: bold;
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            background-color: rgba(255, 255, 255, 0.8);
            padding: 5px;
        }
        .modal {
            display: none;
            position: fixed;
            z-index: 1001;
            padding-top: 100px;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            overflow: auto;
            background-color: rgb(0,0,0);
            background-color: rgba(0,0,0,0.4);
        }
        .modal-content {
            background-color: #fefefe;
            margin: auto;
            padding: 20px;
            border: 1px solid #888;
            width: 80%;
            max-height: 80vh; /* Make modal scrollable */
            overflow-y: auto; /* Add vertical scrollbar if needed */
        }
        .close {
            color: #aaa;
            float: right;
            font-size: 28px;
            font-weight: bold;
        }
        .close:hover,
        .close:focus {
            color: black;
            text-decoration: none;
            cursor: pointer;
        }
    </style>
    <script>
       function openModal(event, id, title, price, unit, synopsis, imgSrc, stock) {
            event.preventDefault();
            if (stock <= 0) return; // Prevent modal for out of stock albums

            const modal = document.getElementById('modal');
            document.getElementById('modal-title').innerText = title;
            document.getElementById('modal-price').innerText = 'Rp ' + price;
            document.getElementById('modal-unit').innerText = unit;
            document.getElementById('modal-synopsis').innerText = synopsis;
            document.getElementById('modal-img').src = imgSrc;
            document.getElementById('modal-album-id').value = id;
            document.getElementById('modal-album-name').value = title;
            document.getElementById('modal-album-price').value = price;
            document.getElementById('modal-stock').innerText = 'Stock album: ' + stock + ' item';
            if (stock > 0) {
                document.getElementById('modal-add-to-cart').disabled = false;
            } else {
                document.getElementById('modal-add-to-cart').disabled = true;
            }
            modal.style.display = "block";
        }

        function closeModal() {
            document.getElementById('modal').style.display = "none";
        }
    </script>
</head>
<body>
    <%@ include file="header.jsp" %>
    <div class="search-results">
        <h2>Search Results</h2>
        <%
            List<AlbumBean> searchResults = (List<AlbumBean>) request.getAttribute("searchResults");
            if (searchResults != null && !searchResults.isEmpty()) {
        %>
            <div class="results-count">
                <p>1-<%= searchResults.size() %> of <%= searchResults.size() %> results for "<%= request.getParameter("query") %>"</p>
            </div>
            <div class="results-grid">
        <%
                for (AlbumBean album : searchResults) {
        %>
                <div class="result-item <%= album.getStock() <= 0 ? "out-of-stock" : "" %>">
                    <a href="#" onclick="openModal(event, '<%= album.getId() %>', '<%= album.getName() %>', '<%= album.getPrice() %>', '<%= album.getUnit() %>', '<%= album.getDescription() %>', 'ImageServlet?id=<%= album.getId() %>')">
                        <img src="ImageServlet?id=<%= album.getId() %>" alt="<%= album.getName() %>">
                        <h3><%= album.getName() %></h3>
                        <p>Unit: <%= album.getUnit() %></p>
                        <p class="price">Rp <%= album.getPrice() %></p>
                        <% if (album.getStock() > 0) { %>
                            <p>Stock: <%= album.getStock() %> item</p>
                        <% } %>
                    </a>
                </div>
        <%
                }
        %>
            </div>
        <%
            } else {
        %>
            <p>No results found for your search.</p>
        <%
            }
        %>
    </div>

    <div id="modal" class="modal">
        <div class="modal-content">
            <span class="close" onclick="closeModal()">&times;</span>
            <img id="modal-img" src="" alt="album Image">
            <h2 id="modal-title">Album Title</h2>
            <p id="modal-price">Price</p>
            <p id="modal-unit">Unit</p>
            <p id="modal-synopsis">Synopsis</p>
            <form id="modal-form" action="<%=request.getContextPath()%>/AddToCartServlet" method="post">
                <input type="hidden" id="modal-album-id" name="albumId">
                <input type="hidden" id="modal-album-name" name="albumName">
                <input type="hidden" id="modal-album-price" name="albumPrice">
                <button type="submit">Add to Cart</button>
            </form>
        </div>
    </div>

    <%@ include file="footer.jsp" %>
</body>
</html>