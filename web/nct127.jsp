<%@ page import="Controller.AlbumDAO, Model.AlbumBean, java.sql.Connection, java.util.List" %>
<%@ page import="javax.servlet.http.HttpSession" %>
<%@ page import="DB.DBConnection" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    DBConnection dbInstance = new DBConnection();
    Connection connection = dbInstance.getConnection();
    AlbumDAO albumDAO = new AlbumDAO();
    List<AlbumBean> albums = albumDAO.getAllAlbums();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link rel="shortcut icon" href="img/logonct.png" type="image/x-icon">
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:ital,wght@0,100..900;1,100..900&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="css/style.css">
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <style>
       @import url('https://fonts.googleapis.com/css2?family=Zen+Maru+Gothic&display=swap');
            /* Menghilangkan margin dan padding pada body dan html */
            body, html {
                font-family: Zen Mathu Ghotic;
                margin: 0;
                padding: 0;
                background-color: #FFFFEF;
            }
        .album-detail img {
            width: 140px;
        }
        #modal-img {
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
    </style>
    <title>NCT 127</title>
</head>
<body>
    <%@ include file="header.jsp" %>

    <section>
        <div id="head-fantasy">
            <h2 class="heading">
                <span>NCT 127</span>
            </h2>
            <p class="subUnit-description">NCT 127 is a sub-unit of the South Korean boy band NCT, formed by SM Entertainment. The sub-unit focuses on the global market and has released several albums that have been successful internationally..</p>
        </div>
        <div class="album-details-container">
            <% for (AlbumBean album : albums) {
                if (album.getSubUnit().equals("NCT 127")) { %>
                <a class="album-detail <%= album.getStock() <= 0 ? "out-of-stock" : "" %>" href="#" onclick="openModal(event, '<%= album.getId() %>', '<%= album.getName() %>', '<%= album.getPrice() %>', '<%= album.getUnit() %>', '<%= album.getDescription() %>', 'ImageServlet?id=<%= album.getId() %>', '<%= album.getStock() %>')">
                    <img src="ImageServlet?id=<%= album.getId() %>" alt="<%= album.getName() %>">
                    <div class="album-info">
                        <h3><%= album.getName() %></h3>
                        <p>Unit: <%= album.getUnit() %></p>
                        <p><%= album.getDescription() %></p>
                        <p>Stock: <%= album.getStock() %> items</p>
                    </div>
                </a>
            <% } } %>
        </div>
    </section>

    <div id="modal" class="modal">
        <div class="modal-content">
            <span class="close" onclick="closeModal()">&times;</span>
            <img id="modal-img" src="" alt="album Image">
            <h2 id="modal-title">Album Title</h2>
            <p id="modal-author">Author</p>
            <p id="modal-price">Price</p>
            <p id="modal-unit">Unit</p>
            <p id="modal-synopsis">Synopsis</p>
            <p id="modal-stock">Stock album: </p>
            <form id="modal-form" action="<%=request.getContextPath()%>/AddToCartServlet" method="post">
                <input type="hidden" id="modal-album-id" name="albumId">
                <input type="hidden" id="modal-album-name" name="albumName">
                <input type="hidden" id="modal-album-price" name="albumPrice">
                <button type="submit" id="modal-add-to-cart">Add to Cart</button>
            </form>
        </div>
    </div>

    <!-- JavaScript -->
    <script>
        $(document).ready(function(){
            var showCartNotification = '<%= session.getAttribute("showCartNotification") %>';
            if (showCartNotification === 'true') {
                $('#cartModal').modal('show');
                <% session.setAttribute("showCartNotification", false); %>
            }
        });

        function openModal(event, id, title, price, unit, synopsis, imgSrc, stock) {
            event.preventDefault();
            console.log("openModal called with:", id, title, price, unit, synopsis, imgSrc, stock); // Added this line

            if (stock <= 0) {
                console.log("Stock is 0 or less, modal will not open.");
                return; // Prevent modal for out of stock albums
            }

            const modal = document.getElementById('modal');
            if (!modal) {
                console.error("Modal element not found!");
                return;
            }

            document.getElementById('modal-title').innerText = title;
            document.getElementById('modal-price').innerText = 'Rp ' + price;
            document.getElementById('modal-unit').innerText = unit;
            document.getElementById('modal-synopsis').innerText = synopsis;
            document.getElementById('modal-img').src = imgSrc;
            document.getElementById('modal-album-id').value = id;
            document.getElementById('modal-album-name').value = title;
            document.getElementById('modal-album-price').value = price;
            document.getElementById('modal-stock').innerText = 'Stock album: ' + stock + ' items';
            if (stock > 0) {
                document.getElementById('modal-add-to-cart').disabled = false;
            } else {
                document.getElementById('modal-add-to-cart').disabled = true;
            }
            modal.style.display = "block";
            console.log("Modal opened successfully.");
        }

        function closeModal() {
            const modal = document.getElementById('modal');
            if (!modal) {
                console.error("Modal element not found!");
                return;
            }
            modal.style.display = "none";
            console.log("Modal closed successfully.");
        }

        window.onclick = function(event) {
            const modal = document.getElementById('modal');
            if (event.target == modal) {
                modal.style.display = "none";
                console.log("Modal closed by clicking outside.");
            }
        }
    </script>

    <%@ include file="footer.jsp" %>
    <script type="text/javascript" src="javascript/script.js"></script>
</body>
</html>