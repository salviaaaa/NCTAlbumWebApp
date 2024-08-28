<%-- 
    Document   : allalbum
    Created on : Jul 13, 2024, 1:30:15 AM
    Author     : salvia
--%>
<%@ page import="Controller.AlbumDAO, Model.AlbumBean, java.sql.*, java.util.List" %>
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
        <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="css/style.css">
        <link rel="shortcut icon" href="img/logonct.png" type="image/x-icon">
        <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/js/bootstrap.bundle.min.js"></script>
        <style>
            @import url('https://fonts.googleapis.com/css2?family=Zen+Maru+Gothic&display=swap');
            html, body {
                font-family: 'Zen Maru Gothic', sans-serif;
                background-color: #FFFFEF;
            }
            .album-item img {
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
        <title>NCT albumstore</title>
    </head>
    <body>
        <%@ include file="header.jsp" %>
        <section class="best-seller" id="best-seller">
            <h1 class="heading"><span>All UNIT NCT ALBUM</span></h1>
            <div class="carousel-container" id="carousel-NCT">
                <button class="carousel-button prev" onclick="plusSlidesAllAlbum('carousel-NCT', -1)">‹</button>
                <div class="carousel-slide">
                    <div class="album-list">
                        <% for (AlbumBean album : albums) {
                                if (album.getUnit().equals("NCT")) {%>
                        <div class="album-item <%= album.getStock() <= 0 ? "out-of-stock" : ""%>">
                            <img src="ImageServlet?id=<%= album.getId()%>" alt="<%= album.getName()%>">
                            <h2><%= album.getName()%></h2>
                            <p>Rp <%= album.getPrice()%></p>
                            <p class="Unit">Unit: <%= album.getUnit()%></p>
                            <p>Stock: <%= album.getStock()%> items</p>
                            <form action="<%= request.getContextPath()%>/AddToCartServlet" method="post">
                                <input type="hidden" name="albumId" value="<%= album.getId()%>">
                                <input type="hidden" name="albumName" value="<%= album.getName()%>">
                                <input type="hidden" name="albumPrice" value="<%= album.getPrice()%>">
                                <button type="submit" class="btn btn-primary" <%= album.getStock() <= 0 ? "disabled" : ""%>>Add to Cart</button>
                            </form>
                        </div>
                        <% }
                            }%>
                    </div>
                </div>
                <button class="carousel-button next" onclick="plusSlidesAllAlbum('carousel-NCT', 1)">›</button>
            </div>
            <br>
        </section>

        <!-- JavaScript -->
        <script>
           
            $('form').on('submit', function(event) {
                event.preventDefault(); // Prevent the form from submitting immediately
                console.log("Form submitted");

                var albumId = $(this).find('input[name="albumId"]').val();
                var albumName = $(this).find('input[name="albumName"]').val();
                var albumPrice = $(this).find('input[name="albumPrice"]').val();

                console.log("Album ID: " + albumId);
                console.log("Album Name: " + albumName);
                console.log("Album Price: " + albumPrice);

                // Check if the albumName value is undefined or empty
                if (albumName === undefined || albumName === "") {
                    console.log("Album Name input is not found or empty");
                } else {
                    console.log("Album Name is present: " + albumName);
                }
                
                // Manually submit the form after logging
                this.submit();
            });


            $(document).ready(function () {
                console.log("Document is ready"); // Logging
                var showCartNotification = '<%= session.getAttribute("showCartNotification")%>';
                if (showCartNotification === 'true') {
                    $('#cartModal').modal('show');
            <% session.setAttribute("showCartNotification", false);%>
                }
            });
        </script>
        <script type="text/javascript" src="javascript/script.js"></script>
        <%@ include file="footer.jsp" %>
    </body>
</html>